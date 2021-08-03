package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.models.BinaryData
import io.github.anvell.kotpass.models.BinaryReference
import io.github.anvell.kotpass.models.Group
import okio.ByteString
import java.util.*

val KeePassDatabase.binaries
    get() = when (this) {
        is KeePassDatabase.Ver3x -> content.meta.binaries
        is KeePassDatabase.Ver4x -> innerHeader.binaries
    }

inline fun KeePassDatabase.modifyBinaries(
    crossinline block: (Map<ByteString, BinaryData>) -> Map<ByteString, BinaryData>
): KeePassDatabase = when (this) {
    is KeePassDatabase.Ver3x -> modifyMeta {
        copy(binaries = block(content.meta.binaries))
    }
    is KeePassDatabase.Ver4x -> copy(
        innerHeader = innerHeader.copy(
            binaries = block(innerHeader.binaries)
        )
    )
}

fun KeePassDatabase.removeUnusedBinaries(): KeePassDatabase {
    val cleanupList = binaries.keys.toMutableSet()

    with(Stack<Group>()) {
        push(content.group)

        while (!empty()) {
            val group = pop()

            for (entry in group.entries) {
                cleanupList.removeAll(
                    entry.binaries.map(BinaryReference::hash)
                )
            }
            group.groups.forEach(::push)
        }
    }

    return modifyBinaries { it - cleanupList }
}
