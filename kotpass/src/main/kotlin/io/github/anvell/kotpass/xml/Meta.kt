package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.constants.BasicFields
import io.github.anvell.kotpass.constants.Defaults
import io.github.anvell.kotpass.extensions.addBoolean
import io.github.anvell.kotpass.extensions.addBytes
import io.github.anvell.kotpass.extensions.addDateTime
import io.github.anvell.kotpass.extensions.addUuid
import io.github.anvell.kotpass.extensions.getBytes
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.extensions.getUuid
import io.github.anvell.kotpass.models.Meta
import io.github.anvell.kotpass.models.XmlContext
import io.github.anvell.kotpass.xml.FormatXml.Tags
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.node

internal fun unmarshalMeta(node: Node): Meta {
    return Meta(
        generator = node
            .firstOrNull(Tags.Meta.Generator)
            ?.getText()
            ?: Defaults.Generator,
        headerHash = node
            .firstOrNull(Tags.Meta.HeaderHash)
            ?.getBytes(),
        settingsChanged = node
            .firstOrNull(Tags.Meta.SettingsChanged)
            ?.getInstant(),
        name = node
            .firstOrNull(Tags.Meta.DatabaseName)
            ?.getText() ?: "",
        nameChanged = node
            .firstOrNull(Tags.Meta.DatabaseNameChanged)
            ?.getInstant(),
        description = node
            .firstOrNull(Tags.Meta.DatabaseDescription)
            ?.getText() ?: "",
        descriptionChanged = node
            .firstOrNull(Tags.Meta.DatabaseDescriptionChanged)
            ?.getInstant(),
        defaultUser = node
            .firstOrNull(Tags.Meta.DefaultUserName)
            ?.getText() ?: "",
        defaultUserChanged = node
            .firstOrNull(Tags.Meta.DefaultUserNameChanged)
            ?.getInstant(),
        maintenanceHistoryDays = node
            .firstOrNull(Tags.Meta.MaintenanceHistoryDays)
            ?.getText()
            ?.toInt()
            ?: Defaults.MaintenanceHistoryDays,
        color = node
            .firstOrNull(Tags.Meta.Color)
            ?.getText(),
        masterKeyChanged = node
            .firstOrNull(Tags.Meta.MasterKeyChanged)
            ?.getInstant(),
        masterKeyChangeRec = node
            .firstOrNull(Tags.Meta.MasterKeyChangeRec)
            ?.getText()
            ?.toInt() ?: -1,
        masterKeyChangeForce = node
            .firstOrNull(Tags.Meta.MasterKeyChangeForce)
            ?.getText()
            ?.toInt() ?: -1,
        recycleBinEnabled = node
            .firstOrNull(Tags.Meta.RecycleBinEnabled)
            ?.getText().toBoolean(),
        recycleBinUuid = node
            .firstOrNull(Tags.Meta.RecycleBinUuid)
            ?.getUuid(),
        recycleBinChanged = node
            .firstOrNull(Tags.Meta.RecycleBinChanged)
            ?.getInstant(),
        entryTemplatesGroup = node
            .firstOrNull(Tags.Meta.EntryTemplatesGroup)
            ?.getUuid(),
        entryTemplatesGroupChanged = node
            .firstOrNull(Tags.Meta.EntryTemplatesGroupChanged)
            ?.getInstant(),
        historyMaxItems = node
            .firstOrNull(Tags.Meta.HistoryMaxItems)
            ?.getText()?.toInt()
            ?: Defaults.HistoryMaxItems,
        historyMaxSize = node
            .firstOrNull(Tags.Meta.HistoryMaxSize)
            ?.getText()?.toInt()
            ?: Defaults.HistoryMaxSize,
        lastSelectedGroup = node
            .firstOrNull(Tags.Meta.LastSelectedGroup)
            ?.getUuid(),
        lastTopVisibleGroup = node
            .firstOrNull(Tags.Meta.LastTopVisibleGroup)
            ?.getUuid(),
        memoryProtection = node
            .firstOrNull(Tags.Meta.MemoryProtection.TagName)
            ?.let(::unmarshalMemoryProtection) ?: setOf(),
        binaries = node.firstOrNull(Tags.Meta.Binaries.TagName)
            ?.let(::unmarshalBinaries) ?: listOf(),
        customIcons = node.firstOrNull(Tags.Meta.CustomIcons.TagName)
            ?.let(CustomIcons::unmarshal) ?: mapOf(),
        customData = node.firstOrNull(Tags.CustomData.TagName)
            ?.let(CustomData::unmarshal) ?: mapOf()
    )
}

