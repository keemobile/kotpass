package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.extensions.addDateTime
import io.github.anvell.kotpass.models.CustomDataItem
import io.github.anvell.kotpass.models.FormatContext
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal object CustomData {

    fun unmarshal(node: Node): Map<String, CustomDataItem> {
        return node.children
            .filterIsInstance(Node::class.java)
            .filter { it.nodeName == FormatXml.Tags.CustomData.Item }
            .mapNotNull(CustomDataItem.Companion::unmarshal)
            .toMap()
    }

    fun marshal(
        context: FormatContext,
        customData: Map<String, CustomDataItem>
    ): Node = node(FormatXml.Tags.CustomData.Name) {
        for ((key, item) in customData) {
            FormatXml.Tags.CustomData.Item {
                FormatXml.Tags.CustomData.ItemKey { text(key) }
                FormatXml.Tags.CustomData.ItemValue { text(item.value) }

                if (item.lastModified != null &&
                    context.version.isAtLeast(4, 1)
                ) {
                    FormatXml.Tags.TimeData.LastModificationTime {
                        addDateTime(context, item.lastModified)
                    }
                }
            }
        }
    }
}
