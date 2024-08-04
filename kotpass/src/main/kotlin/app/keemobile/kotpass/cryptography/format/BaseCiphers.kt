package app.keemobile.kotpass.cryptography.format

import app.keemobile.kotpass.cryptography.AesEngine
import app.keemobile.kotpass.cryptography.ChaCha7539Engine
import java.util.UUID

enum class BaseCiphers : CipherProvider {
    Aes {
        override val uuid: UUID = UUID.fromString("31c1f2e6-bf71-4350-be58-05216afc5aff")
        override val ivLength = 16U

        override fun encrypt(
            key: ByteArray,
            iv: ByteArray,
            data: ByteArray
        ): ByteArray = AesEngine.encrypt(key, iv, data)

        override fun decrypt(
            key: ByteArray,
            iv: ByteArray,
            data: ByteArray
        ): ByteArray = AesEngine.decrypt(key, iv, data)
    },

    ChaCha20 {
        override val uuid: UUID = UUID.fromString("d6038a2b-8b6f-4cb5-a524-339a31dbb59a")
        override val ivLength = 12U

        override fun encrypt(
            key: ByteArray,
            iv: ByteArray,
            data: ByteArray
        ): ByteArray = ChaCha7539Engine()
            .apply { init(key, iv) }
            .processBytes(data)

        override fun decrypt(
            key: ByteArray,
            iv: ByteArray,
            data: ByteArray
        ): ByteArray = ChaCha7539Engine()
            .apply { init(key, iv) }
            .processBytes(data)
    }
}
