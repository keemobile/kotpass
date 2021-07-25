package io.github.anvell.kotpass.errors

sealed class KeyfileError : Exception() {
    class InvalidVersion : KeyfileError()
    class InvalidHash : KeyfileError()
    class NoKeyData : KeyfileError()
}
