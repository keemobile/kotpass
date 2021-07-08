package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.models.CustomDataItem
import org.redundent.kotlin.xml.Node

internal fun CustomDataItem.Companion.unmarshal(node: Node): Pair<String, CustomDataItem>? {
    return with(node) {
        val key = firstOrNull(FormatXml.Tags.CustomData.ItemKey)?.getText()
        val value = firstOrNull(FormatXml.Tags.CustomData.ItemValue)?.getText()
        val lastModified = firstOrNull(FormatXml.Tags.TimeData.LastModificationTime)?.getInstant()

        if (key != null && value != null) {
            key to CustomDataItem(value, lastModified)
        } else {
            null
        }
    }
}
