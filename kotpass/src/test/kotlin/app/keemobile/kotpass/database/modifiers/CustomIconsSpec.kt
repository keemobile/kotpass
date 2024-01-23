package app.keemobile.kotpass.database.modifiers

import app.keemobile.kotpass.cryptography.EncryptedValue
import app.keemobile.kotpass.database.Credentials
import app.keemobile.kotpass.database.KeePassDatabase
import app.keemobile.kotpass.database.decode
import app.keemobile.kotpass.database.traverse
import app.keemobile.kotpass.models.CustomIcon
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class CustomIconsSpec : DescribeSpec({

    describe("CustomIcons modifier") {
        it("Properly cleans up invalid references to custom icons") {
            val uuid = UUID.randomUUID()
            val customIcons = mapOf(
                uuid to CustomIcon(byteArrayOf(0x1), null, null)
            )
            val database = KeePassDatabase.decode(
                ClassLoader.getSystemResourceAsStream("ver4_argon2.kdbx")!!,
                Credentials.from(EncryptedValue.fromString("1"))
            ).modifyCustomIcons {
                customIcons
            }.modifyEntries {
                copy(customIconUuid = uuid)
            }.modifyGroups {
                copy(customIconUuid = uuid)
            }
            database.traverse { element ->
                element.customIconUuid shouldBe uuid
            }
            val noCustomIcons = database.modifyCustomIcons { mapOf() }

            noCustomIcons.traverse { element ->
                element.customIconUuid shouldBe null
            }
        }
    }
})
