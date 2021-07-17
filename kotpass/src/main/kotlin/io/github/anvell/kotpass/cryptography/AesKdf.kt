package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.errors.CryptoError
import io.github.anvell.kotpass.extensions.clear
import io.github.anvell.kotpass.extensions.sha256
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

internal object AesKdf {

    fun transformKey(
        key: ByteArray,
        seed: ByteArray,
        rounds: ULong
    ): ByteArray = try {
        val cipher = Cipher.getInstance("AES/ECB/NoPadding")
        val keySpec = SecretKeySpec(seed, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        for (r in 0 until rounds.toLong()) {
            cipher.update(key, 0, 16, key, 0)
            cipher.update(key, 16, 16, key, 16)
        }
        key.sha256().also {
            key.clear()
        }
    } catch (e: NoSuchAlgorithmException) {
        throw CryptoError.AlgorithmUnavailable("AES/ECB encryption is not support in current environment.")
    }
}
