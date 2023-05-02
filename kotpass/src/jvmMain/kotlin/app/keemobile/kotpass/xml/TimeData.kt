package app.keemobile.kotpass.xml

import app.keemobile.kotpass.extensions.addBoolean
import app.keemobile.kotpass.extensions.addDateTime
import app.keemobile.kotpass.extensions.getText
import app.keemobile.kotpass.models.TimeData
import app.keemobile.kotpass.models.XmlContext
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal fun unmarshalTimeData(node: Node): TimeData = with(node) {
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

internal fun TimeData.marshal(context: XmlContext.Encode): Node {
    return node(FormatXml.Tags.TimeData.TagName) {
        FormatXml.Tags.TimeData.CreationTime { addDateTime(context, creationTime) }
        FormatXml.Tags.TimeData.LastAccessTime { addDateTime(context, lastAccessTime) }
        FormatXml.Tags.TimeData.LastModificationTime { addDateTime(context, lastModificationTime) }
        FormatXml.Tags.TimeData.LocationChanged { addDateTime(context, locationChanged) }
        FormatXml.Tags.TimeData.ExpiryTime { addDateTime(context, expiryTime) }
        FormatXml.Tags.TimeData.Expires { addBoolean(expires) }
        FormatXml.Tags.TimeData.UsageCount { text(usageCount.toString()) }
    }
}
