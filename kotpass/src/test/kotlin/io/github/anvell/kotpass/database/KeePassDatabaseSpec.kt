package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
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
})
