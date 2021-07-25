package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.database.header.FileHeaders

internal object ContentEncryption {

    fun encrypt(
        cipherId: FileHeaders.CipherId,
        key: ByteArray,
        iv: ByteArray,
        data: ByteArray
    ) = when (cipherId) {
        FileHeaders.CipherId.ChaCha20 -> {
            ChaCha7539Engine()
                .apply { init(key, iv) }
                .processBytes(data)
        }
        FileHeaders.CipherId.Aes -> {
            AesEngine.encrypt(key, iv, data)
        }
    }

    fun decrypt(
        cipherId: FileHeaders.CipherId,
        key: ByteArray,
        iv: ByteArray,
        data: ByteArray
    ) = when (cipherId) {
        FileHeaders.CipherId.ChaCha20 -> {
            ChaCha7539Engine()
                .apply { init(key, iv) }
                .processBytes(data)
        }
        FileHeaders.CipherId.Aes -> {
            AesEngine.decrypt(key, iv, data)
        }
    }
}
