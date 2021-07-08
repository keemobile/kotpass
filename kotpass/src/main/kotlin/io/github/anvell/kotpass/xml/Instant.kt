package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.extensions.fromByteArray
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.extensions.toByteArray
import io.github.anvell.kotpass.models.FormatContext
import org.apache.commons.codec.binary.Base64
import org.redundent.kotlin.xml.Node
import java.time.Instant

private const val EpochSecondsFromAD = 62135596800

internal fun Node.getInstant(): Instant? = getText()?.let { text ->
    // Check if ISO text or binary timestamp
    if (text.indexOf(':') > 0) {
        Instant.parse(text)
    } else {
        val seconds = Long.fromByteArray(Base64().decode(text))
        Instant.ofEpochSecond(seconds - EpochSecondsFromAD)
    }
}

internal fun Instant.marshal(context: FormatContext): String {
    val binary = context.version.major >= 4 && !context.isXmlExport

    return if (binary) {
        val seconds = this.epochSecond + EpochSecondsFromAD
        Base64().encodeToString(seconds.toByteArray())
    } else {
        this.toString()
    }
}
