package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.extensions.addBytes
import io.github.anvell.kotpass.extensions.addDateTime
import io.github.anvell.kotpass.extensions.addUuid
import io.github.anvell.kotpass.extensions.childNodes
import io.github.anvell.kotpass.extensions.getBytes
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.extensions.getUuid
import io.github.anvell.kotpass.models.CustomIcon
import io.github.anvell.kotpass.models.XmlContext
import io.github.anvell.kotpass.xml.FormatXml.Tags
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node
import java.util.*

internal object CustomIcons {

    fun unmarshal(node: Node): Map<UUID, CustomIcon> {
        return node
            .childNodes()
            .filter { it.nodeName == Tags.Meta.CustomIcons.Item }
            .mapNotNull(::unmarshalCustomIcon)
            .toMap()
    }

    private fun unmarshalCustomIcon(node: Node): Pair<UUID, CustomIcon>? {
        val id = node
            .firstOrNull(Tags.Meta.CustomIcons.ItemUuid)
            ?.getUuid()
            ?: return null
        val data = node
            .firstOrNull(Tags.Meta.CustomIcons.ItemData)
            ?.getBytes()
            ?: return null
        val name = node
            .firstOrNull(Tags.Meta.CustomIcons.ItemName)
            ?.getText()
        val lastModified = node
            .firstOrNull(Tags.TimeData.LastModificationTime)
            ?.getInstant()

        return id to CustomIcon(data, name, lastModified)
    }

    fun marshal(
        context: XmlContext.Encode,
        customIcons: Map<UUID, CustomIcon>
    ): Node = node(Tags.Meta.CustomIcons.TagName) {
        for ((key, item) in customIcons) {
            Tags.Meta.CustomIcons.Item {
                Tags.Meta.CustomIcons.ItemUuid { addUuid(key) }
                Tags.Meta.CustomIcons.ItemData { addBytes(item.data) }

                if (context.version.isAtLeast(4, 1)) {
                    Tags.Meta.CustomIcons.ItemName {
                        item.name?.let(this::text)
                    }
                    Tags.TimeData.LastModificationTime {
                        addDateTime(context, item.lastModified)
                    }
                }
            }
        }
    }
}
