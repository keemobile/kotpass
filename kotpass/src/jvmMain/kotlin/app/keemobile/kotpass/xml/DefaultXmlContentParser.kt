package app.keemobile.kotpass.xml

import app.keemobile.kotpass.errors.FormatError
import app.keemobile.kotpass.extensions.childNodes
import app.keemobile.kotpass.models.DatabaseContent
import app.keemobile.kotpass.models.Meta
import app.keemobile.kotpass.models.XmlContext
import app.keemobile.kotpass.xml.FormatXml.Tags
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.XmlVersion
import org.redundent.kotlin.xml.parse
import org.redundent.kotlin.xml.xml
import java.io.ByteArrayInputStream
import java.io.InputStream

object DefaultXmlContentParser : XmlContentParser {
    private const val XmlEncoding = "utf-8"

    override fun unmarshalContent(
        xmlData: ByteArray,
        contextBlock: (Meta) -> XmlContext.Decode
    ) = unmarshalContent(ByteArrayInputStream(xmlData), contextBlock)

    override fun unmarshalContent(
        source: InputStream,
        contextBlock: (Meta) -> XmlContext.Decode
    ): DatabaseContent {
        val documentNode = parse(source)
        val rootNode = documentNode
            .firstOrNull(Tags.Root)
            ?: throw FormatError.InvalidXml("No root found.")
        val meta = documentNode
            .firstOrNull(Tags.Meta.TagName)
            ?.let(::unmarshalMeta)
            ?: throw FormatError.InvalidXml("No metadata found.")
        val rootGroup = rootNode
            .firstOrNull(Tags.Group.TagName)
            ?.let { unmarshalGroup(contextBlock(meta), it) }
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
        context: XmlContext.Encode,
        content: DatabaseContent,
        pretty: Boolean
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
        }.toString(
            PrintOptions(
                pretty = pretty,
                singleLineTextElements = true
            )
        )
    }
}
