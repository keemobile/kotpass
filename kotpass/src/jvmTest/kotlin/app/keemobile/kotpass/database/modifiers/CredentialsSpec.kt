package app.keemobile.kotpass.database.modifiers

import app.keemobile.kotpass.cryptography.EncryptedValue
import app.keemobile.kotpass.database.Credentials
import app.keemobile.kotpass.database.KeePassDatabase
import app.keemobile.kotpass.database.decode
import app.keemobile.kotpass.database.encode
import app.keemobile.kotpass.io.decodeBase64ToArray
import app.keemobile.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class CredentialsSpec : DescribeSpec({

    describe("Credentials modifier") {
        it("Properly changes credentials") {
            val newCredentials = Credentials.from(
                passphrase = EncryptedValue.fromString("2"),
                keyData = byteArrayOf(0x4, 0x7, 0x9)
            )
            var database = KeePassDatabase.decode(
                ByteArrayInputStream(DatabaseRes.DbVer4Argon2.decodeBase64ToArray()),
                Credentials.from(EncryptedValue.fromString("1"))
            ).modifyCredentials {
                newCredentials
            }
            val data = ByteArrayOutputStream()
                .apply { database.encode(this) }
                .toByteArray()
            database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(data),
                credentials = newCredentials
            )

            database.content.group.name shouldBe "New"
        }
    }
})
