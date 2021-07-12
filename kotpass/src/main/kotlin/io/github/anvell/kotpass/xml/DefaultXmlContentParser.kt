package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.childNodes
import io.github.anvell.kotpass.models.DatabaseContent
import io.github.anvell.kotpass.models.FormatContext
import io.github.anvell.kotpass.xml.FormatXml.Tags
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.XmlVersion
import org.redundent.kotlin.xml.parse
import org.redundent.kotlin.xml.xml
import java.io.ByteArrayInputStream

object DefaultXmlContentParser : XmlContentParser {
    private const val XmlEncoding = "utf-8"

    override fun unmarshalContent(
        context: FormatContext,
        xmlData: ByteArray
    ): DatabaseContent {
        val documentNode = parse(ByteArrayInputStream(xmlData))
        val rootNode = documentNode
            .firstOrNull(Tags.Root)
            ?: throw FormatError.InvalidXml("No root found.")
        val meta = documentNode
            .firstOrNull(Tags.Meta.TagName)
            ?.let(::unmarshalMeta)
            ?: throw FormatError.InvalidXml("No metadata found.")
        val rootGroup = rootNode
            .firstOrNull(Tags.Group.TagName)
            ?.let { unmarshalGroup(context, it) }
            ?: throw FormatError.InvalidXml("No root group.")
        val deletedObjects = rootNode
            .firstOrNull(Tags.DeletedObjects.TagName)
            ?.childNodes()
            ?.filter { it.nodeName == Tags.DeletedObjects.Object }
            ?.mapNotNull(::unmarshalDeletedObject)
            ?: listOf()

        return DatabaseContent(meta, rootGroup, deletedObjects)
    }

    override fun marshalContent(
        context: FormatContext,
        content: DatabaseContent
    ): String {
        return xml(Tags.Document, XmlEncoding, XmlVersion.V10) {
            addNode(content.meta.marshal(context))
            Tags.Root {
                addNode(content.group.marshal(context))
                Tags.DeletedObjects.TagName {
                    content.deletedObjects.forEach {
                        addNode(it.marshal(context))
                    }
                }
            }
        }.toString(PrintOptions(singleLineTextElements = true))
    }
}
