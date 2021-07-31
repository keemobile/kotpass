package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.constants.BasicFields
import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class KeePassDatabaseSpec : DescribeSpec({

    describe("Database decoder") {
        it("Reads KeePass 3.x file") {
            val database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbVer3Aes.decodeBase64ToArray()),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
            )
            database.content.group.name shouldBe "New"
        }

        it("Reads KeePass 4.x file") {
            val database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbVer4WithBinaries.decodeBase64ToArray()),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
            )
            database.content.group.name shouldBe "New"
        }
    }

    describe("Database encoder") {
        it("Writes KeePass 3.x file") {
            var database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbVer3Aes.decodeBase64ToArray()),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
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
            var database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbVer4Argon2.decodeBase64ToArray()),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
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
            val database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbGroupsAndEntries.decodeBase64ToArray()),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
            )
            val entries = database.findEntries {
                it.fields[BasicFields.Title.value]
                    ?.content
                    ?.contains("Entry") == true
            }

            entries.size shouldBe 3
        }

        it("Finds entry with specific title") {
            val database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbGroupsAndEntries.decodeBase64ToArray()),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
            )
            val result = database.findEntry {
                it.fields[BasicFields.Title.value]
                    ?.content
                    ?.contains("Entry 2") == true
            }

            result shouldNotBe null
        }

        it("Finds group with specific name") {
            val database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbGroupsAndEntries.decodeBase64ToArray()),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
            )
            val result = database.findGroup { it.name == "Group 3" }

            result shouldNotBe null
            result?.second?.name shouldBe "Group 3"
        }
    }
})
