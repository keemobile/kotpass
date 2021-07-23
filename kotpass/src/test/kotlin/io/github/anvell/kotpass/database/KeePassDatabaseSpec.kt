package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import java.io.ByteArrayInputStream

class KeePassDatabaseSpec : DescribeSpec({

    describe("Database decoder") {
        it("Reads KeePass 3.x file") {
            val database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbVer3Aes.decodeBase64ToArray()),
                credentials = Credentials.Companion.from(EncryptedValue.fromString("1"))
            )
            print(database.content.group)
        }

        it("Reads KeePass 4.x file") {
            val database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbVer4WithBinaries.decodeBase64ToArray()),
                credentials = Credentials.Companion.from(EncryptedValue.fromString("1"))
            )
            print(database.content.group)
        }
    }
})
