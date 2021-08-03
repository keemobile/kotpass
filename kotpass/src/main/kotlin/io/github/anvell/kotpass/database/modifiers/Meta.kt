package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.models.Meta

inline fun KeePassDatabase.modifyMeta(
    crossinline block: Meta.() -> Meta
) = modifyContent {
    copy(meta = block(meta))
}
