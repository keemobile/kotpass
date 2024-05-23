package app.keemobile.kotpass.database.header

import app.keemobile.kotpass.constants.KdfConst
import app.keemobile.kotpass.errors.FormatError
import app.keemobile.kotpass.extensions.b
import okio.ByteString

/**
 * Describes key-derivation function parameters.
 */
sealed class KdfParameters {
    /**
     * Used to identify KDF in [DatabaseHeader]. The following KDFs
     * are supported by KeePass format by default:
     *
     * ```properties
     * AES-KDF  C9:D9:F3:9A:62:8A:44:60:BF:74:0D:08:C1:8A:4F:EA
     * Argon2d  EF:63:6D:DF:8C:29:44:4B:91:F7:A9:A4:03:E3:0A:0C
     * Argon2id 9E:29:8B:19:56:DB:47:73:B2:3D:FC:3E:C6:F0:A1:E6
     */
    internal abstract val uuid: ByteString

    /**
     * Uses AES as key-derivation function.
     *
     * @property rounds How many times to hash the data.
     * @property seed Used as AES seed.
     */
    data class Aes(
        val rounds: ULong,
        val seed: ByteString
    ) : KdfParameters() {
        override val uuid = Uuid

        internal companion object {
            val Uuid = ByteString.of(
                0xC9.b, 0xD9.b, 0xF3.b, 0x9A.b, 0x62, 0x8A.b, 0x44, 0x60,
                0xBF.b, 0x74, 0x0D, 0x08, 0xC1.b, 0x8A.b, 0x4F, 0xEA.b
            )
        }
    }

    /**
     * Uses Argon2 as key-derivation function.
     *
     * @property variant of Argon2 which is being used.
     * @property salt [ByteString] of salt to be used by the algorithm.
     * @property parallelism The number of threads (or lanes) used by the algorithm.
     * @property memory The amount of memory used by the algorithm (in bytes).
     * @property iterations The number of passes over the memory.
     * @property version Which algorithm version to use (0x10 or 0x13).
     * @property secretKey Not used in KDBX format.
     * @property associatedData Not used in KDBX format.
     */
    data class Argon2(
        val variant: Variant,
        val salt: ByteString,
        val parallelism: UInt,
        val memory: ULong,
        val iterations: ULong,
        val version: UInt,
        val secretKey: ByteString?,
        val associatedData: ByteString?
    ) : KdfParameters() {
        override val uuid = variant.uuid

        enum class Variant(internal val uuid: ByteString) {
            Argon2d(
                ByteString.of(
                    0xEF.b, 0x63, 0x6D, 0xDF.b, 0x8C.b, 0x29, 0x44, 0x4B, 0x91.b,
                    0xF7.b, 0xA9.b, 0xA4.b, 0x03, 0xE3.b, 0x0A, 0x0C
                )
            ),
            Argon2id(
                ByteString.of(
                    0x9E.b, 0x29, 0x8B.b, 0x19, 0x56, 0xDB.b, 0x47, 0x73, 0xB2.b,
                    0x3D, 0xFC.b, 0x3E, 0xC6.b, 0xF0.b, 0xA1.b, 0xE6.b
                )
            );

            internal companion object {
                val Uuids = entries.map(Variant::uuid)

                fun from(uuid: ByteString) = entries.first { it.uuid == uuid }
            }
        }
    }

    /**
     * Encodes [KdfParameters] as [VariantDictionary] to [ByteString].
     */
    internal fun writeToByteString(): ByteString {
        val items = when (this) {
            is Aes -> mapOf(
                KdfConst.Keys.Uuid to VariantItem.Bytes(uuid),
                KdfConst.Keys.Rounds to VariantItem.UInt64(rounds),
                KdfConst.Keys.SaltOrSeed to VariantItem.Bytes(seed)
            )
            is Argon2 -> buildMap {
                put(KdfConst.Keys.Uuid, VariantItem.Bytes(uuid))
                put(KdfConst.Keys.SaltOrSeed, VariantItem.Bytes(salt))
                put(KdfConst.Keys.Parallelism, VariantItem.UInt32(parallelism))
                put(KdfConst.Keys.Memory, VariantItem.UInt64(memory))
                put(KdfConst.Keys.Iterations, VariantItem.UInt64(iterations))
                put(KdfConst.Keys.Version, VariantItem.UInt32(version))
                if (secretKey != null) {
                    put(KdfConst.Keys.SecretKey, VariantItem.Bytes(secretKey))
                }
                if (associatedData != null) {
                    put(KdfConst.Keys.AssocData, VariantItem.Bytes(associatedData))
                }
            }
        }
        return VariantDictionary.writeToByteString(items)
    }

    companion object {
        /**
         * Reads [KdfParameters] encoded as [VariantDictionary] from [data].
         *
         * @throws FormatError.InvalidHeader if any of required values is missing.
         */
        internal fun readFrom(data: ByteString) = with(VariantDictionary.readFrom(data)) {
            val uuid = (get(KdfConst.Keys.Uuid) as? VariantItem.Bytes)?.value
                ?: throw FormatError.InvalidHeader("No KDF UUID found.")

            when (uuid) {
                Aes.Uuid -> {
                    Aes(
                        rounds = (get(KdfConst.Keys.Rounds) as? VariantItem.UInt64)?.value
                            ?: throw FormatError.InvalidHeader("No KDF rounds found."),
                        seed = (get(KdfConst.Keys.SaltOrSeed) as? VariantItem.Bytes)?.value
                            ?: throw FormatError.InvalidHeader("No KDF seed found.")
                    )
                }
                in Argon2.Variant.Uuids -> {
                    Argon2(
                        variant = Argon2.Variant.from(uuid),
                        salt = (get(KdfConst.Keys.SaltOrSeed) as? VariantItem.Bytes)?.value
                            ?: throw FormatError.InvalidHeader("No KDF salt found."),
                        parallelism = (get(KdfConst.Keys.Parallelism) as? VariantItem.UInt32)?.value
                            ?: throw FormatError.InvalidHeader("No KDF parallelism found."),
                        memory = (get(KdfConst.Keys.Memory) as? VariantItem.UInt64)?.value
                            ?: throw FormatError.InvalidHeader("No KDF memory found."),
                        iterations = (get(KdfConst.Keys.Iterations) as? VariantItem.UInt64)?.value
                            ?: throw FormatError.InvalidHeader("No KDF iterations found."),
                        version = (get(KdfConst.Keys.Version) as? VariantItem.UInt32)?.value
                            ?: throw FormatError.InvalidHeader("No KDF version found."),
                        secretKey = (get(KdfConst.Keys.SecretKey) as? VariantItem.Bytes)?.value,
                        associatedData = (get(KdfConst.Keys.AssocData) as? VariantItem.Bytes)?.value
                    )
                }
                else -> throw FormatError.InvalidHeader("Unknown KDF UUID.")
            }
        }
    }
}
