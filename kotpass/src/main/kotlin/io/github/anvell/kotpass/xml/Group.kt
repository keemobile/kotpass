package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.builders.buildGroup
import io.github.anvell.kotpass.constants.Const
import io.github.anvell.kotpass.constants.PredefinedIcon
import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.*
import io.github.anvell.kotpass.models.Group
import io.github.anvell.kotpass.models.XmlContext
import io.github.anvell.kotpass.xml.FormatXml.Tags
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal fun unmarshalGroup(
    context: XmlContext.Decode,
    node: Node
): Group {
    val uuid = node
        .firstOrNull(Tags.Uuid)
        ?.getUuid()
        ?: throw FormatError.InvalidXml("Invalid entry without Uuid.")

    return buildGroup(uuid) {
        for (childNode in node.childNodes()) {
            when (childNode.nodeName) {
                Tags.Group.Name -> {
                    name = childNode.getText() ?: ""
                }
                Tags.Group.Notes -> {
                    notes = childNode.getText() ?: ""
                }
                Tags.Group.IconId -> {
                    icon = childNode.getText()
                        ?.toInt()
                        ?.let(PredefinedIcon.values()::getOrNull)
                        ?: PredefinedIcon.Folder
                }
                Tags.Group.CustomIconId -> {
                    customIconUuid = childNode.getUuid()
                }
                Tags.TimeData.TagName -> {
                    times = unmarshalTimeData(childNode)
                }
                Tags.Group.IsExpanded -> {
                    expanded = childNode.getText().toBoolean()
                }
                Tags.Group.DefaultAutoTypeSequence -> {
                    defaultAutoTypeSequence = childNode.getText()
                }
                Tags.Group.EnableAutoType -> {
                    enableAutoType = childNode.getGroupOverride()
                }
                Tags.Group.EnableSearching -> {
                    enableSearching = childNode.getGroupOverride()
                }
                Tags.Group.LastTopVisibleEntry -> {
                    lastTopVisibleEntry = childNode.getUuid()
                }
                Tags.Group.PreviousParentGroup -> {
                    previousParentGroup = childNode.getUuid()
                }
                Tags.Group.Tags -> {
                    childNode
                        .getText()
                        ?.split(Const.TagsSeparatorsRegex)
                        ?.forEach(tags::add)
                }
                Tags.Group.TagName -> {
                    groups.add(unmarshalGroup(context, childNode))
                }
                Tags.Entry.TagName -> {
                    entries.add(unmarshalEntry(context, childNode))
                }
                Tags.CustomData.TagName -> {
                    customData = CustomData.unmarshal(childNode).toMutableMap()
                }
            }
        }
    }
}

internal fun Group.marshal(
    context: XmlContext.Encode
): Node = node(Tags.Group.TagName) {
    Tags.Uuid { addUuid(uuid) }
    Tags.Group.Name { text(name) }
    Tags.Group.Notes { text(notes) }
    Tags.Group.IconId { text(icon.ordinal.toString()) }
    if (customIconUuid != null) {
        Tags.Group.CustomIconId { addUuid(customIconUuid) }
    }
    if (times != null) {
        addNode(times.marshal(context))
    }
    Tags.Group.IsExpanded { addBoolean(expanded) }
    Tags.Group.DefaultAutoTypeSequence { text(defaultAutoTypeSequence ?: "") }
    Tags.Group.EnableAutoType { addGroupOverride(enableAutoType) }
    Tags.Group.EnableSearching { addGroupOverride(enableSearching) }
    if (lastTopVisibleEntry != null) {
        Tags.Group.LastTopVisibleEntry { addUuid(lastTopVisibleEntry) }
    }
    if (context.version.isAtLeast(4, 1) && previousParentGroup != null) {
        Tags.Group.PreviousParentGroup { addUuid(previousParentGroup) }
    }
    if (context.version.isAtLeast(4, 1)) {
        Tags.Group.Tags { text(tags.joinToString(Const.TagsSeparator)) }
    }
    if (customData.isNotEmpty()) {
        addNode(CustomData.marshal(context, customData))
    }
    groups.forEach { group -> addNode(group.marshal(context)) }
    entries.forEach { entry -> addNode(entry.marshal(context)) }
}
