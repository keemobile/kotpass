package app.keemobile.kotpass.cryptography

import app.keemobile.kotpass.constants.KdfConst
import app.keemobile.kotpass.database.Credentials
import app.keemobile.kotpass.database.header.DatabaseHeader
import app.keemobile.kotpass.database.header.KdfParameters
import app.keemobile.kotpass.errors.FormatError
import app.keemobile.kotpass.extensions.b
import app.keemobile.kotpass.extensions.clear
import app.keemobile.kotpass.extensions.sha256
import app.keemobile.kotpass.extensions.sha512

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

    fun transformedKey(header: DatabaseHeader, credentials: Credentials): ByteArray {
        return when (header) {
            is DatabaseHeader.Ver3x -> {
                AesKdf.transformKey(
                    key = compositeKey(credentials),
                    seed = header.transformSeed.toByteArray(),
                    rounds = header.transformRounds
                )
            }
            is DatabaseHeader.Ver4x -> {
                when (header.kdfParameters) {
                    is KdfParameters.Aes -> {
                        AesKdf.transformKey(
                            key = compositeKey(credentials),
                            seed = header.kdfParameters.seed.toByteArray(),
                            rounds = header.kdfParameters.rounds
                        )
                    }
                    is KdfParameters.Argon2 -> {
                        Argon2Kdf.transformKey(
                            type = when (header.kdfParameters.uuid) {
                                KdfConst.KdfArgon2d -> Argon2Engine.Type.Argon2D
                                KdfConst.KdfArgon2id -> Argon2Engine.Type.Argon2Id
                                else -> throw FormatError.InvalidHeader(
                                    "Unsupported Kdf UUID (Argon2): ${header.kdfParameters.uuid}"
                                )
                            },
                            version = Argon2Engine.Version.from(header.kdfParameters.version),
                            password = compositeKey(credentials),
                            salt = header.kdfParameters.salt.toByteArray(),
                            secretKey = header.kdfParameters.secretKey?.toByteArray(),
                            additional = header.kdfParameters.associatedData?.toByteArray(),
                            iterations = header.kdfParameters.iterations,
                            parallelism = header.kdfParameters.parallelism,
                            memory = header.kdfParameters.memory
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
