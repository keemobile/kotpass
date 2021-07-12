package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.cryptography.EncryptionSaltGenerator

internal class FormatContext(
    val version: FormatVersion,
    val encryption: EncryptionSaltGenerator,
    val isXmlExport: Boolean = false
)
