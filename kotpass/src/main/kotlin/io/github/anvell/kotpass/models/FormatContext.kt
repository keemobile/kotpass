package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.cryptography.EncryptionSaltGenerator

class FormatContext(
    val version: FormatVersion,
    val encryption: EncryptionSaltGenerator,
    val isXmlExport: Boolean = false
)
