package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.addBytes
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.models.Binary
import io.github.anvell.kotpass.models.BinaryData
import org.apache.commons.codec.binary.Base64.decodeBase64
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

/*
* Note: memory protection applies only to binaries stored in inner header (KDBX 4.x)
* */
internal fun Binary.Companion.unmarshal(node: Node): Binary = with(node) {
    val id = get<String?>(FormatXml.Attributes.Id)?.toInt()
        ?: throw FormatError.InvalidXml("Binary node has no id.")
    val text = getText()
        ?: throw FormatError.InvalidXml("Empty body of binary node with id: $id.")
    val compressed = get<String?>(FormatXml.Attributes.Compressed).toBoolean()
    val binaryData = decodeBase64(text).let {
        when {
            compressed -> BinaryData.Compressed(it)
            else -> BinaryData.Uncompressed(it)
        }
    }

    return Binary(id, false, binaryData)
}

internal fun Binary.marshal(): Node {
    return node(FormatXml.Tags.Meta.Binaries.Item) {
        set(FormatXml.Attributes.Id, id)
        set(FormatXml.Attributes.Compressed, data is BinaryData.Compressed)
        addBytes(data.rawContent)
    }
}
