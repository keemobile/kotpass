package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.addBytes
import io.github.anvell.kotpass.extensions.getBytes
import io.github.anvell.kotpass.models.Binary
import io.github.anvell.kotpass.models.BinaryData
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

/*
* Note: memory protection applies only to binaries stored in inner header (KDBX 4.x)
* */
internal fun Binary.Companion.unmarshal(node: Node): Binary = with(node) {
    val id = get<String?>(FormatXml.Attributes.Id)?.toInt()
        ?: throw FormatError.InvalidXml("Binary node has no id.")
    val bytes = getBytes()
        ?: throw FormatError.InvalidXml("Empty body of binary node with id: $id.")
    val compressed = get<String?>(FormatXml.Attributes.Compressed).toBoolean()

    return Binary(
        id = id,
        memoryProtection = false,
        data = when {
            compressed -> BinaryData.Compressed(bytes)
            else -> BinaryData.Uncompressed(bytes)
        }
    )
}

internal fun Binary.marshal(): Node {
    return node(FormatXml.Tags.Meta.Binaries.Item) {
        set(FormatXml.Attributes.Id, id)
        set(FormatXml.Attributes.Compressed, data is BinaryData.Compressed)
        addBytes(data.rawContent)
    }
}
