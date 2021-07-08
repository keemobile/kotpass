package io.github.anvell.kotpass.errors

sealed class StreamCipherError : Exception() {
    class DataLengthException(
        override val message: String? = null
    ) : StreamCipherError()

    class OutputLengthException(
        override val message: String? = null
    ) : StreamCipherError()

    class MaxBytesExceededException(
        override val message: String? = null
    ) : StreamCipherError()
}
