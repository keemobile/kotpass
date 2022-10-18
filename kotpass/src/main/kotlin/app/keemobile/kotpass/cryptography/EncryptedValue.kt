package app.keemobile.kotpass.cryptography

import app.keemobile.kotpass.extensions.sha256
import app.keemobile.kotpass.io.decodeBase64ToArray
import app.keemobile.kotpass.io.encodeBase64
import java.security.SecureRandom
import kotlin.experimental.xor

class EncryptedValue(
    private val value: ByteArray,
    private val salt: ByteArray
) {
    val byteLength: Int get() = value.size

    val text: String get() = getBinary().toString(Charsets.UTF_8)

    fun getHash() = getBinary().sha256()

    fun getBinary(): ByteArray {
        val bytes = ByteArray(value.size)

        for (i in bytes.indices) {
            bytes[i] = value[i] xor salt[i]
        }
        return bytes
    }

    fun setSalt(newSalt: ByteArray) {
        for (i in value.indices) {
            value[i] = (value[i] xor salt[i]) xor newSalt[i]
            salt[i] = newSalt[i]
        }
    }

    fun toBase64(): String = getBinary().encodeBase64()

    override fun toString(): String = value.encodeBase64()

    companion object {
        fun fromString(text: String) = fromBinary(text.toByteArray())

        fun fromBase64(base64: String) = fromBinary(base64.decodeBase64ToArray())

        fun fromBinary(bytes: ByteArray): EncryptedValue {
            val salt = ByteArray(bytes.size)
            SecureRandom().nextBytes(salt)

            for (i in bytes.indices) {
                bytes[i] = bytes[i] xor salt[i]
            }
            return EncryptedValue(bytes, salt)
        }
    }
}
