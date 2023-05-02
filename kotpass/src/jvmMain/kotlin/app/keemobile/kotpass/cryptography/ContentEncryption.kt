package app.keemobile.kotpass.cryptography

import app.keemobile.kotpass.database.header.DatabaseHeader

internal object ContentEncryption {

    fun encrypt(
        cipherId: DatabaseHeader.CipherId,
        key: ByteArray,
        iv: ByteArray,
        data: ByteArray
    ) = when (cipherId) {
        DatabaseHeader.CipherId.ChaCha20 -> {
            ChaCha7539Engine()
                .apply { init(key, iv) }
                .processBytes(data)
        }
        DatabaseHeader.CipherId.Aes -> {
            AesEngine.encrypt(key, iv, data)
        }
    }

    fun decrypt(
        cipherId: DatabaseHeader.CipherId,
        key: ByteArray,
        iv: ByteArray,
        data: ByteArray
    ) = when (cipherId) {
        DatabaseHeader.CipherId.ChaCha20 -> {
            ChaCha7539Engine()
                .apply { init(key, iv) }
                .processBytes(data)
        }
        DatabaseHeader.CipherId.Aes -> {
            AesEngine.decrypt(key, iv, data)
        }
    }
}
