package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.constants.BasicFields
import io.github.anvell.kotpass.constants.Defaults
import java.time.Instant
import java.util.*

data class Meta(
    val generator: String = Defaults.Generator,
    val headerHash: ByteArray? = null,
    val settingsChanged: Instant? = null,
    val name: String = "",
    val nameChanged: Instant? = null,
    val description: String = "",
    val descriptionChanged: Instant? = null,
    val defaultUser: String = "",
    val defaultUserChanged: Instant? = null,
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
    val binaries: List<Binary> = listOf(),
    val customIcons: Map<UUID, CustomIcon> = mapOf(),
    val customData: Map<String, CustomDataValue> = mapOf()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Meta

        if (generator != other.generator) return false
        if (headerHash != null) {
            if (other.headerHash == null) return false
            if (!headerHash.contentEquals(other.headerHash)) return false
        } else if (other.headerHash != null) return false
        if (settingsChanged != other.settingsChanged) return false
        if (name != other.name) return false
        if (nameChanged != other.nameChanged) return false
        if (description != other.description) return false
        if (descriptionChanged != other.descriptionChanged) return false
        if (defaultUser != other.defaultUser) return false
        if (defaultUserChanged != other.defaultUserChanged) return false
        if (maintenanceHistoryDays != other.maintenanceHistoryDays) return false
        if (color != other.color) return false
        if (masterKeyChanged != other.masterKeyChanged) return false
        if (masterKeyChangeRec != other.masterKeyChangeRec) return false
        if (masterKeyChangeForce != other.masterKeyChangeForce) return false
        if (recycleBinEnabled != other.recycleBinEnabled) return false
        if (recycleBinUuid != other.recycleBinUuid) return false
        if (recycleBinChanged != other.recycleBinChanged) return false
        if (entryTemplatesGroup != other.entryTemplatesGroup) return false
        if (entryTemplatesGroupChanged != other.entryTemplatesGroupChanged) return false
        if (historyMaxItems != other.historyMaxItems) return false
        if (historyMaxSize != other.historyMaxSize) return false
        if (lastSelectedGroup != other.lastSelectedGroup) return false
        if (lastTopVisibleGroup != other.lastTopVisibleGroup) return false
        if (memoryProtection != other.memoryProtection) return false
        if (binaries != other.binaries) return false
        if (customIcons != other.customIcons) return false
        if (customData != other.customData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = generator.hashCode()
        result = 31 * result + (headerHash?.contentHashCode() ?: 0)
        result = 31 * result + (settingsChanged?.hashCode() ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + (nameChanged?.hashCode() ?: 0)
        result = 31 * result + description.hashCode()
        result = 31 * result + (descriptionChanged?.hashCode() ?: 0)
        result = 31 * result + defaultUser.hashCode()
        result = 31 * result + (defaultUserChanged?.hashCode() ?: 0)
        result = 31 * result + maintenanceHistoryDays
        result = 31 * result + (color?.hashCode() ?: 0)
        result = 31 * result + (masterKeyChanged?.hashCode() ?: 0)
        result = 31 * result + masterKeyChangeRec
        result = 31 * result + masterKeyChangeForce
        result = 31 * result + recycleBinEnabled.hashCode()
        result = 31 * result + (recycleBinUuid?.hashCode() ?: 0)
        result = 31 * result + (recycleBinChanged?.hashCode() ?: 0)
        result = 31 * result + (entryTemplatesGroup?.hashCode() ?: 0)
        result = 31 * result + (entryTemplatesGroupChanged?.hashCode() ?: 0)
        result = 31 * result + historyMaxItems
        result = 31 * result + historyMaxSize
        result = 31 * result + (lastSelectedGroup?.hashCode() ?: 0)
        result = 31 * result + (lastTopVisibleGroup?.hashCode() ?: 0)
        result = 31 * result + memoryProtection.hashCode()
        result = 31 * result + binaries.hashCode()
        result = 31 * result + customIcons.hashCode()
        result = 31 * result + customData.hashCode()
        return result
    }
}
