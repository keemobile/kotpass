package io.github.anvell.kotpass.errors

sealed class FormatError : Exception() {
    class UnknownFormat(override val message: String) : FormatError()
    class UnsupportedVersion(override val message: String) : FormatError()
    class InvalidHeader(override val message: String) : FormatError()
    class InvalidXml(override val message: String) : FormatError()
    class FailedCompression(override val message: String) : FormatError()
}
