package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.Credentials
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.decode
import io.github.anvell.kotpass.database.modifiers.binaries
import io.github.anvell.kotpass.database.modifiers.modifyEntry
import io.github.anvell.kotpass.database.modifiers.removeUnusedBinaries
import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.extensions.parseAsXml
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.io.encodeBase64
import io.github.anvell.kotpass.resources.DatabaseRes
import io.github.anvell.kotpass.xml.marshal
import io.github.anvell.kotpass.xml.unmarshalBinaries
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
