package app.keemobile.kotpass.cryptography

import app.keemobile.kotpass.errors.CryptoError
import java.security.GeneralSecurityException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object AesEngine {
    fun encrypt(
        key: ByteArray,
        iv: ByteArray,
        data: ByteArray
    ) = processBytes(Cipher.ENCRYPT_MODE, key, iv, data)

    fun decrypt(
        key: ByteArray,
        iv: ByteArray,
        data: ByteArray
    ) = processBytes(Cipher.DECRYPT_MODE, key, iv, data)

    private fun processBytes(
        mode: Int,
        key: ByteArray,
        iv: ByteArray,
        data: ByteArray
    ): ByteArray = try {
        with(Cipher.getInstance("AES/CBC/PKCS5Padding")) {
            init(mode, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
            doFinal(data)
        }
    } catch (e: GeneralSecurityException) {
        if (e is NoSuchAlgorithmException) {
            throw CryptoError.AlgorithmUnavailable("AES/CBC encryption is not supported in current environment.")
        } else {
            throw CryptoError.InvalidKey("Wrong key used for decryption.")
        }
    }
}
