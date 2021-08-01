package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.cryptography.EncryptionSaltGenerator

sealed class XmlContext {
    abstract val version: FormatVersion

    class Encode(
        override val version: FormatVersion,
        val isXmlExport: Boolean = false
    ) : XmlContext()

    class Decode(
        override val version: FormatVersion,
        val encryption: EncryptionSaltGenerator
    ) : XmlContext()
}
