package app.keemobile.kotpass.models

import app.keemobile.kotpass.builders.buildEntry
import app.keemobile.kotpass.constants.BasicField
import app.keemobile.kotpass.cryptography.EncryptionSaltGenerator
import app.keemobile.kotpass.extensions.parseAsXml
import app.keemobile.kotpass.resources.EntryRes
import app.keemobile.kotpass.resources.TimeDataRes
import app.keemobile.kotpass.xml.unmarshalEntry
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

class EntrySpec : DescribeSpec({

    describe("Entry") {
        it("Properly deserialized from Xml") {
            val context = XmlContext.Decode(
                version = FormatVersion(4, 1),
                encryption = EncryptionSaltGenerator.ChaCha20(byteArrayOf()),
                binaries = linkedMapOf()
            )
            val entry = unmarshalEntry(context, EntryRes.BasicXml.parseAsXml())

            entry.tags.size shouldBe 3
            entry.tags.first() shouldBe "lorem"
            entry.fields["custom1"] shouldNotBe null
            entry.fields["custom2"] shouldNotBe null
            entry.times shouldNotBe null
            entry.times?.creationTime shouldBe TimeDataRes.ParsedDateTime
            entry.history.size shouldBe 1
            entry.binaries.size shouldBe 0
        }

        it("New instance should have basic fields") {
            val entry = Entry(
                uuid = UUID.randomUUID(),
                times = TimeData.create()
            )

            entry.fields.keys shouldContainAll BasicField.keys
        }

        it("Different order in fields should be reflected in equality check") {
            val uuid = UUID.randomUUID()
            val someValue = EntryValue.Plain("Some")
            val numberOfItems = 3
            val items = (0..numberOfItems).associate { "$it" to someValue }
            val reversed = (numberOfItems downTo 0).associate { "$it" to someValue }
            val entry1 = buildEntry(uuid) {
                times = null
                fields.putAll(items)
            }
            val entry2 = buildEntry(uuid) {
                times = null
                fields.putAll(reversed)
            }

            items shouldBe reversed
            entry1 shouldNotBe entry2
        }
    }
})
