package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.constants.BasicField
import io.github.anvell.kotpass.constants.PredefinedIcon
import io.github.anvell.kotpass.cryptography.EncryptedValue
import java.util.*

data class Entry(
    override val uuid: UUID,
    override val icon: PredefinedIcon = PredefinedIcon.Key,
    val customIconUuid: UUID? = null,
    val foregroundColor: String? = null,
    val backgroundColor: String? = null,
    val overrideUrl: String = "",
    override val times: TimeData? = TimeData.create(),
    val autoType: AutoTypeData? = null,
    val fields: Map<String, EntryValue> = mapOf(),
    override val tags: List<String> = listOf(),
    val binaries: List<BinaryReference> = listOf(),
    val history: List<Entry> = listOf(),
    val customData: Map<String, CustomDataValue> = mapOf(),
    val previousParentGroup: UUID? = null,
    val qualityCheck: Boolean = true
) : DatabaseElement {

    operator fun get(field: BasicField): EntryValue? = fields[field()]

    companion object {
        fun create() = Entry(
            uuid = UUID.randomUUID(),
            times = TimeData.create(),
            fields = emptyBasicFields()
        )

        private fun emptyBasicFields() = buildMap {
            BasicField
                .values()
                .filter { it != BasicField.Password }
                .forEach { field -> put(field(), EntryValue.Plain("")) }

            val password = EncryptedValue.fromString("")
            put(BasicField.Password(), EntryValue.Encrypted(password))
        }
    }
}
