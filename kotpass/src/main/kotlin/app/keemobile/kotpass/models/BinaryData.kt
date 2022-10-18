@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package app.keemobile.kotpass.models

import app.keemobile.kotpass.errors.FormatError
import app.keemobile.kotpass.extensions.sha256
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

sealed class BinaryData(val hash: ByteString) {
    abstract val memoryProtection: Boolean
    abstract val rawContent: ByteArray

    abstract fun getContent(): ByteArray

    class Uncompressed(
        override val memoryProtection: Boolean,
        override val rawContent: ByteArray
    ) : BinaryData(rawContent.sha256().toByteString()) {

        override fun getContent(): ByteArray = rawContent

        fun toCompressed() = try {
            val outputStream = ByteArrayOutputStream()
            GZIPOutputStream(outputStream).use { it.write(getContent()) }
            Compressed(memoryProtection, outputStream.toByteArray())
        } catch (e: IOException) {
            throw FormatError.FailedCompression("Failed to gzip binary data.")
        }
    }

    class Compressed(
        override val memoryProtection: Boolean,
        override val rawContent: ByteArray
    ) : BinaryData(rawContent.sha256().toByteString()) {

        override fun getContent(): ByteArray = try {
            GZIPInputStream(ByteArrayInputStream(rawContent))
                .use(GZIPInputStream::readBytes)
        } catch (e: IOException) {
            throw FormatError.FailedCompression("Failed to unzip binary data.")
        }
    }
}
