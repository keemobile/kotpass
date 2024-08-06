package app.keemobile.kotpass.cryptography.format

import java.util.UUID

interface CipherProvider {
    val uuid: UUID
    val ivLength: UInt

    fun encrypt(
        key: ByteArray,
        iv: ByteArray,
        data: ByteArray
    ): ByteArray

    fun decrypt(
        key: ByteArray,
        iv: ByteArray,
        data: ByteArray
    ): ByteArray
}
