package io.github.anvell.kotpass.models

import java.util.*

data class Entry(
    val uuid: UUID,
    val iconId: Int = 0,
    val customIconUuid: UUID? = null,
    val foregroundColor: String? = null,
    val backgroundColor: String? = null,
    val overrideUrl: String = "",
    val times: TimeData? = null,
    val autoType: AutoTypeData? = null,
    val fields: Map<String, EntryValue> = mapOf(),
    val tags: List<String> = listOf(),
    val binaries: List<BinaryReference> = listOf(),
    val history: List<Entry> = listOf(),
    val customData: Map<String, CustomDataValue> = mapOf(),
    val previousParentGroup: UUID? = null,
    val qualityCheck: Boolean = true
) {
    companion object {
        fun create() = Entry(
            uuid = UUID.randomUUID(),
            times = TimeData.create()
        )
    }
}
