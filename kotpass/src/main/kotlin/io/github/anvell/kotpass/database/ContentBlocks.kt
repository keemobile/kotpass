package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.cryptography.intToLittleEndian
import io.github.anvell.kotpass.cryptography.longToLittleEndian
import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.constantTimeEquals
import io.github.anvell.kotpass.extensions.sha256
import io.github.anvell.kotpass.extensions.sha512
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString.Companion.toByteString
import java.lang.Integer.min

internal object ContentBlocks {
    private const val BlockSplitRate = 1048576

    private class Block(
        val index: Long,
        val length: Int,
        val data: ByteArray
    )

    fun readContentBlocksVer3x(source: BufferedSource): ByteArray {
        val contentData = Buffer()

        while (true) {
            val index = source.readIntLe()
            val hash = source.readByteArray(32)
            val length = source.readIntLe().toUInt()

            if (length > 0U) {
                val data = source.readByteArray(length.toLong())
                if (!data.sha256().contentEquals(hash)) {
                    throw FormatError.InvalidContent("Hash for block $index does not match.")
                }
                contentData.write(data)
            } else {
                break
            }
        }

        return contentData.readByteArray()
    }

    fun readContentBlocksVer4x(
        source: BufferedSource,
        masterSeed: ByteArray,
        transformedKey: ByteArray
    ): ByteArray {
        val contentData = Buffer()
        val hmacKey = createBlockHmacKey(masterSeed, transformedKey)
        var index = 0L

        while (true) {
            val blockHMAC = source.readByteArray(32)
            val length = source.readIntLe()
            val data = source.readByteArray(length.toLong())

            if (!createBlockHmac(hmacKey, index, length, data).constantTimeEquals(blockHMAC)) {
                throw FormatError.InvalidContent("HMAC for block $index does not match.")
            }
            contentData.write(data)

            if (length == 0) {
                break
            }
            index++
        }

        return contentData.readByteArray()
    }

    internal fun writeContentBlocksVer3x(
        sink: BufferedSink,
        contentData: ByteArray
    ) = writeContentBlocks(sink, contentData, true) {
        data.sha256()
    }

    internal fun writeContentBlocksVer4x(
        sink: BufferedSink,
        contentData: ByteArray,
        masterSeed: ByteArray,
        transformedKey: ByteArray
    ) {
        val hmacKey = createBlockHmacKey(masterSeed, transformedKey)

        writeContentBlocks(sink, contentData, false) {
            createBlockHmac(hmacKey, index, length, data)
        }
    }

    private fun writeContentBlocks(
        sink: BufferedSink,
        contentData: ByteArray,
        writeIndexes: Boolean,
        hashFunc: Block.() -> ByteArray
    ): Unit = with(sink) {
        var index = 0L
        var offset = 0

        while (offset < contentData.size) {
            val length = min(contentData.size - offset, BlockSplitRate)
            val data = contentData.sliceArray(offset until offset + length)
            val hash = hashFunc(Block(index, length, data))

            if (writeIndexes) {
                writeIntLe(index.toInt())
            }
            write(hash)
            writeIntLe(length)
            write(data)
            index++
            offset += length
        }
        if (writeIndexes) {
            writeIntLe(index.toInt())
        }
        write(hashFunc(Block(index, 0, ByteArray(0))))
        writeIntLe(0)
    }

    private fun createBlockHmacKey(
        masterSeed: ByteArray,
        transformedKey: ByteArray
    ) = byteArrayOf(*masterSeed, *transformedKey, 0x01).sha512()

    private fun createBlockHmac(
        hmacKey: ByteArray,
        index: Long,
        length: Int,
        data: ByteArray
    ): ByteArray {
        val indexBytes = longToLittleEndian(index)
        val blockKey = (indexBytes + hmacKey)
            .toByteString()
            .sha512()

        return (indexBytes + intToLittleEndian(length) + data)
            .toByteString()
            .hmacSha256(blockKey)
            .toByteArray()
    }
}
