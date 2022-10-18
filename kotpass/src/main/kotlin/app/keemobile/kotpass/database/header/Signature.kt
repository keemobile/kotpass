package app.keemobile.kotpass.database.header

import app.keemobile.kotpass.extensions.b
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString

class Signature(
    val base: ByteString,
    val secondary: ByteString,
) {

    internal fun writeTo(sink: BufferedSink) = with(sink) {
        write(base)
        write(secondary)
    }

    companion object {
        val Base = ByteString.of(0x03, 0xd9.b, 0xa2.b, 0x9a.b)
        val Secondary = ByteString.of(0x67, 0xfb.b, 0x4b, 0xb5.b)
        val Default = Signature(Base, Secondary)

        internal fun readFrom(source: BufferedSource) = Signature(
            base = source.readByteString(4),
            secondary = source.readByteString(4)
        )
    }
}
