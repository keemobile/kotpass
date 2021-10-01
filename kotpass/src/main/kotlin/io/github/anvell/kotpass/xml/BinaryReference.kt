package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.models.BinaryReference
import io.github.anvell.kotpass.models.XmlContext
import io.github.anvell.kotpass.xml.FormatXml.Tags
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal fun unmarshalBinaryReference(
    context: XmlContext.Decode,
    node: Node
): BinaryReference {
    val id = node
        .firstOrNull(Tags.Entry.BinaryReferences.ItemValue)
        ?.get<String?>(FormatXml.Attributes.Ref)
        ?.toInt()
        ?: throw FormatError.InvalidXml("Invalid binary reference id.")

    return BinaryReference(
        hash = context.binaries.keys.elementAt(id),
        name = node
            .firstOrNull(Tags.Entry.BinaryReferences.ItemKey)
            ?.getText()
            ?: throw FormatError.InvalidXml("Invalid binary reference key.")
    )
}

internal fun BinaryReference.marshal(
    context: XmlContext.Encode
): Node {
    val id = context.binaries.keys.indexOf(hash)
    if (id == -1) {
        throw FormatError.InvalidContent("No binary with hash: ${hash.hex()}.")
    }

    return node(Tags.Entry.BinaryReferences.TagName) {
        Tags.Entry.BinaryReferences.ItemKey {
            text(name)
        }
        Tags.Entry.BinaryReferences.ItemValue {
            attribute(FormatXml.Attributes.Ref, id.toString())
        }
    }
}
