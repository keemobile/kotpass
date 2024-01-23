package app.keemobile.kotpass.database.modifiers

import app.keemobile.kotpass.cryptography.EncryptedValue
import app.keemobile.kotpass.database.Credentials
import app.keemobile.kotpass.database.KeePassDatabase
import app.keemobile.kotpass.database.decode
import app.keemobile.kotpass.database.encode
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
                ClassLoader.getSystemResourceAsStream("ver4_argon2.kdbx")!!,
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
