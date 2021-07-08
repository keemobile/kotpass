package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.extensions.sha256
import io.github.anvell.kotpass.extensions.sha512

private val SalsaNonce = intArrayOf(0xe8, 0x30, 0x09, 0x4b, 0x97, 0x20, 0x5d, 0x2a)
    .map(Int::toByte)
    .toByteArray()

internal sealed class EncryptionSaltGenerator {

    abstract fun getSalt(length: Int): ByteArray

    class Salsa20(key: ByteArray) : EncryptionSaltGenerator() {
        private val engine = Salsa20Engine().apply {
            init(key.sha256(), SalsaNonce)
        }

        override fun getSalt(length: Int) = engine.getBytes(length)
    }

    class ChaCha20(key: ByteArray) : EncryptionSaltGenerator() {
        private val engine = ChaCha7539Engine().apply {
            val hash = key.sha512()
            init(
                key = hash.sliceArray(0 until 32),
                iv = hash.sliceArray(32 until 44)
            )
        }

        override fun getSalt(length: Int) = engine.getBytes(length)
    }
}
