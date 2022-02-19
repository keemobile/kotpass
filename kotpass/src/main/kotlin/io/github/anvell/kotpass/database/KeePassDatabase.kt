package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.constants.GroupOverride
import io.github.anvell.kotpass.database.header.DatabaseHeader
import io.github.anvell.kotpass.database.header.DatabaseInnerHeader
import io.github.anvell.kotpass.models.*
import java.util.*

sealed class KeePassDatabase {
    abstract val credentials: Credentials
    abstract val header: DatabaseHeader
    abstract val content: DatabaseContent

    data class Ver3x(
        override val credentials: Credentials,
        override val header: DatabaseHeader.Ver3x,
        override val content: DatabaseContent
    ) : KeePassDatabase() {

        companion object {
            fun create(
                databaseName: String,
                rootName: String,
                credentials: Credentials
            ) = Ver3x(
                credentials = credentials,
                header = DatabaseHeader.Ver3x.create(),
                content = DatabaseContent(
                    meta = Meta(name = databaseName),
                    group = Group(
                        uuid = UUID.randomUUID(),
                        name = rootName,
                        enableAutoType = GroupOverride.Enabled,
                        enableSearching = GroupOverride.Enabled
                    ),
                    deletedObjects = listOf()
                )
            )
        }
    }

    data class Ver4x(
        override val credentials: Credentials,
        override val header: DatabaseHeader.Ver4x,
        override val content: DatabaseContent,
        val innerHeader: DatabaseInnerHeader
    ) : KeePassDatabase() {

        companion object {
            fun create(
                databaseName: String,
                rootName: String,
                credentials: Credentials
            ) = Ver4x(
                credentials = credentials,
                header = DatabaseHeader.Ver4x.create(),
                content = DatabaseContent(
                    meta = Meta(name = databaseName),
                    group = Group(
                        uuid = UUID.randomUUID(),
                        name = rootName,
                        enableAutoType = GroupOverride.Enabled,
                        enableSearching = GroupOverride.Enabled
                    ),
                    deletedObjects = listOf()
                ),
                innerHeader = DatabaseInnerHeader.create()
            )
        }
    }

    companion object {
        const val MinSupportedVersion = 3
        const val MaxSupportedVersion = 4
    }
}

fun KeePassDatabase.traverse(
    block: (DatabaseElement) -> Unit
) = content.group.traverse(block)

fun KeePassDatabase.findGroup(
    predicate: (Group) -> Boolean
): Pair<Group?, Group>? {
    return if (predicate(content.group)) {
        null to content.group
    } else {
        content.group.findChildGroup(predicate)
    }
}

fun KeePassDatabase.findGroupBy(
    predicate: Group.() -> Boolean
): Group? {
    return if (predicate(content.group)) {
        content.group
    } else {
        content.group
            .findChildGroup(predicate)
            ?.let { (_, group) -> group }
    }
}

fun KeePassDatabase.findEntry(
    predicate: (Entry) -> Boolean
): Pair<Group, Entry>? {
    return content
        .group
        .findChildEntry(predicate)
}

fun KeePassDatabase.findEntryBy(
    predicate: Entry.() -> Boolean
): Entry? {
    return content
        .group
        .findChildEntry(predicate)
        ?.let { (_, entry) -> entry }
}

fun KeePassDatabase.findEntries(
    predicate: (Entry) -> Boolean
): List<Pair<Group, List<Entry>>> {
    return content
        .group
        .findChildEntries(content.meta.recycleBinUuid, predicate)
}