@OptIn(ExperimentalStdlibApi::class)
private fun unmarshalMemoryProtection(node: Node): Set<BasicFields> =
    with(Tags.Meta.MemoryProtection) {
        return buildSet {
            if (node.firstOrNull(ProtectTitle)?.getText().toBoolean()) {
                add(BasicFields.Title)
            }
            if (node.firstOrNull(ProtectUserName)?.getText().toBoolean()) {
                add(BasicFields.UserName)
            }
            if (node.firstOrNull(ProtectPassword)?.getText().toBoolean()) {
                add(BasicFields.Password)
            }
            if (node.firstOrNull(ProtectUrl)?.getText().toBoolean()) {
                add(BasicFields.Url)
            }
            if (node.firstOrNull(ProtectNotes)?.getText().toBoolean()) {
                add(BasicFields.Notes)
            }
        }
    }

internal fun Meta.marshal(context: XmlContext.Encode): Node {
    return node(Tags.Meta.TagName) {
        Tags.Meta.Generator { text(generator) }
        if (context.version.major < 4 && headerHash != null) {
            Tags.Meta.HeaderHash { addBytes(headerHash) }
        }
        if (settingsChanged != null) {
            Tags.Meta.SettingsChanged { addDateTime(context, settingsChanged) }
        }
        Tags.Meta.DatabaseName { text(name) }
        Tags.Meta.DatabaseNameChanged { addDateTime(context, nameChanged) }
        Tags.Meta.DatabaseDescription { text(description) }
        Tags.Meta.DatabaseDescriptionChanged { addDateTime(context, descriptionChanged) }
        Tags.Meta.DefaultUserName { text(defaultUser) }
        Tags.Meta.DefaultUserNameChanged { addDateTime(context, defaultUserChanged) }
        Tags.Meta.MaintenanceHistoryDays { text(maintenanceHistoryDays.toString()) }
        Tags.Meta.Color { color?.let(this::text) }
        Tags.Meta.MasterKeyChanged { addDateTime(context, masterKeyChanged) }
        Tags.Meta.MasterKeyChangeRec { text(masterKeyChangeRec.toString()) }
        Tags.Meta.MasterKeyChangeForce { text(masterKeyChangeForce.toString()) }
        Tags.Meta.RecycleBinEnabled { addBoolean(recycleBinEnabled) }
        Tags.Meta.RecycleBinUuid { recycleBinUuid?.let(this::addUuid) }
        Tags.Meta.RecycleBinChanged { addDateTime(context, recycleBinChanged) }
        Tags.Meta.EntryTemplatesGroup { entryTemplatesGroup?.let(this::addUuid) }
        Tags.Meta.EntryTemplatesGroupChanged { addDateTime(context, entryTemplatesGroupChanged) }
        Tags.Meta.HistoryMaxItems { text(historyMaxItems.toString()) }
        Tags.Meta.HistoryMaxSize { text(historyMaxSize.toString()) }
        Tags.Meta.LastSelectedGroup { lastSelectedGroup?.let(this::addUuid) }
        Tags.Meta.LastTopVisibleGroup { lastTopVisibleGroup?.let(this::addUuid) }
        addNode(marshalMemoryProtection(memoryProtection))
        addNode(CustomIcons.marshal(context, customIcons))
        addNode(CustomData.marshal(context, customData))

        // In version 4.x files are stored in binary inner header
        if (context.version.major < 4) {
            Tags.Meta.Binaries.TagName {
                for (item in binaries) addNode(item.marshal())
            }
        }
    }
}

private fun marshalMemoryProtection(
    memoryProtection: Set<BasicFields>
): Node = node(Tags.Meta.MemoryProtection.TagName) {
    BasicFields
        .values()
        .forEach {
            it.value { addBoolean(memoryProtection.contains(it)) }
        }
}
