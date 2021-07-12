package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.childNodes
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.models.BinaryReference
import io.github.anvell.kotpass.xml.FormatXml.Tags
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal fun unmarshalBinaryReferences(node: Node): List<BinaryReference> {
    return node
        .childNodes()
        .filter { it.nodeName == Tags.Entry.BinaryReferences.TagName }
        .map {
            BinaryReference(
                id = it.firstOrNull(Tags.Entry.BinaryReferences.ItemValue)
                    ?.get<String?>(FormatXml.Attributes.Ref)
                    ?.toInt()
                    ?: throw FormatError.InvalidXml("Invalid binary reference id."),
                name = it.firstOrNull(Tags.Entry.BinaryReferences.ItemKey)
                    ?.getText()
                    ?: throw FormatError.InvalidXml("Invalid binary reference key.")
            )
        }
}

internal fun BinaryReference.marshal(): Node {
    return node(Tags.Entry.BinaryReferences.TagName) {
        Tags.Entry.BinaryReferences.ItemKey {
            text(name)
        }
        Tags.Entry.BinaryReferences.ItemValue {
            set(FormatXml.Attributes.Ref, id)
        }
    }
}
