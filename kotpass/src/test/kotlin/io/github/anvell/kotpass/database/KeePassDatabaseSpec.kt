package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.constants.BasicFields
import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.modifiers.*
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.models.DatabaseElement
import io.github.anvell.kotpass.models.DeletedObject
import io.github.anvell.kotpass.models.TimeData
import io.github.anvell.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.Period

class KeePassDatabaseSpec : DescribeSpec({

    describe("Database decoder") {
        it("Reads KeePass 3.x file") {
            val database = loadDatabase(
                rawData = DatabaseRes.DbVer3Aes,
                passphrase = "1"
            )
            database.content.group.name shouldBe "New"
        }

        it("Reads KeePass 4.x file") {
            val database = loadDatabase(
                rawData = DatabaseRes.DbVer4WithBinaries,
                passphrase = "1"
            )
            database.content.group.name shouldBe "New"
        }
    }

    describe("Database encoder") {
        it("Writes KeePass 3.x file") {
            var database = loadDatabase(
                rawData = DatabaseRes.DbVer3Aes,
                passphrase = "1"
            )
            val data = ByteArrayOutputStream()
                .apply { database.encode(this) }
                .toByteArray()
            database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(data),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
            )
            database.content.group.name shouldBe "New"
        }

        it("Writes KeePass 4.x file") {
            var database = loadDatabase(
                rawData = DatabaseRes.DbVer4Argon2,
                passphrase = "1"
            )
            val data = ByteArrayOutputStream()
                .apply { database.encode(this) }
                .toByteArray()
            database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(data),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
            )
            database.content.group.name shouldBe "New"
        }
    }

    describe("Database search") {
        it("Traverse database") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            )
            val result = mutableSetOf<DatabaseElement>()
            database.traverse { result += it }

            result.map { it.uuid } shouldContainAll setOf(
                DatabaseRes.GroupsAndEntries.Group1,
                DatabaseRes.GroupsAndEntries.Group2,
                DatabaseRes.GroupsAndEntries.Group3,
                DatabaseRes.GroupsAndEntries.Entry1,
                DatabaseRes.GroupsAndEntries.Entry2,
                DatabaseRes.GroupsAndEntries.Entry3
            )
        }

        it("Finds entries with specific title") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            )
            val entries = database.findEntries {
                it.fields[BasicFields.Title.value]
                    ?.content
                    ?.contains("Entry") == true
            }

            entries.size shouldBe 3
        }

        it("Finds entry with specific title") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            )
            val result = database.findEntry {
                it.fields[BasicFields.Title.value]
                    ?.content
                    ?.contains("Entry 2") == true
            }

            result shouldNotBe null
        }

        it("Finds group with specific name") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            )
            val result = database.findGroup { it.name == "Group 3" }

            result shouldNotBe null
            result?.second?.name shouldBe "Group 3"
        }
    }

    describe("Database modifiers") {
        it("Removed Group is moved to recycle bin") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            ).withRecycleBin { recycleBinUuid ->
                moveGroup(DatabaseRes.GroupsAndEntries.Group2, recycleBinUuid)
            }
            val (parent, _) = database
                .findGroup { it.uuid == DatabaseRes.GroupsAndEntries.Group2 }!!

            database.content.meta.recycleBinEnabled shouldBe true
            parent?.uuid shouldBe database.content.meta.recycleBinUuid
        }

        it("Removed Group and it's children UUIDs are added to deleted objects") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            ).removeGroup(
                DatabaseRes.GroupsAndEntries.Group2
            )
            val deletedObjects = database
                .content
                .deletedObjects
                .map(DeletedObject::id)

            deletedObjects shouldContainAll listOf(
                DatabaseRes.GroupsAndEntries.Group2,
                DatabaseRes.GroupsAndEntries.Group3,
                DatabaseRes.GroupsAndEntries.Entry2,
                DatabaseRes.GroupsAndEntries.Entry3
            )
        }

        it("Group modification") {
            val (_, group) = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            ).modifyGroup(DatabaseRes.GroupsAndEntries.Group3) {
                copy(name = "Hello")
            }.findGroup {
                it.uuid == DatabaseRes.GroupsAndEntries.Group3
            }!!

            group.name shouldBe "Hello"
        }

        it("Entry modification with history") {
            val (_, entry) = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            ).modifyEntry(DatabaseRes.GroupsAndEntries.Entry1) {
                withHistory {
                    copy(overrideUrl = "Hello")
                }
            }.findEntry {
                it.uuid == DatabaseRes.GroupsAndEntries.Entry1
            }!!

            entry.overrideUrl shouldBe "Hello"
            entry.history.size shouldBe 1
        }

        it("Removed Entry is moved to recycle bin") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            ).withRecycleBin { recycleBinUuid ->
                moveEntry(DatabaseRes.GroupsAndEntries.Entry1, recycleBinUuid)
            }
            val (parent, _) = database
                .findEntry { it.uuid == DatabaseRes.GroupsAndEntries.Entry1 }!!

            database.content.meta.recycleBinEnabled shouldBe true
            parent.uuid shouldBe database.content.meta.recycleBinUuid
        }

        it("Removed Entry UUID is added to deleted objects") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            ).removeEntry(
                DatabaseRes.GroupsAndEntries.Entry1
            )
            val deletedObjects = database
                .content
                .deletedObjects
                .map(DeletedObject::id)

            deletedObjects shouldContain DatabaseRes.GroupsAndEntries.Entry1
        }

        it("Old entries are removed from history when performing cleanup") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            )
            val outdated = Instant
                .now()
                .minus(Period.ofDays(database.content.meta.maintenanceHistoryDays + 1))
            val (_, entry) = database
                .modifyEntry(DatabaseRes.GroupsAndEntries.Entry1) {
                    copy(
                        history = listOf(
                            copy(
                                times = TimeData
                                    .create()
                                    .copy(lastModificationTime = outdated)
                            )
                        )
                    )
                }
                .cleanupHistory()
                .findEntry { it.uuid == DatabaseRes.GroupsAndEntries.Entry1 }!!

            entry.history.size shouldBe 0
        }
    }
})

private fun loadDatabase(
    rawData: String,
    passphrase: String
) = KeePassDatabase.decode(
    inputStream = ByteArrayInputStream(rawData.decodeBase64ToArray()),
    credentials = Credentials.from(EncryptedValue.fromString(passphrase))
)
