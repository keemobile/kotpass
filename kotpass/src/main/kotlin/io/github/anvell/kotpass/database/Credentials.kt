package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.extensions.sha256
import org.apache.commons.codec.binary.Base16
import org.apache.commons.codec.binary.Base64.decodeBase64

private val KeyDataPattern = Regex("""<Data>(.+)</Data>""")

class Credentials private constructor(
    val passphrase: EncryptedValue?,
    val key: EncryptedValue?
) {

    companion object {
        fun from(passphrase: EncryptedValue) = Credentials(
            passphrase = EncryptedValue.fromBinary(passphrase.getHash()),
            key = null
        )

        fun from(keyData: ByteArray) = Credentials(
            passphrase = null,
            key = EncryptedValue.fromBinary(parse(keyData))
        )

        fun from(passphrase: EncryptedValue, keyData: ByteArray) = Credentials(
            passphrase = EncryptedValue.fromBinary(passphrase.getHash()),
            key = EncryptedValue.fromBinary(parse(keyData))
        )

        // TODO: 16/07/2021 Add proper Xml support
        private fun parse(keyData: ByteArray) = when (keyData.size) {
            32 -> keyData
            64 -> Base16(true).decode(keyData.toString(Charsets.UTF_8).lowercase())
            else ->
                KeyDataPattern
                    .find(keyData.toString(Charsets.UTF_8))
                    ?.groupValues
                    ?.getOrNull(1)
                    ?.let(::decodeBase64)
                    ?: keyData.sha256()
        }
    }
}
