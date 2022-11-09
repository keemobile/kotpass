package app.keemobile.kotpass.builders

import app.keemobile.kotpass.constants.PredefinedIcon
import app.keemobile.kotpass.models.*
import java.util.*

internal class MutableEntry(
    var uuid: UUID,
    var icon: PredefinedIcon = PredefinedIcon.Key,
    var customIconUuid: UUID? = null,
    var foregroundColor: String? = null,
    var backgroundColor: String? = null,
    var overrideUrl: String = "",
    var times: TimeData? = null,
    var autoType: AutoTypeData? = null,
    var fields: MutableMap<String, EntryValue> = mutableMapOf(),
    var tags: MutableList<String> = mutableListOf(),
    var binaries: MutableList<BinaryReference> = mutableListOf(),
    var history: MutableList<Entry> = mutableListOf(),
    var customData: MutableMap<String, CustomDataValue> = mutableMapOf(),
    var previousParentGroup: UUID? = null,
    var qualityCheck: Boolean = true
)

internal inline fun buildEntry(
    uuid: UUID,
    crossinline block: MutableEntry.() -> Unit
): Entry = MutableEntry(uuid)
    .apply(block)
    .run {
        Entry(
            uuid = uuid,
            icon = icon,
            customIconUuid = customIconUuid,
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor,
            overrideUrl = overrideUrl,
            times = times,
            autoType = autoType,
            fields = EntryFields(fields),
            tags = tags,
            binaries = binaries,
            history = history,
            customData = customData,
            previousParentGroup = previousParentGroup,
            qualityCheck = qualityCheck
        )
    }
