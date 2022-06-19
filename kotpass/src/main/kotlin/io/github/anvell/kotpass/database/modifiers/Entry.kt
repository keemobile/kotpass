package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.getEntry
import io.github.anvell.kotpass.models.DeletedObject
import io.github.anvell.kotpass.models.Entry
import io.github.anvell.kotpass.models.Group
import io.github.anvell.kotpass.models.TimeData
import java.time.Instant
import java.util.*

fun KeePassDatabase.moveEntry(
    uuid: UUID,
    parentGroup: UUID
): KeePassDatabase {
    val (parent, item) = getEntry { it.uuid == uuid } ?: return this

    return modifyParentGroup {
        removeChildEntry(uuid)
    }.modifyGroup(parentGroup) {
        copy(
            entries = entries + item.copy(
                times = item.times
                    ?.copy(locationChanged = Instant.now())
                    ?: TimeData.create(),
                previousParentGroup = parent.uuid
            )
        )
    }
}

fun KeePassDatabase.modifyEntry(
    uuid: UUID,
    block: Entry.() -> Entry
) = modifyContent {
    copy(group = group.modifyEntry(uuid, block))
}

fun KeePassDatabase.modifyEntries(
    block: Entry.() -> Entry
) = modifyContent {
    copy(group = group.modifyEntries(block))
}

fun KeePassDatabase.removeEntry(
    uuid: UUID
) = modifyContent {
    copy(
        group = group.removeChildEntry(uuid),
        deletedObjects = deletedObjects + DeletedObject(uuid, Instant.now())
    )
}

fun Entry.withHistory(
    block: Entry.() -> Entry
): Entry {
    val historicEntry = copy(history = listOf())
    return block().copy(
        history = history + historicEntry
    )
}

private fun Group.modifyEntry(
    uuid: UUID,
    block: Entry.() -> Entry
): Group {
    val item = entries.find { it.uuid == uuid }

    return if (item != null) {
        val now = Instant.now()
        val modifiedEntry = block(item).copy(
            times = item.times?.copy(
                lastAccessTime = now,
                lastModificationTime = now
            ) ?: TimeData.create()
        )
        copy(entries = (entries - item) + modifiedEntry)
    } else {
        copy(groups = groups.map { it.modifyEntry(uuid, block) })
    }
}

private fun Group.modifyEntries(
    block: Entry.() -> Entry
): Group = copy(
    entries = entries.map { entry ->
        val newEntry = block(entry)

        if (newEntry != entry) {
            val now = Instant.now()
            newEntry.copy(
                times = entry.times?.copy(
                    lastAccessTime = now,
                    lastModificationTime = now
                ) ?: TimeData.create()
            )
        } else {
            newEntry
        }
    },
    groups = groups.map { it.modifyEntries(block) }
)

private fun Group.removeChildEntry(
    uuid: UUID
): Group {
    return if (entries.find { it.uuid == uuid } != null) {
        copy(entries = entries.filter { it.uuid != uuid })
    } else {
        copy(groups = groups.map { it.removeChildEntry(uuid) })
    }
}
