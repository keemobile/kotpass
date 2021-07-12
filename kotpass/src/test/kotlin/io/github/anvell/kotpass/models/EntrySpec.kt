package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.cryptography.EncryptionSaltGenerator
import io.github.anvell.kotpass.extensions.parseAsXml
import io.github.anvell.kotpass.resources.EntryRes
import io.github.anvell.kotpass.resources.TimeDataRes
import io.github.anvell.kotpass.xml.unmarshalEntry
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.*

class EntrySpec : DescribeSpec({

    describe("Entry") {
        it("Properly deserialized from Xml") {
            val context = FormatContext(
                version = FormatVersion(4, 1),
                encryption = EncryptionSaltGenerator.ChaCha20(byteArrayOf())
            )
            val entry = unmarshalEntry(context, EntryRes.BasicXml.parseAsXml())

            entry.tags.size shouldBe 3
            entry.tags.first() shouldBe "lorem"
            entry.fields["custom1"] shouldNotBe null
            entry.fields["custom2"] shouldNotBe null
            entry.times shouldNotBe null
            entry.times?.creationTime shouldBe TimeDataRes.ParsedDateTime
            entry.history.size shouldBe 1
            entry.binaries.size shouldBe 1
        }
    }
})
