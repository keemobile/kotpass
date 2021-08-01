package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.constants.BasicFields
import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.modifiers.modifyEntry
import io.github.anvell.kotpass.database.modifiers.modifyGroup
import io.github.anvell.kotpass.database.modifiers.moveEntry
import io.github.anvell.kotpass.database.modifiers.moveGroup
import io.github.anvell.kotpass.database.modifiers.withHistory
import io.github.anvell.kotpass.database.modifiers.withRecycleBin
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

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
    }
})

private fun loadDatabase(
    rawData: String,
    passphrase: String
) = KeePassDatabase.decode(
    inputStream = ByteArrayInputStream(rawData.decodeBase64ToArray()),
    credentials = Credentials.from(EncryptedValue.fromString(passphrase))
)
