package io.github.anvell.kotpass.errors

sealed class FormatError : Exception() {
    class InvalidXml(override val message: String) : FormatError()
    class FailedCompression(override val message: String) : FormatError()
}
