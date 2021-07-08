package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.extensions.addBoolean
import io.github.anvell.kotpass.extensions.addDateTime
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.models.FormatContext
import io.github.anvell.kotpass.models.TimeData
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal fun TimeData.Companion.unmarshal(node: Node): TimeData = with(node) {
    return TimeData(
        creationTime = firstOrNull(FormatXml.Tags.TimeData.CreationTime)?.getInstant(),
        lastAccessTime = firstOrNull(FormatXml.Tags.TimeData.LastAccessTime)?.getInstant(),
        lastModificationTime = firstOrNull(FormatXml.Tags.TimeData.LastModificationTime)?.getInstant(),
        locationChanged = firstOrNull(FormatXml.Tags.TimeData.LocationChanged)?.getInstant(),
        expiryTime = firstOrNull(FormatXml.Tags.TimeData.ExpiryTime)?.getInstant(),
        expires = firstOrNull(FormatXml.Tags.TimeData.Expires)?.getText()?.toBoolean() ?: false,
        usageCount = firstOrNull(FormatXml.Tags.TimeData.UsageCount)?.getText()?.toInt() ?: 0
    )
}

internal fun TimeData.marshal(context: FormatContext): Node {
    return node(FormatXml.Tags.TimeData.Name) {
        FormatXml.Tags.TimeData.CreationTime { addDateTime(context, creationTime) }
        FormatXml.Tags.TimeData.LastAccessTime { addDateTime(context, lastAccessTime) }
        FormatXml.Tags.TimeData.LastModificationTime { addDateTime(context, lastModificationTime) }
        FormatXml.Tags.TimeData.LocationChanged { addDateTime(context, locationChanged) }
        FormatXml.Tags.TimeData.ExpiryTime { addDateTime(context, expiryTime) }
        FormatXml.Tags.TimeData.Expires { addBoolean(expires) }
        FormatXml.Tags.TimeData.UsageCount { text(usageCount.toString()) }
    }
}
