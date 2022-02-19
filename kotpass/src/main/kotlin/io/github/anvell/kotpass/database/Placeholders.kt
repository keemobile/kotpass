@file:Suppress("SpellCheckingInspection")

package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.constants.BasicField
import io.github.anvell.kotpass.constants.Defaults
import io.github.anvell.kotpass.constants.FieldReference
import io.github.anvell.kotpass.constants.Placeholder
import io.github.anvell.kotpass.extensions.asUuid
import io.github.anvell.kotpass.extensions.toHexString
import io.github.anvell.kotpass.models.Entry
import io.github.anvell.kotpass.models.EntryValue
import okio.ByteString.Companion.decodeHex

private val PlaceholderRegex = Regex(
    pattern = """\{([ \t\d\p{L}:@]+)}"""
)
private val ReferenceRegex = Regex(
    pattern = """\{REF:([TUPANI])@([TUPANIO]):([ \t\d\p{L}]+)}""",
    option = RegexOption.IGNORE_CASE
)

/**
 * Iterates over [Entry] fields and recursively resolves
 * every [Placeholder] up to [maxDepth].
 */
fun KeePassDatabase.resolveEntryPlaceholders(
    entry: Entry,
    maxDepth: UInt = Defaults.PlaceholdersMaxDepth
): Map<String, EntryValue> {
    val result = mutableMapOf<String, EntryValue>()

    for ((key, value) in entry.fields) {
        result[key] = resolveValuePlaceholders(entry, value, maxDepth)
    }
    return result
}

/**
 * Recursively resolves every [Placeholder] up to [maxDepth].
 */
fun KeePassDatabase.resolveValuePlaceholders(
    entry: Entry,
    value: EntryValue,
    maxDepth: UInt = Defaults.PlaceholdersMaxDepth
): EntryValue = value.takeIfNoPlaceholderOrElse {
    value.map {
        PlaceholderRegex.replace(value.content) { match ->
            resolvePlaceholder(entry, match.value, maxDepth)
        }
    }
}

/**
 * Recursively resolves [Placeholder] up to [maxDepth].
 */
fun KeePassDatabase.resolvePlaceholder(
    entry: Entry,
    placeholder: String,
    maxDepth: UInt = Defaults.PlaceholdersMaxDepth
): String {
    if (maxDepth == 0U) return placeholder
    val pattern = placeholder.trim { it == '{' || it == '}' }

    if (pattern.startsWith(Placeholder.Reference(), true)) {
        return resolveReference(placeholder, maxDepth)
    }
    if (pattern.equals(Placeholder.Uuid(), true)) {
        return entry.uuid.toHexString()
    }

    val fieldKey = when {
        pattern.startsWith(Placeholder.CustomField(), true) ->
            pattern.drop(Placeholder.CustomField().length)
        pattern.equals(Placeholder.Title(), true) -> BasicField.Title()
        pattern.equals(Placeholder.UserName(), true) -> BasicField.UserName()
        pattern.equals(Placeholder.Password(), true) -> BasicField.Password()
        pattern.equals(Placeholder.Url(), true) -> BasicField.Url()
        pattern.equals(Placeholder.Notes(), true) -> BasicField.Notes()
        else -> return placeholder
    }
    val content = entry.fields[fieldKey]?.content ?: ""

    return content.takeIfNoPlaceholderOrElse {
        PlaceholderRegex.replace(content) { match ->
            resolvePlaceholder(entry, match.value, maxDepth - 1U)
        }
    }
}

/**
 * Recursively resolves field reference up to [maxDepth].
 */
fun KeePassDatabase.resolveReference(
    reference: String,
    maxDepth: UInt = Defaults.PlaceholdersMaxDepth
): String {
    val match = ReferenceRegex.find(reference) ?: return reference
    val (wantedKey, searchInKey, searchText) = match.destructured

    val wanted = FieldReference.WantedField[wantedKey] ?: return reference
    val searchIn = FieldReference.SearchIn[searchInKey] ?: return reference

    val foundEntry = when (searchIn) {
        FieldReference.SearchIn.Uuid -> {
            runCatching { searchText.decodeHex().asUuid() }
                .map { refUuid -> findEntryBy { uuid == refUuid } }
                .getOrNull()
        }
        FieldReference.SearchIn.Title -> {
            findEntryBy { findInField(BasicField.Title(), searchText) }
        }
        FieldReference.SearchIn.UserName -> {
            findEntryBy { findInField(BasicField.UserName(), searchText) }
        }
        FieldReference.SearchIn.Password -> {
            findEntryBy { findInField(BasicField.Password(), searchText) }
        }
        FieldReference.SearchIn.Url -> {
            findEntryBy { findInField(BasicField.Url(), searchText) }
        }
        FieldReference.SearchIn.Notes -> {
            findEntryBy { findInField(BasicField.Notes(), searchText) }
        }
        FieldReference.SearchIn.Other -> {
            findEntryBy {
                val customFieldKeys = fields.keys - BasicField.keys
                customFieldKeys
                    .any { k -> findInField(k, searchText) }
            }
        }
    } ?: return reference

    val rawText = when (wanted) {
        FieldReference.WantedField.Title -> foundEntry[BasicField.Title]?.content
        FieldReference.WantedField.UserName -> foundEntry[BasicField.UserName]?.content
        FieldReference.WantedField.Password -> foundEntry[BasicField.Password]?.content
        FieldReference.WantedField.Url -> foundEntry[BasicField.Url]?.content
        FieldReference.WantedField.Notes -> foundEntry[BasicField.Notes]?.content
        FieldReference.WantedField.Uuid -> foundEntry.uuid.toHexString()
    } ?: return reference

    return rawText.takeIfNoPlaceholderOrElse {
        PlaceholderRegex.replace(rawText) { match ->
            resolvePlaceholder(foundEntry, match.value, maxDepth - 1U)
        }
    }
}

private fun Entry.findInField(
    fieldKey: String,
    text: CharSequence
) = fields[fieldKey]
    ?.content
    ?.contains(text)
    ?: false

private inline fun EntryValue.takeIfNoPlaceholderOrElse(
    block: (EntryValue) -> EntryValue
) = when {
    '{' !in this.content -> this
    else -> block(this)
}

private inline fun String.takeIfNoPlaceholderOrElse(
    block: (String) -> String
) = when {
    '{' !in this -> this
    else -> block(this)
}
