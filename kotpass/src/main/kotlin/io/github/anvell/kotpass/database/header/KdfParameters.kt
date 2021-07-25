@file:Suppress("unused")

package io.github.anvell.kotpass.database.header

import io.github.anvell.kotpass.constants.KdfConst
import io.github.anvell.kotpass.errors.FormatError
import okio.ByteString

sealed class KdfParameters {
    abstract val uuid: ByteString

    data class Aes(
        override val uuid: ByteString,
        val rounds: ULong,
        val seed: ByteString
    ) : KdfParameters()

    data class Argon2(
        override val uuid: ByteString,
        val salt: ByteString,
        val parallelism: UInt,
        val memory: ULong,
        val iterations: ULong,
        val version: UInt,
        val secretKey: ByteString?,
        val associatedData: ByteString?,
    ) : KdfParameters()

    @OptIn(ExperimentalStdlibApi::class)
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
