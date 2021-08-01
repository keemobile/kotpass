package io.github.anvell.kotpass.extensions

import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.io.encodeBase64
import io.github.anvell.kotpass.models.XmlContext
import io.github.anvell.kotpass.xml.FormatXml
import io.github.anvell.kotpass.xml.marshal
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.TextElement
import java.nio.ByteBuffer
import java.time.Instant
import java.util.*

internal fun Node.childNodes() = children.filterIsInstance(Node::class.java)

internal fun Node.getText() = (children.firstOrNull() as? TextElement)?.text

internal fun Node.getUuid(): UUID? = getText()?.let { text ->
    val bytes = text.decodeBase64ToArray()
    val byteBuffer = ByteBuffer.wrap(bytes)
    val mostSigBits = byteBuffer.long
    val leastSigBits = byteBuffer.long
    UUID(mostSigBits, leastSigBits)
}

internal fun Node.getBytes(): ByteArray? {
    return getText()?.decodeBase64ToArray()
}

internal fun Node.addDateTime(
    context: XmlContext.Encode,
    instant: Instant?
) {
    if (instant != null) {
        text(instant.marshal(context))
    }
}

internal fun Node.addBoolean(value: Boolean) {
    text(if (value) FormatXml.Values.True else FormatXml.Values.False)
}

internal fun Node.addOptionalBoolean(value: Boolean?) {
    text(
        when (value) {
            true -> FormatXml.Values.True
            false -> FormatXml.Values.False
            else -> FormatXml.Values.Null
        }
    )
}

internal fun Node.addUuid(value: UUID) {
    val buffer = ByteBuffer.allocate(16).apply {
        putLong(value.mostSignificantBits)
        putLong(value.leastSignificantBits)
    }
    text(buffer.array().encodeBase64())
}

internal fun Node.addBytes(bytes: ByteArray) {
    text(bytes.encodeBase64())
}
