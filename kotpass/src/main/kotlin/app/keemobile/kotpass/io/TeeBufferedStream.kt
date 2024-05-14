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

internal class TeeBufferedStream(
    source: Source,
    private val mirrorBuffer: Buffer
) : BufferedStream {
    private val bufferedSource = source.buffer()

    override val buffer: Buffer = bufferedSource.buffer

    override fun close() = bufferedSource.close()

    override fun exhausted() = bufferedSource.exhausted()

    override fun indexOf(b: Byte) = bufferedSource.indexOf(b)

    override fun indexOf(b: Byte, fromIndex: Long) =
        bufferedSource.indexOf(b, fromIndex)

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

    override fun read(sink: ByteArray) = bufferedSource
        .read(sink)
        .also { if (it > 0) mirrorBuffer.write(sink) }

    override fun read(sink: ByteArray, offset: Int, byteCount: Int) = bufferedSource
        .read(sink, offset, byteCount)
        .also { if (it > 0) mirrorBuffer.write(sink) }

    override fun read(sink: Buffer, byteCount: Long) = bufferedSource
        .read(sink, byteCount).also {
            if (it > 0) sink.copyTo(mirrorBuffer, sink.size - byteCount, byteCount)
        }

    override fun read(dst: ByteBuffer?) = bufferedSource
        .read(dst)
        .also {
            if (dst != null && it > 0) {
                val len = dst.array().size
                mirrorBuffer.write(dst.array().sliceArray(len - it until len))
            }
        }

    override fun readAll(sink: Sink): Long {
        val temp = Buffer()
        val byteCount = bufferedSource.readAll(temp)
        if (byteCount > 0) {
            sink.write(temp, byteCount)
            mirrorBuffer.write(temp, byteCount)
        }
        return byteCount
    }

    override fun readByte() = bufferedSource
        .readByte()
        .also { mirrorBuffer.writeByte(it.toInt()) }

    override fun readByteArray() = bufferedSource
        .readByteArray()
        .also(mirrorBuffer::write)

    override fun readByteArray(byteCount: Long) = bufferedSource
        .readByteArray(byteCount)
        .also(mirrorBuffer::write)

    override fun readByteString() = bufferedSource
        .readByteString()
        .also(mirrorBuffer::write)

    override fun readByteString(byteCount: Long) = bufferedSource
        .readByteString(byteCount)
        .also(mirrorBuffer::write)

    override fun readDecimalLong() = bufferedSource
        .readDecimalLong()
        .also(mirrorBuffer::writeDecimalLong)

    override fun readFully(sink: ByteArray) = bufferedSource
        .readFully(sink)
        .also { mirrorBuffer.write(sink) }

    override fun readFully(sink: Buffer, byteCount: Long) = bufferedSource
        .readFully(sink, byteCount)
        .also { sink.copyTo(mirrorBuffer, sink.size - byteCount, byteCount) }

    override fun readHexadecimalUnsignedLong() = bufferedSource
        .readHexadecimalUnsignedLong()
        .also(mirrorBuffer::writeHexadecimalUnsignedLong)

    override fun readInt() = bufferedSource
        .readInt()
        .also(mirrorBuffer::writeInt)

    override fun readIntLe() = bufferedSource
        .readIntLe()
        .also(mirrorBuffer::writeIntLe)

    override fun readLong() = bufferedSource
        .readLong()
        .also(mirrorBuffer::writeLong)

    override fun readLongLe() = bufferedSource
        .readLongLe()
        .also(mirrorBuffer::writeLongLe)

    override fun readShort() = bufferedSource
        .readShort()
        .also { mirrorBuffer.writeShort(it.toInt()) }

    override fun readShortLe() = bufferedSource
        .readShortLe()
        .also { mirrorBuffer.writeShortLe(it.toInt()) }

    override fun readString(charset: Charset) = bufferedSource
        .readString(charset)
        .also { mirrorBuffer.writeString(it, charset) }

    override fun readString(byteCount: Long, charset: Charset) = bufferedSource
        .readString(byteCount, charset)
        .also { mirrorBuffer.writeString(it, charset) }

    override fun readUtf8() = bufferedSource
        .readUtf8()
        .also(mirrorBuffer::writeUtf8)

    override fun readUtf8(byteCount: Long) = bufferedSource
        .readUtf8(byteCount)
        .also(mirrorBuffer::writeUtf8)

    override fun readUtf8CodePoint() = bufferedSource
        .readUtf8CodePoint()
        .also(mirrorBuffer::writeUtf8CodePoint)

    override fun readUtf8Line() = bufferedSource
        .readUtf8Line()
        ?.also(mirrorBuffer::writeUtf8)

    override fun readUtf8LineStrict() = bufferedSource
        .readUtf8LineStrict()
        .also(mirrorBuffer::writeUtf8)

    override fun readUtf8LineStrict(limit: Long) = bufferedSource
        .readUtf8LineStrict(limit)
        .also(mirrorBuffer::writeUtf8)

    override fun request(byteCount: Long) = bufferedSource.request(byteCount)

    override fun require(byteCount: Long) = bufferedSource.require(byteCount)

    override fun select(options: Options) = bufferedSource.select(options)

    override fun <T : Any> select(options: TypedOptions<T>): T? = bufferedSource.select(options)

    override fun skip(byteCount: Long) = bufferedSource.skip(byteCount)

    override fun timeout() = bufferedSource.timeout()
}
