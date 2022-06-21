package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.Credentials
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.decode
import io.github.anvell.kotpass.database.traverse
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.models.CustomIcon
import io.github.anvell.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.util.*

class CustomIconsSpec : DescribeSpec({

    describe("CustomIcons modifier") {
        it("Properly cleans up invalid references to custom icons") {
            val uuid = UUID.randomUUID()
            val customIcons = mapOf(
                uuid to CustomIcon(byteArrayOf(0x1), null, null)
            )
            val database = KeePassDatabase.decode(
                ByteArrayInputStream(DatabaseRes.DbVer4Argon2.decodeBase64ToArray()),
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
