package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.constants.PredefinedIcon
import java.util.*

data class Group(
    override val uuid: UUID,
    val name: String,
    val notes: String = "",
    override val icon: PredefinedIcon = PredefinedIcon.Folder,
    val customIconUuid: UUID? = null,
    override val times: TimeData? = TimeData.create(),
    val expanded: Boolean = true,
    val defaultAutoTypeSequence: String? = null,
    val enableAutoType: Boolean? = null,
    val enableSearching: Boolean? = null,
    val lastTopVisibleEntry: UUID? = null,
    val previousParentGroup: UUID? = null,
    override val tags: List<String> = listOf(),
    val groups: List<Group> = listOf(),
    val entries: List<Entry> = listOf(),
    val customData: Map<String, CustomDataValue> = mapOf()
) : DatabaseElement {

    fun traverse(
        block: (DatabaseElement) -> Unit
    ) {
        val stack = Stack<Group>()
        stack.push(this)

        while (!stack.empty()) {
            val current = stack.pop()
            block(current)

            for (entry in current.entries) {
                block(entry)
            }
            for (group in current.groups) {
                stack.push(group)
            }
        }
    }

    fun findChildGroup(
        predicate: (Group) -> Boolean
    ): Pair<Group, Group>? {
        val stack = Stack<Pair<Group, Group>>()
        groups.forEach {
            stack.push(this to it)
        }

        while (!stack.empty()) {
            val (parent, current) = stack.pop()

            if (predicate(current)) {
                return parent to current
            }
            current.groups.forEach {
                stack.push(current to it)
            }
        }

        return null
    }

    fun findChildEntry(
        predicate: (Entry) -> Boolean
    ): Pair<Group, Entry>? {
        val stack = Stack<Group>()
        stack.push(this)

        while (!stack.empty()) {
            val current = stack.pop()

            for (entry in current.entries) {
                if (predicate(entry)) {
                    return current to entry
                }
            }
            current.groups.forEach(stack::push)
        }

        return null
    }

    fun findChildEntries(
        recycleBinUuid: UUID?,
        predicate: (Entry) -> Boolean
    ): List<Pair<Group, List<Entry>>> {
        val result = mutableListOf<Pair<Group, List<Entry>>>()
        val stack = Stack<Group>()
        stack.push(this)

        while (!stack.empty()) {
            val current = stack.pop()
            val found = current.entries.filter { predicate(it) }

            if (found.isNotEmpty()) {
                result.add(current to found)
            }
            current.groups.forEach {
                if (recycleBinUuid == null ||
                    it.uuid.compareTo(recycleBinUuid) != 0
                ) {
                    stack.push(it)
                }
            }
        }

        return result
    }
}
