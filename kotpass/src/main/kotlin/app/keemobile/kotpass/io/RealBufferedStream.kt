package app.keemobile.kotpass.io

import okio.Buffer
import okio.ByteString
import okio.Options
import okio.Sink
import okio.Source
import okio.TypedOptions
import okio.buffer
import java.nio.ByteBuffer
import java.nio.charset.Charset

internal class RealBufferedStream(source: Source) : BufferedStream {
    private val bufferedSource = source.buffer()

    override val buffer: Buffer = bufferedSource.buffer

    override fun close() = bufferedSource.close()

    override fun exhausted() = bufferedSource.exhausted()

    override fun indexOf(b: Byte) = bufferedSource.indexOf(b)

    override fun indexOf(b: Byte, fromIndex: Long) = bufferedSource.indexOf(b, fromIndex)

    override fun indexOf(b: Byte, fromIndex: Long, toIndex: Long) =
        bufferedSource.indexOf(b, fromIndex, toIndex)

    override fun indexOf(bytes: ByteString) = bufferedSource.indexOf(bytes)

    override fun indexOf(bytes: ByteString, fromIndex: Long) =
        bufferedSource.indexOf(bytes, fromIndex)

    override fun indexOfElement(targetBytes: ByteString) =
        bufferedSource.indexOfElement(targetBytes)

    override fun indexOfElement(targetBytes: ByteString, fromIndex: Long) =
        bufferedSource.indexOfElement(targetBytes, fromIndex)

    override fun inputStream() = bufferedSource.inputStream()

    override fun isOpen() = bufferedSource.isOpen

    override fun peek() = bufferedSource.peek()

    override fun rangeEquals(offset: Long, bytes: ByteString) =
        bufferedSource.rangeEquals(offset, bytes)

    override fun rangeEquals(
        offset: Long,
        bytes: ByteString,
        bytesOffset: Int,
        byteCount: Int
    ) = bufferedSource.rangeEquals(
        offset,
        bytes,
        bytesOffset,
        byteCount
    )

    override fun read(sink: ByteArray) = bufferedSource.read(sink)

    override fun read(sink: ByteArray, offset: Int, byteCount: Int) = bufferedSource
        .read(sink, offset, byteCount)

    override fun read(sink: Buffer, byteCount: Long) = bufferedSource.read(sink, byteCount)

    override fun read(dst: ByteBuffer?) = bufferedSource.read(dst)

    override fun readAll(sink: Sink): Long = bufferedSource.readAll(sink)

    override fun readByte() = bufferedSource.readByte()

    override fun readByteArray() = bufferedSource.readByteArray()

    override fun readByteArray(byteCount: Long) = bufferedSource.readByteArray(byteCount)

    override fun readByteString() = bufferedSource.readByteString()

    override fun readByteString(byteCount: Long) = bufferedSource.readByteString(byteCount)

    override fun readDecimalLong() = bufferedSource.readDecimalLong()

    override fun readFully(sink: ByteArray) = bufferedSource.readFully(sink)

    override fun readFully(sink: Buffer, byteCount: Long) =
        bufferedSource.readFully(sink, byteCount)

    override fun readHexadecimalUnsignedLong() = bufferedSource.readHexadecimalUnsignedLong()

    override fun readInt() = bufferedSource.readInt()

    override fun readIntLe() = bufferedSource.readIntLe()

    override fun readLong() = bufferedSource.readLong()

    override fun readLongLe() = bufferedSource.readLongLe()

    override fun readShort() = bufferedSource.readShort()

    override fun readShortLe() = bufferedSource.readShortLe()

    override fun readString(charset: Charset) = bufferedSource.readString(charset)

    override fun readString(byteCount: Long, charset: Charset) =
        bufferedSource.readString(byteCount, charset)

    override fun readUtf8() = bufferedSource.readUtf8()

    override fun readUtf8(byteCount: Long) = bufferedSource.readUtf8(byteCount)

    override fun readUtf8CodePoint() = bufferedSource.readUtf8CodePoint()

    override fun readUtf8Line() = bufferedSource.readUtf8Line()

    override fun readUtf8LineStrict() = bufferedSource.readUtf8LineStrict()

    override fun readUtf8LineStrict(limit: Long) = bufferedSource.readUtf8LineStrict(limit)

    override fun request(byteCount: Long) = bufferedSource.request(byteCount)

    override fun require(byteCount: Long) = bufferedSource.require(byteCount)

    override fun select(options: Options) = bufferedSource.select(options)

    override fun <T : Any> select(options: TypedOptions<T>): T? = bufferedSource.select(options)

    override fun skip(byteCount: Long) = bufferedSource.skip(byteCount)

    override fun timeout() = bufferedSource.timeout()
}
