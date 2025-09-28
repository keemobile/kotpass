package app.keemobile.kotpass.cryptography.format

import app.keemobile.kotpass.errors.CryptoError
import app.keemobile.kotpass.extensions.clear
import app.keemobile.kotpass.extensions.sha256
import java.security.GeneralSecurityException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

internal object AesKdf {
    fun transformKey(
        key: ByteArray,
        seed: ByteArray,
        rounds: ULong
    ): ByteArray {
        val bytes = key.copyOf()

        return try {
            val cipher = Cipher.getInstance("AES/ECB/NoPadding")
            val keySpec = SecretKeySpec(seed, "AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)

            repeat(rounds.toInt()) {
                cipher.update(bytes, 0, 16, bytes, 0)
                cipher.update(bytes, 16, 16, bytes, 16)
            }
            bytes.sha256()
        } catch (error: GeneralSecurityException) {
            if (error is NoSuchAlgorithmException) {
                throw CryptoError.AlgorithmUnavailable("AES/ECB encryption is not supported in current environment.")
            } else {
                throw CryptoError.InvalidKey("Wrong KDF seed used for decryption.")
            }
        } finally {
            bytes.clear()
        }
    }
}
