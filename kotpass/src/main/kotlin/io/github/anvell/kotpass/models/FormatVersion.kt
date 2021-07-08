package io.github.anvell.kotpass.models

internal data class FormatVersion(
    val major: Int,
    val minor: Int
) {
    fun isAtLeast(major: Int, minor: Int): Boolean {
        return this.major > major || (this.major == major && this.minor >= minor)
    }
}
