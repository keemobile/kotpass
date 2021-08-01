package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.extensions.fromByteArray
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.extensions.toByteArray
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.io.encodeBase64
import io.github.anvell.kotpass.models.XmlContext
import org.redundent.kotlin.xml.Node
import java.time.Instant

private const val EpochSecondsFromAD = 62135596800

internal fun Node.getInstant(): Instant? = getText()?.let { text ->
    // Check if ISO text or binary timestamp
    if (text.indexOf(':') > 0) {
        Instant.parse(text)
    } else {
        val seconds = Long.fromByteArray(text.decodeBase64ToArray())
        Instant.ofEpochSecond(seconds - EpochSecondsFromAD)
    }
}

internal fun Instant.marshal(context: XmlContext.Encode): String {
    val binary = context.version.major >= 4 && !context.isXmlExport

    return if (binary) {
        (epochSecond + EpochSecondsFromAD).toByteArray().encodeBase64()
    } else {
        this.toString()
    }
}
