package io.github.anvell.kotpass.errors

sealed class CryptoError : Exception() {
    class InvalidDataLength(override val message: String) : CryptoError()
    class MaxBytesExceeded(override val message: String) : CryptoError()
    class AlgorithmUnavailable(override val message: String) : CryptoError()
    class InvalidKey(override val message: String) : CryptoError()
}
