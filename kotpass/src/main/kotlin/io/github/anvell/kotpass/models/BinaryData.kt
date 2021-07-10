@file:Suppress("MemberVisibilityCanBePrivate")

package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.errors.FormatError
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

sealed class BinaryData {
    abstract val rawContent: ByteArray

    abstract fun getContent(): ByteArray

    class Uncompressed(override val rawContent: ByteArray) : BinaryData() {
        override fun getContent(): ByteArray = rawContent

        fun toCompressed() = try {
            val outputStream = ByteArrayOutputStream()
            GZIPOutputStream(outputStream).use { it.write(getContent()) }
            Compressed(outputStream.toByteArray())
        } catch (e: IOException) {
            throw FormatError.FailedCompression("Failed to gzip binary data.")
        }
    }

    class Compressed(override val rawContent: ByteArray) : BinaryData() {
        override fun getContent(): ByteArray = try {
            GZIPInputStream(ByteArrayInputStream(rawContent))
                .use(GZIPInputStream::readBytes)
        } catch (e: IOException) {
            throw FormatError.FailedCompression("Failed to unzip binary data.")
        }
    }
}
