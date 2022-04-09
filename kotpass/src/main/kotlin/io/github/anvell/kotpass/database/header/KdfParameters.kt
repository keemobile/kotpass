package io.github.anvell.kotpass.database.header

import io.github.anvell.kotpass.constants.KdfConst
import io.github.anvell.kotpass.errors.FormatError
import okio.ByteString

/**
 * Describes key-derivation function parameters
 */
sealed class KdfParameters {
    abstract val uuid: ByteString

    /**
     * Uses AES as key-derivation function.
     *
     * @property uuid Used to identify KDF in [DatabaseHeader].
     * @property rounds How many times to hash the data.
     * @property seed Used as AES seed.
     */
    data class Aes(
        override val uuid: ByteString,
        val rounds: ULong,
        val seed: ByteString
    ) : KdfParameters()

    /**
     * Uses Argon2 as key-derivation function.
     *
     * @property uuid Used to identify KDF in [DatabaseHeader].
     * @property salt [ByteString] of salt to be used by the algorithm.
     * @property parallelism The number of threads (or lanes) used by the algorithm.
     * @property memory The amount of memory used by the algorithm (in bytes).
     * @property iterations The number of passes over the memory.
     * @property version Which algorithm version to use (0x10 or 0x13).
     * @property secretKey Not used in KDBX format.
     * @property associatedData Not used in KDBX format.
     */
    data class Argon2(
        override val uuid: ByteString,
        val salt: ByteString,
        val parallelism: UInt,
        val memory: ULong,
        val iterations: ULong,
        val version: UInt,
        val secretKey: ByteString?,
        val associatedData: ByteString?
    ) : KdfParameters()

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
                KdfConst.KdfAes -> {
                    Aes(
                        uuid = uuid,
                        rounds = (get(KdfConst.Keys.Rounds) as? VariantItem.UInt64)?.value
                            ?: throw FormatError.InvalidHeader("No KDF rounds found."),
                        seed = (get(KdfConst.Keys.SaltOrSeed) as? VariantItem.Bytes)?.value
                            ?: throw FormatError.InvalidHeader("No KDF seed found."),
                    )
                }
                KdfConst.KdfArgon2d, KdfConst.KdfArgon2id -> {
                    Argon2(
                        uuid = (get(KdfConst.Keys.Uuid) as? VariantItem.Bytes)?.value
                            ?: throw FormatError.InvalidHeader("No KDF uuid found."),
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
                        associatedData = (get(KdfConst.Keys.AssocData) as? VariantItem.Bytes)?.value,
                    )
                }
                else -> throw FormatError.InvalidHeader("Unknown KDF UUID.")
            }
        }
    }
}
