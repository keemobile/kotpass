package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.header.KdfParameters
import io.github.anvell.kotpass.extensions.nextByteString
import java.security.SecureRandom

fun KeePassDatabase.regenerateVectors(
    random: SecureRandom = SecureRandom()
) = when (this) {
    is KeePassDatabase.Ver3x -> {
        copy(
            header = header.copy(
                masterSeed = random.nextByteString(32),
                encryptionIV = random.nextByteString(header.cipherId.ivLength),
                transformSeed = random.nextByteString(32),
                innerRandomStreamKey = random.nextByteString(32),
                streamStartBytes = random.nextByteString(32)
            )
        )
    }
    is KeePassDatabase.Ver4x -> {
        copy(
            header = header.copy(
                masterSeed = random.nextByteString(32),
                encryptionIV = random.nextByteString(header.cipherId.ivLength),
                kdfParameters = when (header.kdfParameters) {
                    is KdfParameters.Aes ->
                        header
                            .kdfParameters
                            .copy(seed = random.nextByteString(32))
                    is KdfParameters.Argon2 ->
                        header
                            .kdfParameters
                            .copy(salt = random.nextByteString(32))
                }
            ),
            innerHeader = innerHeader.copy(
                randomStreamKey = random.nextByteString(64)
            )
        )
    }
}
