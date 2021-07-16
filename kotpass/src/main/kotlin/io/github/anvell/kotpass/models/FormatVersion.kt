package io.github.anvell.kotpass.models

data class FormatVersion(
    val major: Short,
    val minor: Short
) {
    fun isAtLeast(major: Short, minor: Short): Boolean {
        return this.major > major || (this.major == major && this.minor >= minor)
    }
}
