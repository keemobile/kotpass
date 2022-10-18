package app.keemobile.kotpass.models

import app.keemobile.kotpass.cryptography.EncryptedValue
import app.keemobile.kotpass.database.Credentials
import app.keemobile.kotpass.database.KeePassDatabase
import app.keemobile.kotpass.database.decode
import app.keemobile.kotpass.database.modifiers.binaries
import app.keemobile.kotpass.database.modifiers.modifyEntry
import app.keemobile.kotpass.database.modifiers.removeUnusedBinaries
import app.keemobile.kotpass.extensions.getText
import app.keemobile.kotpass.extensions.parseAsXml
import app.keemobile.kotpass.io.decodeBase64ToArray
import app.keemobile.kotpass.io.encodeBase64
import app.keemobile.kotpass.resources.DatabaseRes
import app.keemobile.kotpass.xml.marshal
import app.keemobile.kotpass.xml.unmarshalBinaries
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.util.*

private const val Contents = "hello kotpass"
private const val ContentsAsXml =
    """<Binaries><Binary ID="0" Compressed="true">H4sIAAAAAAAAAMtIzcnJV8jOLylILC4GACxwuZMNAAAA</Binary></Binaries>"""

class BinariesSpec : DescribeSpec({

    describe("Binaries") {
        it("Binary serialization does not affect compression") {
            BinaryData
                .Uncompressed(false, Contents.toByteArray())
                .marshal(0)
                .getText() shouldBe Contents.toByteArray().encodeBase64()
        }

        it("Binaries are properly decompressed") {
            unmarshalBinaries(ContentsAsXml.parseAsXml())
                .map { (_, binary) -> binary }
                .first()
                .getContent() shouldBe Contents.toByteArray()
        }

        it("Removes unused binaries") {
            val database = KeePassDatabase.decode(
                inputStream = ByteArrayInputStream(DatabaseRes.DbVer4WithBinaries.decodeBase64ToArray()),
                credentials = Credentials.from(EncryptedValue.fromString("1"))
            ).modifyEntry(UUID.fromString("6d9b7812-6d1a-1765-9cd7-c66a93a220e9")) {
                copy(binaries = listOf())
            }.removeUnusedBinaries()

            database.binaries.size shouldBe 0
        }
    }
})
