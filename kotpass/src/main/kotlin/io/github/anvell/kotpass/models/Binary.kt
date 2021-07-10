package io.github.anvell.kotpass.models

data class Binary(
    val id: Int,
    val memoryProtection: Boolean,
    val data: BinaryData
) {
    companion object
}
