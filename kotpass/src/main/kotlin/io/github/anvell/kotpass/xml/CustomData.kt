package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.extensions.addDateTime
import io.github.anvell.kotpass.extensions.childNodes
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.models.CustomDataValue
import io.github.anvell.kotpass.models.FormatContext
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal object CustomData {

    fun unmarshal(node: Node): Map<String, CustomDataValue> {
        return node
            .childNodes()
            .filter { it.nodeName == FormatXml.Tags.CustomData.Item }
            .mapNotNull(::unmarshalCustomDataItem)
            .toMap()
    }

    private fun unmarshalCustomDataItem(node: Node): Pair<String, CustomDataValue>? {
        val key = node
            .firstOrNull(FormatXml.Tags.CustomData.ItemKey)
            ?.getText()
            ?: return null
        val value = node
            .firstOrNull(FormatXml.Tags.CustomData.ItemValue)
            ?.getText()
            ?: return null
        val lastModified = node
            .firstOrNull(FormatXml.Tags.TimeData.LastModificationTime)
            ?.getInstant()

        return key to CustomDataValue(value, lastModified)
    }

    fun marshal(
        context: FormatContext,
        customData: Map<String, CustomDataValue>
    ): Node = node(FormatXml.Tags.CustomData.TagName) {
        for ((key, item) in customData) {
            FormatXml.Tags.CustomData.Item {
                FormatXml.Tags.CustomData.ItemKey { text(key) }
                FormatXml.Tags.CustomData.ItemValue { text(item.value) }

                if (context.version.isAtLeast(4, 1)) {
                    FormatXml.Tags.TimeData.LastModificationTime {
                        addDateTime(context, item.lastModified)
                    }
                }
            }
        }
    }
}
