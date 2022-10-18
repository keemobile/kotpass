package app.keemobile.kotpass.models

import okio.BufferedSink
import okio.BufferedSource

data class FormatVersion(
    val major: Short,
    val minor: Short
) {
    fun isAtLeast(major: Short, minor: Short): Boolean {
        return this.major > major || (this.major == major && this.minor >= minor)
    }

    internal fun writeTo(sink: BufferedSink) = with(sink) {
        writeShortLe(minor.toInt())
        writeShortLe(major.toInt())
    }

    companion object {
        internal fun readFrom(source: BufferedSource) = FormatVersion(
            minor = source.readShortLe(),
            major = source.readShortLe()
        )
    }
}
