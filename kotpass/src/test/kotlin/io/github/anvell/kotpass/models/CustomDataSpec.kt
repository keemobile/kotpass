package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.extensions.parseAsXml
import io.github.anvell.kotpass.resources.CustomDataRes
import io.github.anvell.kotpass.xml.CustomData
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.redundent.kotlin.xml.PrintOptions

class CustomDataSpec : DescribeSpec({

    describe("Parsing CustomData from Xml string") {
        it("Basic custom data") {
            val customData = CustomData
                .unmarshal(CustomDataRes.BasicXml.parseAsXml())

            customData["k1"] shouldBe CustomDataValue("v1")
            customData["k2"] shouldBe CustomDataValue("v2")
        }

        it("Empty custom data") {
            CustomData
                .unmarshal(CustomDataRes.EmptyTagXml.parseAsXml())
                .isEmpty() shouldBe true
        }

        it("Skips unknown tags") {
            val customData = CustomData
                .unmarshal(CustomDataRes.UnknownTagsXml.parseAsXml())

            customData.size shouldBe 1
            customData["k1"] shouldBe CustomDataValue("v1")
        }

        it("Skips empty keys") {
            CustomData
                .unmarshal(CustomDataRes.EmptyKeysXml.parseAsXml())
                .isEmpty() shouldBe true
        }
    }

    describe("Writing CustomData to Xml string") {
        it("Basic custom data") {
            val context = XmlContext.Encode(
                version = FormatVersion(4, 1)
            )
            val customData = mapOf(
                "k1" to CustomDataValue("v1"),
                "k2" to CustomDataValue("v2")
            )

            CustomData.marshal(context, customData)
                .toString(PrintOptions(singleLineTextElements = true))
                .indexOf("<Key>k1</Key>") shouldNotBe -1
        }
    }
})
