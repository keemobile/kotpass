package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.cryptography.EncryptedValue

sealed class EntryValue {
    abstract val content: String

    class Plain(
        override val content: String
    ) : EntryValue()

    class Encrypted(
        private val value: EncryptedValue
    ) : EntryValue() {
        override val content: String get() = value.text
    }

    inline fun map(block: (String) -> String) = when (this) {
        is Plain -> Plain(block(content))
        is Encrypted -> Encrypted(EncryptedValue.fromString(block(content)))
    }
}
