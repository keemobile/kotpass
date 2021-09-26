package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.constants.Const
import io.github.anvell.kotpass.constants.PredefinedIcon
import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.*
import io.github.anvell.kotpass.models.Entry
import io.github.anvell.kotpass.models.EntryValue
import io.github.anvell.kotpass.models.XmlContext
import io.github.anvell.kotpass.xml.FormatXml.Tags
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal fun unmarshalEntry(
    context: XmlContext.Decode,
    node: Node
): Entry {
    return Entry(
        uuid = node
            .firstOrNull(Tags.Uuid)
            ?.getUuid()
            ?: throw FormatError.InvalidXml("Invalid entry without Uuid."),
        icon = node
            .firstOrNull(Tags.Entry.IconId)
            ?.getText()
            ?.toInt()
            ?.let(PredefinedIcon.values()::getOrNull)
            ?: PredefinedIcon.Key,
        customIconUuid = node
            .firstOrNull(Tags.Entry.CustomIconId)
            ?.getUuid(),
        foregroundColor = node
            .firstOrNull(Tags.Entry.ForegroundColor)
            ?.getText(),
        backgroundColor = node
            .firstOrNull(Tags.Entry.BackgroundColor)
            ?.getText(),
        overrideUrl = node
            .firstOrNull(Tags.Entry.OverrideUrl)
            ?.getText()
            ?: "",
        times = node
            .firstOrNull(Tags.TimeData.TagName)
            ?.let(::unmarshalTimeData),
        autoType = node
            .firstOrNull(Tags.Entry.AutoType.TagName)
            ?.let(::unmarshalAutoTypeData),
        fields = unmarshalFields(context, node),
        tags = node
            .firstOrNull(Tags.Entry.Tags)
            ?.getText()
            ?.split(Const.TagsSeparatorsRegex)
            ?: listOf(),
        binaries = unmarshalBinaryReferences(context, node),
        history = node
            .firstOrNull(Tags.Entry.History)
            ?.let { unmarshalEntries(context, it) }
            ?: listOf(),
        customData = node
            .firstOrNull(Tags.CustomData.TagName)
            ?.let(CustomData::unmarshal)
            ?: mapOf(),
        previousParentGroup = node
            .firstOrNull(Tags.Entry.PreviousParentGroup)
            ?.getUuid(),
        qualityCheck = node
            .firstOrNull(Tags.Entry.QualityCheck)
            ?.getText()
            ?.toBoolean()
            ?: true,
    )
}

internal fun unmarshalEntries(
    context: XmlContext.Decode,
    node: Node
): List<Entry> = node
    .childNodes()
    .filter { it.nodeName == Tags.Entry.TagName }
    .map { unmarshalEntry(context, it) }

private fun unmarshalFields(
    context: XmlContext.Decode,
    node: Node
): Map<String, EntryValue> = node
    .childNodes()
    .filter { it.nodeName == Tags.Entry.Fields.TagName }
    .associate { fieldNode ->
        val key = fieldNode
            .firstOrNull(Tags.Entry.Fields.ItemKey)
            ?.getText()
            ?: throw FormatError.InvalidXml("Invalid entry field without id.")
        val protected = fieldNode
            .firstOrNull(Tags.Entry.Fields.ItemValue)
            ?.get<String?>(FormatXml.Attributes.Protected)
            .toBoolean()

        if (protected) {
            val bytes = fieldNode
                .firstOrNull(Tags.Entry.Fields.ItemValue)
                ?.getBytes()
                ?: ByteArray(0)
            val salt = context.encryption.getSalt(bytes.size)

            key to EntryValue.Encrypted(EncryptedValue(bytes, salt))
        } else {
            val text = fieldNode
                .firstOrNull(Tags.Entry.Fields.ItemValue)
                ?.getText()
                ?: ""

            key to EntryValue.Plain(text)
        }
    }

internal fun Entry.marshal(context: XmlContext.Encode): Node {
    return node(Tags.Entry.TagName) {
        Tags.Uuid { addUuid(uuid) }
        Tags.Entry.IconId { text(icon.ordinal.toString()) }
        if (customIconUuid != null) {
            Tags.Entry.CustomIconId { addUuid(customIconUuid) }
        }
        Tags.Entry.ForegroundColor { foregroundColor?.let(::text) }
        Tags.Entry.BackgroundColor { backgroundColor?.let(::text) }
        Tags.Entry.OverrideUrl { text(overrideUrl) }
        Tags.Entry.Tags { text(tags.joinToString(Const.TagsSeparator)) }
        if (context.version.isAtLeast(4, 1)) {
            Tags.Entry.QualityCheck { addBoolean(qualityCheck) }
        }
        if (context.version.isAtLeast(4, 1) && previousParentGroup != null) {
            Tags.Entry.PreviousParentGroup { addUuid(previousParentGroup) }
        }
        if (times != null) {
            addNode(times.marshal(context))
        }
        marshalFields(context, fields).forEach {
            addNode(it)
        }
        binaries.forEach {
            addNode(it.marshal(context))
        }
        if (customData.isNotEmpty()) {
            addNode(CustomData.marshal(context, customData))
        }
        if (autoType != null) {
            addNode(autoType.marshal())
        }
        if (history.isNotEmpty()) {
            Tags.Entry.History {
                history.forEach { addNode(it.marshal(context)) }
            }
        }
    }
}

private fun marshalFields(
    context: XmlContext.Encode,
    fields: Map<String, EntryValue>
): List<Node> {
    return fields.map { (key, value) ->
        node(Tags.Entry.Fields.TagName) {
            Tags.Entry.Fields.ItemKey { text(key) }
            Tags.Entry.Fields.ItemValue {
                val isProtected = value is EntryValue.Encrypted

                when {
                    isProtected && context.isXmlExport -> {
                        attribute(
                            FormatXml.Attributes.ProtectedInMemPlainXml,
                            isProtected.toXmlString()
                        )
                        text(value.content)
                    }
                    isProtected -> {
                        val encryptedContent = context
                            .encryption
                            .processBytes(value.content.toByteArray())

                        attribute(
                            FormatXml.Attributes.Protected,
                            isProtected.toXmlString()
                        )
                        addBytes(encryptedContent)
                    }
                    else -> {
                        text(value.content)
                    }
                }
            }
        }
    }
}
