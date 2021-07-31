package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.database.header.DatabaseHeader
import io.github.anvell.kotpass.database.header.DatabaseInnerHeader
import io.github.anvell.kotpass.models.Binary
import io.github.anvell.kotpass.models.DatabaseContent
import io.github.anvell.kotpass.models.Entry
import io.github.anvell.kotpass.models.Group
import io.github.anvell.kotpass.models.Meta
import java.util.*

sealed class KeePassDatabase {
    abstract val credentials: Credentials
    abstract val header: DatabaseHeader
    abstract val content: DatabaseContent

    abstract fun findBinaryById(id: Int): Binary?

    data class Ver3x(
        override val credentials: Credentials,
        override val header: DatabaseHeader.Ver3x,
        override val content: DatabaseContent
    ) : KeePassDatabase() {

        override fun findBinaryById(id: Int): Binary? {
            return content.meta.binaries.find { it.id == id }
        }

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
                        enableAutoType = true,
                        enableSearching = true
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
        internal val innerHeader: DatabaseInnerHeader
    ) : KeePassDatabase() {

        override fun findBinaryById(id: Int): Binary? {
            return innerHeader.binaries.find { it.id == id }
        }

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
                        enableAutoType = true,
                        enableSearching = true
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

fun KeePassDatabase.findGroup(
    predicate: (Group) -> Boolean
): Pair<Group?, Group>? = with(Stack<Pair<Group?, Group>>()) {
    push(null to content.group)

    while (!empty()) {
        val (parent, group) = pop()

        if (predicate(group)) {
            return parent to group
        }
        group.groups.forEach {
            push(group to it)
        }
    }

    return null
}

fun KeePassDatabase.findEntry(
    predicate: (Entry) -> Boolean
): Pair<Group, Entry>? = with(Stack<Group>()) {
    push(content.group)

    while (!empty()) {
        val group = pop()

        for (entry in group.entries) {
            if (predicate(entry)) {
                return group to entry
            }
        }
        group.groups.forEach(::push)
    }

    return null
}

fun KeePassDatabase.findEntries(
    predicate: (Entry) -> Boolean
): List<Pair<Group, List<Entry>>> = with(Stack<Group>()) {
    val result = mutableListOf<Pair<Group, List<Entry>>>()
    push(content.group)

    while (!empty()) {
        val group = pop()
        val found = group.entries.filter { predicate(it) }

        if (found.isNotEmpty()) {
            result.add(group to found)
        }
        group.groups.forEach {
            if (content.meta.recycleBinUuid == null ||
                it.uuid.compareTo(content.meta.recycleBinUuid) != 0
            ) {
                push(it)
            }
        }
    }

    return result
}
