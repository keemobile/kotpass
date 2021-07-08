package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.extensions.parseAsXml
import io.github.anvell.kotpass.resources.KdbxCustomDataRes
import io.github.anvell.kotpass.xml.CustomData
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.redundent.kotlin.xml.PrintOptions

class CustomDataSpec : DescribeSpec({

    describe("Parsing CustomData from Xml string") {
        it("Basic custom data") {
            val customData = CustomData
                .unmarshal(KdbxCustomDataRes.BasicXml.parseAsXml())

            customData["k1"] shouldBe CustomDataItem("v1")
            customData["k2"] shouldBe CustomDataItem("v2")
        }

        it("Empty custom data") {
            CustomData
                .unmarshal(KdbxCustomDataRes.EmptyTagXml.parseAsXml())
                .isEmpty() shouldBe true
        }

        it("Skips unknown tags") {
            val customData = CustomData
                .unmarshal(KdbxCustomDataRes.UnknownTagsXml.parseAsXml())

            customData.size shouldBe 1
            customData["k1"] shouldBe CustomDataItem("v1")
        }

        it("Skips empty keys") {
            CustomData
                .unmarshal(KdbxCustomDataRes.EmptyKeysXml.parseAsXml())
                .isEmpty() shouldBe true
        }
    }

    describe("Writing CustomData to Xml string") {
        it("Basic custom data") {
            val context = FormatContext(FormatVersion(4, 1))
            val customData = mapOf(
                "k1" to CustomDataItem("v1"),
                "k2" to CustomDataItem("v2")
            )

            CustomData.marshal(context, customData)
                .toString(PrintOptions(singleLineTextElements = true))
                .indexOf("<Key>k1</Key>") shouldNotBe -1
        }
    }
})
