package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.builders.buildEntry
import io.github.anvell.kotpass.constants.BasicFields
import io.github.anvell.kotpass.constants.Placeholder
import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.modifiers.modifyParentGroup
import io.github.anvell.kotpass.extensions.toHexString
import io.github.anvell.kotpass.models.EntryValue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.*

private val EmptyDatabase = KeePassDatabase.Ver4x.create(
    databaseName = "",
    rootName = "",
    credentials = Credentials.from(EncryptedValue.fromString(""))
)

class PlaceholdersSpec : DescribeSpec({

    describe("Placeholders") {
        it("Resolves local placeholders") {
            val uuid = UUID.randomUUID()
            val content1 = "Lorem"
            val content2 = "ipsum"
            val customKey1 = "Extra"
            val customKey2 = "Id"

            val entry = buildEntry(uuid) {
                fields[BasicFields.Title()] = EntryValue.Plain("{${Placeholder.UserName()}}")
                fields[BasicFields.UserName()] = EntryValue.Plain(content1)
                fields[BasicFields.Url()] = EntryValue.Plain("{${Placeholder.Notes()}}")
                fields[BasicFields.Notes()] = EntryValue.Plain(content2)
                fields[BasicFields.Password()] =
                    EntryValue.Plain("{${Placeholder.CustomField()}$customKey1}")

                fields[customKey1] =
                    EntryValue.Plain("{${Placeholder.Title()}} {${Placeholder.Url()}}")
                fields[customKey2] =
                    EntryValue.Plain("{${Placeholder.Uuid()}} {${Placeholder.CustomField()}$customKey1}")
            }
            val database = EmptyDatabase
                .modifyParentGroup { copy(entries = listOf(entry)) }
            val result = database
                .resolveEntryPlaceholders(entry)

            result[BasicFields.Title()]?.content shouldBe content1
            result[BasicFields.Password()]?.content shouldBe "$content1 $content2"
            result[customKey2]?.content shouldBe "${uuid.toHexString()} $content1 $content2"
        }

        it("Recursion is properly limited by maximum depth") {
            val maxDepth = 3U
            val last = "END"
            val entry = buildEntry(UUID.randomUUID()) {
                (0U..10U).forEach {
                    fields["$it"] = EntryValue
                        .Plain("{${Placeholder.CustomField()}${it + 1U}}")
                }
                fields["$maxDepth"] = EntryValue.Plain(last)
            }
            val database = EmptyDatabase
                .modifyParentGroup { copy(entries = listOf(entry)) }
            val result = database
                .resolveEntryPlaceholders(entry, maxDepth)

            result["0"]?.content shouldBe last
        }
    }

    describe("References") {
        it("Resolves nested references") {
            val content1 = "Lorem"
            val content2 = "ipsum"

            val uuid1 = UUID.randomUUID()
            val entry1 = buildEntry(uuid1) {
                fields[BasicFields.Title()] = EntryValue.Plain(content1)
                fields[BasicFields.Notes()] = EntryValue.Plain(content2)
            }
            val uuid2 = UUID.randomUUID()
            val entry2 = buildEntry(uuid2) {
                fields[BasicFields.Url()] = EntryValue
                    .Plain("{REF:T@I:${uuid1.toHexString()}} {REF:N@I:${uuid1.toHexString()}}")
            }
            val entryWithRefs = buildEntry(UUID.randomUUID()) {
                fields[BasicFields.UserName()] = EntryValue
                    .Plain("{REF:A@I:${uuid2.toHexString()}}")
            }
            val database = EmptyDatabase
                .modifyParentGroup {
                    copy(entries = listOf(entry1, entry2, entryWithRefs))
                }
            val result = database
                .resolveEntryPlaceholders(entryWithRefs)

            result[BasicFields.UserName()]?.content shouldBe "$content1 $content2"
        }

        it("Resolves query based references") {
            val userName = "Допплер"
            val content1 = "Lorem ipsum"
            val content2 = "dolor"

            val uuid1 = UUID.randomUUID()
            val entry1 = buildEntry(uuid1) {
                fields[BasicFields.Title()] = EntryValue.Plain(content1)
                fields[BasicFields.UserName()] = EntryValue.Plain(userName)
                fields[BasicFields.Notes()] = EntryValue.Plain(content2)
            }
            val uuid2 = UUID.randomUUID()
            val entry2 = buildEntry(uuid2) {
                fields[BasicFields.Url()] = EntryValue
                    .Plain("{REF:N@T:$content1}")
            }
            val entryWithRefs = buildEntry(UUID.randomUUID()) {
                fields[BasicFields.UserName()] = EntryValue
                    .Plain("{REF:A@I:${uuid2.toHexString()}}")
                fields[BasicFields.Notes()] = EntryValue
                    .Plain("{REF:N@U:$userName}")
            }
            val database = EmptyDatabase
                .modifyParentGroup {
                    copy(entries = listOf(entry1, entry2, entryWithRefs))
                }
            val result = database
                .resolveEntryPlaceholders(entryWithRefs)

            result[BasicFields.UserName()]?.content shouldBe content2
            result[BasicFields.Notes()]?.content shouldBe content2
        }
    }
})
