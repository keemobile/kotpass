package app.keemobile.kotpass.models

import app.keemobile.kotpass.cryptography.EncryptedValue

/**
 * Wraps value fields stored in [Entry].
 */
sealed class EntryValue {
    /**
     * Returns underlying value, decrypting if required.
     */
    abstract val content: String

    /**
     * Should be used for non-sensitive values.
     */
    data class Plain(
        override val content: String
    ) : EntryValue()

    /**
     * Should be used for secrets.
     */
    data class Encrypted(
        private val value: EncryptedValue
    ) : EntryValue() {
        override val content: String get() = value.text
    }

    /**
     * Replaces wrapped value with result of the [block].
     */
    inline fun map(block: (String) -> String) = when (this) {
        is Plain -> Plain(block(content))
        is Encrypted -> Encrypted(EncryptedValue.fromString(block(content)))
    }
}
