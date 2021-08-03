package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.constants.Defaults
import io.github.anvell.kotpass.constants.PredefinedIcon
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.models.DatabaseContent
import io.github.anvell.kotpass.models.Group
import java.time.Instant
import java.util.*

inline fun KeePassDatabase.modifyContent(
    crossinline block: DatabaseContent.() -> DatabaseContent
) = when (this) {
    is KeePassDatabase.Ver3x -> copy(content = block(content))
    is KeePassDatabase.Ver4x -> copy(content = block(content))
}

inline fun KeePassDatabase.withRecycleBin(
    crossinline block: KeePassDatabase.(recycleBinUuid: UUID) -> KeePassDatabase
): KeePassDatabase {
    return if (content.meta.recycleBinUuid != null) {
        block(this, content.meta.recycleBinUuid!!)
    } else {
        val recycleBin = Group(
            uuid = UUID.randomUUID(),
            name = Defaults.RecycleBinName,
            icon = PredefinedIcon.TrashBin
        )
        modifyContent {
            copy(
                meta = meta.copy(
                    recycleBinEnabled = true,
                    recycleBinUuid = recycleBin.uuid,
                    recycleBinChanged = Instant.now()
                )
            )
        }.modifyParentGroup {
            copy(groups = groups + recycleBin)
        }.run {
            block(recycleBin.uuid)
        }
    }
}
