package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.models.CustomIcon
import java.util.*

/**
 * Replaces [Map] of [CustomIcon] while removing invalid references in entries.
 */
inline fun KeePassDatabase.modifyCustomIcons(
    crossinline block: (Map<UUID, CustomIcon>) -> Map<UUID, CustomIcon>
): KeePassDatabase {
    val newCustomIcons = block(content.meta.customIcons)
    val removedUuids = content.meta.customIcons.keys
        .filter { it !in newCustomIcons.keys }

    return modifyEntries {
        when (customIconUuid) {
            in removedUuids -> copy(customIconUuid = null)
            else -> this
        }
    }.modifyGroups {
        when (customIconUuid) {
            in removedUuids -> copy(customIconUuid = null)
            else -> this
        }
    }.modifyMeta {
        copy(customIcons = newCustomIcons)
    }
}
