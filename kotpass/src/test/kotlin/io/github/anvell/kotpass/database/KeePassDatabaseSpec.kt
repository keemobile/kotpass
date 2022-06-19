package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.constants.BasicField
import io.github.anvell.kotpass.constants.GroupOverride
import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.modifiers.*
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.models.*
import io.github.anvell.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.Period
import java.util.*

private val EmptyDatabase = KeePassDatabase.Ver4x.create(
    rootName = "",
    meta = Meta(),
    credentials = Credentials.from(EncryptedValue.fromString(""))
)

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

        it("Entry search should respect Group's search override") {
            val inEnabledGroup = Entry(UUID.randomUUID())
            val inDisabledGroup = Entry(UUID.randomUUID())
            val inInheritedGroup = Entry(UUID.randomUUID())
            val enabledGroup = {
                Group(
                    uuid = UUID.randomUUID(),
                    name = "",
                    enableSearching = GroupOverride.Enabled,
                    entries = listOf(inEnabledGroup)
                )
            }
            val disabledGroup = {
                Group(
                    uuid = UUID.randomUUID(),
                    name = "",
                    enableSearching = GroupOverride.Disabled,
                    groups = listOf(
                        enabledGroup(),
                        enabledGroup()
                    ),
                    entries = listOf(inDisabledGroup)
                )
            }
            val database = EmptyDatabase.modifyParentGroup {
                copy(
                    enableSearching = GroupOverride.Enabled,
                    groups = listOf(
                        Group(
                            uuid = UUID.randomUUID(),
                            name = "",
                            enableSearching = GroupOverride.Inherit,
                            groups = listOf(
                                disabledGroup(),
                                disabledGroup()
                            ),
                            entries = listOf(inInheritedGroup)
                        )
                    )
                )
            }
            val uuids = database
                .findEntries { true }
                .flatMap { (_, entries) -> entries }
                .map(Entry::uuid)
                .toSet()

            inEnabledGroup.uuid shouldBeIn uuids
            inInheritedGroup.uuid shouldBeIn uuids
            inDisabledGroup.uuid shouldNotBeIn uuids
        }

        it("Finds entries with specific title") {
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            )
            val entries = database.findEntries {
                it[BasicField.Title]
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
                it[BasicField.Title]
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
            val result = database.getGroup { it.name == "Group 3" }

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
                .getGroup { it.uuid == DatabaseRes.GroupsAndEntries.Group2 }!!

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
            }.getGroup {
                it.uuid == DatabaseRes.GroupsAndEntries.Group3
            }!!

            group.name shouldBe "Hello"
        }

        it("Entries mass modification") {
            val label = "Hello"
            val database = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            ).modifyEntries {
                copy(overrideUrl = label)
            }

            database.traverse { element ->
                if (element is Entry) {
                    element.overrideUrl shouldBe label
                }
            }
        }

        it("Entry modification with history") {
            val (_, entry) = loadDatabase(
                rawData = DatabaseRes.GroupsAndEntries.DbGroupsAndEntries,
                passphrase = "1"
            ).modifyEntry(DatabaseRes.GroupsAndEntries.Entry1) {
                withHistory {
                    copy(overrideUrl = "Hello")
                }
            }.getEntry {
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
                .getEntry { it.uuid == DatabaseRes.GroupsAndEntries.Entry1 }!!

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
                .getEntry { it.uuid == DatabaseRes.GroupsAndEntries.Entry1 }!!

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
