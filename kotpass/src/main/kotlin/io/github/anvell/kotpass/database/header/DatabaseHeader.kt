package io.github.anvell.kotpass.database.header

import io.github.anvell.kotpass.models.FormatVersion
import okio.BufferedSink
import okio.BufferedSource

class DatabaseHeader(
    val signature: Signature,
    val version: FormatVersion,
    val fileHeaders: FileHeaders
) {

    internal fun writeTo(sink: BufferedSink) = with(sink) {
        signature.writeTo(sink)
        writeShortLe(version.minor.toInt())
        writeShortLe(version.major.toInt())
        fileHeaders.writeTo(sink)
    }

    companion object {
        internal fun readFrom(source: BufferedSource): DatabaseHeader {
            val signature = Signature.readFrom(source)
            val version = FormatVersion(
                minor = source.readShortLe(),
                major = source.readShortLe()
            )
            val fileHeaders = FileHeaders.readFrom(source, version)

            return DatabaseHeader(
                signature = signature,
                version = version,
                fileHeaders = fileHeaders
            )
        }
    }
}
