package io.github.anvell.kotpass.database.header

import io.github.anvell.kotpass.constants.CrsAlgorithm
import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.b
import io.github.anvell.kotpass.extensions.nextByteString
import io.github.anvell.kotpass.models.Binary
import io.github.anvell.kotpass.models.BinaryData
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString
import java.security.SecureRandom
import java.util.*

private object InnerHeaderFieldId {
    const val Terminator = 0x00
    const val StreamId = 0x01
    const val StreamKey = 0x02
    const val Binary = 0x03
}

data class DatabaseInnerHeader(
    val randomStreamId: CrsAlgorithm,
    val randomStreamKey: ByteString,
    val binaries: List<Binary>
) {

    internal fun writeTo(sink: BufferedSink) = with(sink) {
        writeByte(InnerHeaderFieldId.StreamId)
        writeIntLe(Int.SIZE_BYTES)
        writeIntLe(randomStreamId.ordinal)

        writeByte(InnerHeaderFieldId.StreamKey)
        writeIntLe(randomStreamKey.size)
        write(randomStreamKey)

        for (binary in binaries) {
            val data = binary.data.getContent()
            writeByte(InnerHeaderFieldId.Binary)
            writeIntLe(data.size + 1)
            writeByte(if (binary.memoryProtection) 0x1 else 0x0)
            write(data)
        }

        writeByte(InnerHeaderFieldId.Terminator)
        writeIntLe(0)
    }

    companion object {
        fun create() = with(SecureRandom()) {
            DatabaseInnerHeader(
                randomStreamId = CrsAlgorithm.ChaCha20,
                randomStreamKey = nextByteString(64),
                binaries = listOf()
            )
        }

        internal fun readFrom(source: BufferedSource): DatabaseInnerHeader {
            val binaries = mutableListOf<Binary>()
            var randomStreamId: CrsAlgorithm? = null
            var randomStreamKey: ByteString? = null
            var binaryCount = 0

            while (true) {
                val id = source.readByte()
                val length = source.readIntLe().toLong()

                when (id.toInt()) {
                    InnerHeaderFieldId.Terminator -> {
                        source.readByteArray(length)
                        break
                    }
                    InnerHeaderFieldId.StreamId -> {
                        randomStreamId = CrsAlgorithm.values()[source.readIntLe()]
                    }
                    InnerHeaderFieldId.StreamKey -> {
                        randomStreamKey = source.readByteString(length)
                    }
                    InnerHeaderFieldId.Binary -> {
                        val protection = source.readByte() != 0x0.b
                        val content = source.readByteArray(length - 1)
                        val binary = Binary(
                            id = binaryCount,
                            memoryProtection = protection,
                            data = BinaryData.Uncompressed(content)
                        )
                        binaries.add(binary)
                        binaryCount++
                    }
                    else -> throw FormatError.InvalidContent("Unknown inner header id: $id.")
                }
            }

            return DatabaseInnerHeader(
                randomStreamId = randomStreamId
                    ?: throw FormatError.InvalidContent("No random stream id found in inner header"),
                randomStreamKey = randomStreamKey
                    ?: throw FormatError.InvalidContent("No random stream key found in inner header"),
                binaries = binaries
            )
        }
    }
}
