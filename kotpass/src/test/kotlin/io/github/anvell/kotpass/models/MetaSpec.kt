package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.constants.MemoryProtectionFlag
import io.github.anvell.kotpass.extensions.parseAsXml
import io.github.anvell.kotpass.resources.MetaRes
import io.github.anvell.kotpass.resources.TimeDataRes
import io.github.anvell.kotpass.xml.unmarshalMeta
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
            meta.memoryProtection.containsAll(MemoryProtectionFlag.values().toList()) shouldBe true
            meta.recycleBinEnabled shouldBe false
            meta.binaries.values.first().shouldBeInstanceOf<BinaryData.Uncompressed>()
            meta.binaries.values.first().getContent() shouldBe MetaRes.DummyText.toByteArray()
        }
    }
})
