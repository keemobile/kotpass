package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.extensions.sha256
import org.apache.commons.codec.binary.Base64.decodeBase64
import org.apache.commons.codec.binary.Base64.encodeBase64String
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

    fun toBase64(): String = encodeBase64String(getBinary())

    override fun toString(): String = encodeBase64String(value)

    companion object {
        fun fromString(text: String) = fromBinary(text.toByteArray())

        fun fromBase64(base64: String) = fromBinary(decodeBase64(base64))

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
