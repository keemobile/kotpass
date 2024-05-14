package app.keemobile.kotpass.io

import okio.Buffer
import okio.BufferedSource
import okio.ByteString
import okio.Options
import okio.Sink
import okio.Source
import okio.TypedOptions
import java.io.InputStream
import java.nio.channels.ReadableByteChannel
import java.nio.charset.Charset

interface BufferedStream : Source, ReadableByteChannel {
    val buffer: Buffer

    fun exhausted(): Boolean

    fun require(byteCount: Long)

    fun request(byteCount: Long): Boolean

    fun readByte(): Byte

    fun readShort(): Short

    fun readShortLe(): Short

    fun readInt(): Int

    fun readIntLe(): Int

    fun readLong(): Long

    fun readLongLe(): Long

    fun readDecimalLong(): Long

    fun readHexadecimalUnsignedLong(): Long

    fun skip(byteCount: Long)

    fun readByteString(): ByteString

    fun readByteString(byteCount: Long): ByteString

    fun select(options: Options): Int

    fun <T : Any> select(options: TypedOptions<T>): T?

    fun readByteArray(): ByteArray

    fun readByteArray(byteCount: Long): ByteArray

    fun read(sink: ByteArray): Int

    fun readFully(sink: ByteArray)

    fun read(sink: ByteArray, offset: Int, byteCount: Int): Int

    fun readFully(sink: Buffer, byteCount: Long)

    fun readAll(sink: Sink): Long

    fun readUtf8(): String

    fun readUtf8(byteCount: Long): String

    fun readUtf8Line(): String?

    fun readUtf8LineStrict(): String

    fun readUtf8LineStrict(limit: Long): String

    fun readUtf8CodePoint(): Int

    fun readString(charset: Charset): String

    fun readString(byteCount: Long, charset: Charset): String

    fun indexOf(b: Byte): Long

    fun indexOf(b: Byte, fromIndex: Long): Long

    fun indexOf(b: Byte, fromIndex: Long, toIndex: Long): Long

    fun indexOf(bytes: ByteString): Long

    fun indexOf(bytes: ByteString, fromIndex: Long): Long

    fun indexOfElement(targetBytes: ByteString): Long

    fun indexOfElement(targetBytes: ByteString, fromIndex: Long): Long

    fun rangeEquals(offset: Long, bytes: ByteString): Boolean

    fun rangeEquals(offset: Long, bytes: ByteString, bytesOffset: Int, byteCount: Int): Boolean

    fun peek(): BufferedSource

    fun inputStream(): InputStream
}
