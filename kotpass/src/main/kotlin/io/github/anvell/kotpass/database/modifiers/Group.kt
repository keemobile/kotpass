package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.findGroup
import io.github.anvell.kotpass.models.Group
import io.github.anvell.kotpass.models.TimeData
import java.time.Instant
import java.util.*

fun KeePassDatabase.moveGroup(
    uuid: UUID,
    parentGroup: UUID
): KeePassDatabase {
    if (content.group.uuid == uuid) {
        return this
    }
    val (previousParent, item) = findGroup { it.uuid == uuid } ?: return this

    return with(removeGroup(uuid)) {
        modifyGroup(parentGroup) {
            copy(
                groups = groups + item.copy(
                    times = item.times
                        ?.copy(locationChanged = Instant.now())
                        ?: TimeData.create(),
                    previousParentGroup = previousParent?.uuid
                )
            )
        }
    }
}

fun KeePassDatabase.modifyParentGroup(
    block: Group.() -> Group
) = modifyContent {
    copy(group = group.modifyGroup(group.uuid, block))
}

fun KeePassDatabase.modifyGroup(
    uuid: UUID,
    block: Group.() -> Group
) = modifyContent {
    copy(group = group.modifyGroup(uuid, block))
}

fun KeePassDatabase.removeGroup(
    uuid: UUID,
) = modifyContent {
    copy(group = group.removeChildGroup(uuid))
}

private fun Group.removeChildGroup(
    uuid: UUID
): Group {
    return if (groups.find { it.uuid == uuid } != null) {
        copy(groups = groups.filter { it.uuid != uuid })
    } else {
        copy(groups = groups.map { it.removeChildGroup(uuid) })
    }
}

private fun Group.modifyGroup(
    uuid: UUID,
    block: Group.() -> Group
): Group {
    return if (this.uuid == uuid) {
        val now = Instant.now()
        block(this).copy(
            times = times?.copy(
                lastAccessTime = now,
                lastModificationTime = now
            ) ?: TimeData.create(),
        )
    } else {
        copy(groups = groups.map { it.modifyGroup(uuid, block) })
    }
}
