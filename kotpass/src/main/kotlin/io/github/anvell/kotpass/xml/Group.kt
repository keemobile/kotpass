package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.addBoolean
import io.github.anvell.kotpass.extensions.addOptionalBoolean
import io.github.anvell.kotpass.extensions.addUuid
import io.github.anvell.kotpass.extensions.childNodes
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.extensions.getUuid
import io.github.anvell.kotpass.models.FormatContext
import io.github.anvell.kotpass.models.Group
import io.github.anvell.kotpass.xml.FormatXml.Tags
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal fun unmarshalGroup(context: FormatContext, node: Node): Group {
    return Group(
        uuid = node
            .firstOrNull(Tags.Uuid)
            ?.getUuid()
            ?: throw FormatError.InvalidXml("Invalid entry without Uuid."),
        name = node
            .firstOrNull(Tags.Group.Name)
            ?.getText()
            ?: "",
        notes = node
            .firstOrNull(Tags.Group.Notes)
            ?.getText()
            ?: "",
        iconId = node
            .firstOrNull(Tags.Group.IconId)
            ?.getText()
            ?.toInt()
            ?: Group.DefaultIconId,
        customIconUuid = node
            .firstOrNull(Tags.Group.CustomIconId)
            ?.getUuid(),
        times = node
            .firstOrNull(Tags.TimeData.TagName)
            ?.let(::unmarshalTimeData),
        expanded = node
            .firstOrNull(Tags.Group.IsExpanded)
            ?.getText()
            .toBoolean(),
        defaultAutoTypeSequence = node
            .firstOrNull(Tags.Group.DefaultAutoTypeSequence)
            ?.getText(),
        enableAutoType = node
            .firstOrNull(Tags.Group.EnableAutoType)
            ?.getText()
            ?.lowercase()
            ?.toBooleanStrictOrNull(),
        enableSearching = node
            .firstOrNull(Tags.Group.EnableSearching)
            ?.getText()
            ?.lowercase()
            ?.toBooleanStrictOrNull(),
        lastTopVisibleEntry = node
            .firstOrNull(Tags.Group.LastTopVisibleEntry)
            ?.getUuid(),
        previousParentGroup = node
            .firstOrNull(Tags.Group.PreviousParentGroup)
            ?.getUuid(),
        groups = unmarshalGroups(context, node),
        entries = unmarshalEntries(context, node),
        customData = node
            .firstOrNull(Tags.CustomData.TagName)
            ?.let(CustomData::unmarshal)
            ?: mapOf()
    )
}

internal fun unmarshalGroups(
    context: FormatContext,
    node: Node
): List<Group> = node
    .childNodes()
    .filter { it.nodeName == Tags.Group.TagName }
    .map { unmarshalGroup(context, it) }

internal fun Group.marshal(context: FormatContext): Node {
    return node(Tags.Group.TagName) {
        Tags.Uuid { addUuid(uuid) }
        Tags.Group.Name { text(name) }
        Tags.Group.Notes { text(notes) }
        Tags.Group.IconId { text(iconId.toString()) }
        if (customIconUuid != null) {
            Tags.Group.CustomIconId { addUuid(customIconUuid) }
        }
        if (times != null) {
            addNode(times.marshal(context))
        }
        Tags.Group.IsExpanded { addBoolean(expanded) }
        Tags.Group.DefaultAutoTypeSequence { text(defaultAutoTypeSequence ?: "") }
        Tags.Group.EnableAutoType { addOptionalBoolean(enableAutoType) }
        Tags.Group.EnableSearching { addOptionalBoolean(enableSearching) }
        if (lastTopVisibleEntry != null) {
            Tags.Group.LastTopVisibleEntry { addUuid(lastTopVisibleEntry) }
        }
        if (context.version.isAtLeast(4, 1) && previousParentGroup != null) {
            Tags.Group.PreviousParentGroup { addUuid(previousParentGroup) }
        }
        addNode(CustomData.marshal(context, customData))
        groups.forEach { group -> addNode(group.marshal(context)) }
        entries.forEach { entry -> addNode(entry.marshal(context)) }
    }
}
