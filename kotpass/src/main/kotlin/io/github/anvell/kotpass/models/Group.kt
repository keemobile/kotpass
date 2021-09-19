package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.constants.PredefinedIcon
import java.util.*

data class Group(
    override val uuid: UUID,
    val name: String,
    val notes: String = "",
    override val icon: PredefinedIcon = PredefinedIcon.Folder,
    val customIconUuid: UUID? = null,
    override val times: TimeData? = TimeData.create(),
    val expanded: Boolean = true,
    val defaultAutoTypeSequence: String? = null,
    val enableAutoType: Boolean? = null,
    val enableSearching: Boolean? = null,
    val lastTopVisibleEntry: UUID? = null,
    val previousParentGroup: UUID? = null,
    override val tags: List<String> = listOf(),
    val entries: List<Entry> = listOf(),
    val groups: List<Group> = listOf(),
    val customData: Map<String, CustomDataValue> = mapOf()
) : DatabaseElement
