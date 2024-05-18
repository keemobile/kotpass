package app.keemobile.kotpass.models

import app.keemobile.kotpass.constants.MemoryProtectionFlag
import app.keemobile.kotpass.extensions.parseAsXml
import app.keemobile.kotpass.resources.MetaRes
import app.keemobile.kotpass.resources.TimeDataRes
import app.keemobile.kotpass.xml.unmarshalMeta
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MetaSpec : DescribeSpec({

    describe("Metadata") {
        it("Properly deserialized from Xml") {
            val meta = unmarshalMeta(MetaRes.BasicXml.parseAsXml())

            meta.generator shouldBe MetaRes.DummyText
            meta.description shouldBe MetaRes.DummyText
            meta.nameChanged shouldBe TimeDataRes.ParsedDateTime
            meta.memoryProtection.containsAll(MemoryProtectionFlag.entries) shouldBe true
            meta.recycleBinEnabled shouldBe false
            meta.binaries.values.first().shouldBeInstanceOf<BinaryData.Uncompressed>()
            meta.binaries.values.first().getContent() shouldBe MetaRes.DummyText.toByteArray()
        }
    }
})
