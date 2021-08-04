package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.constants.BasicFields
import io.github.anvell.kotpass.constants.Defaults
import okio.ByteString
import java.time.Instant
import java.util.*

data class Meta(
    val generator: String = Defaults.Generator,
    val headerHash: ByteString? = null,
    val settingsChanged: Instant? = Instant.now(),
    val name: String = "",
    val nameChanged: Instant? = Instant.now(),
    val description: String = "",
    val descriptionChanged: Instant? = Instant.now(),
    val defaultUser: String = "",
    val defaultUserChanged: Instant? = Instant.now(),
    val maintenanceHistoryDays: Int = Defaults.MaintenanceHistoryDays,
    val color: String? = null,
    val masterKeyChanged: Instant? = null,
    val masterKeyChangeRec: Int = -1,
    val masterKeyChangeForce: Int = -1,
    val recycleBinEnabled: Boolean = false,
    val recycleBinUuid: UUID? = null,
    val recycleBinChanged: Instant? = null,
    val entryTemplatesGroup: UUID? = null,
    val entryTemplatesGroupChanged: Instant? = null,
    val historyMaxItems: Int = Defaults.HistoryMaxItems,
    val historyMaxSize: Int = Defaults.HistoryMaxSize,
    val lastSelectedGroup: UUID? = null,
    val lastTopVisibleGroup: UUID? = null,
    val memoryProtection: Set<BasicFields> = setOf(BasicFields.Password),
    val customIcons: Map<UUID, CustomIcon> = mapOf(),
    val customData: Map<String, CustomDataValue> = mapOf(),
    @PublishedApi
    internal val binaries: Map<ByteString, BinaryData> = linkedMapOf()
)
