package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.Credentials
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.decode
import io.github.anvell.kotpass.database.encode
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.resources.DatabaseRes
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
