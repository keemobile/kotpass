package io.github.anvell.kotpass.models

import java.util.*

data class Group(
    val uuid: UUID,
    val name: String,
    val notes: String = "",
    val iconId: Int = DefaultIconId,
    val customIconUuid: UUID? = null,
    val times: TimeData? = null,
    val expanded: Boolean = false,
    val defaultAutoTypeSequence: String? = null,
    val enableAutoType: Boolean? = null,
    val enableSearching: Boolean? = null,
    val lastTopVisibleEntry: UUID? = null,
    val previousParentGroup: UUID? = null,
    val tags: List<String> = listOf(),
    val groups: List<Group> = listOf(),
    val entries: List<Entry> = listOf(),
    val customData: Map<String, CustomDataValue> = mapOf()
) {
    companion object {
        const val DefaultIconId = 49
    }
}
