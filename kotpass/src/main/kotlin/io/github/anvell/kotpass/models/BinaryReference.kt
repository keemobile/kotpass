package io.github.anvell.kotpass.models

import okio.ByteString

data class BinaryReference(
    val hash: ByteString,
    val name: String
)
