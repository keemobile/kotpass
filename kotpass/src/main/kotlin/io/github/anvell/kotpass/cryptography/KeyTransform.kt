package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.constants.KdfConst
import io.github.anvell.kotpass.database.Credentials
import io.github.anvell.kotpass.database.header.FileHeaders
import io.github.anvell.kotpass.database.header.KdfParameters
import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.b
import io.github.anvell.kotpass.extensions.clear
import io.github.anvell.kotpass.extensions.sha256
import io.github.anvell.kotpass.extensions.sha512

internal object KeyTransform {

    fun compositeKey(credentials: Credentials): ByteArray {
        val items = listOfNotNull(
            credentials.passphrase?.getBinary(),
            credentials.key?.getBinary()
        )
        val composite = when {
            items.isNotEmpty() -> items.reduce { a, b -> a + b }
            else -> ByteArray(0)
        }

        return composite
            .sha256()
            .also { composite.clear() }
    }

    fun transformedKey(fileHeaders: FileHeaders, credentials: Credentials): ByteArray {
        return when (fileHeaders) {
            is FileHeaders.Ver3x -> {
                AesKdf.transformKey(
                    key = compositeKey(credentials),
                    seed = fileHeaders.transformSeed.toByteArray(),
                    rounds = fileHeaders.transformRounds
                )
            }
            is FileHeaders.Ver4x -> {
                when (fileHeaders.kdfParameters) {
                    is KdfParameters.Aes -> {
                        AesKdf.transformKey(
                            key = compositeKey(credentials),
                            seed = fileHeaders.kdfParameters.seed.toByteArray(),
                            rounds = fileHeaders.kdfParameters.rounds
                        )
                    }
                    is KdfParameters.Argon2 -> {
                        Argon2Kdf.transformKey(
                            type = when (fileHeaders.kdfParameters.uuid) {
                                KdfConst.KdfArgon2d -> Argon2Engine.Type.Argon2D
                                KdfConst.KdfArgon2id -> Argon2Engine.Type.Argon2Id
                                else -> throw FormatError.InvalidHeader(
                                    "Unsupported Kdf UUID (Argon2): ${fileHeaders.kdfParameters.uuid}"
                                )
                            },
                            version = Argon2Engine.Version.from(fileHeaders.kdfParameters.version),
                            password = compositeKey(credentials),
                            salt = fileHeaders.kdfParameters.salt.toByteArray(),
                            secretKey = fileHeaders.kdfParameters.secretKey?.toByteArray(),
                            additional = fileHeaders.kdfParameters.associatedData?.toByteArray(),
                            iterations = fileHeaders.kdfParameters.iterations,
                            parallelism = fileHeaders.kdfParameters.parallelism,
                            memory = fileHeaders.kdfParameters.memory,
                        )
                    }
                }
            }
        }
    }

    fun masterKey(
        masterSeed: ByteArray,
        transformedKey: ByteArray
    ) = (masterSeed + transformedKey).sha256()

    fun hmacKey(
        masterSeed: ByteArray,
        transformedKey: ByteArray
    ): ByteArray {
        val combined = byteArrayOf(*masterSeed, *transformedKey, 0x01)
        return (ByteArray(8) { 0xFF.b } + combined.sha512())
            .sha512()
            .also { combined.clear() }
    }
}
