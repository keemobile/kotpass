package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.extensions.parseAsXml
import io.github.anvell.kotpass.resources.TimeDataRes
import io.github.anvell.kotpass.xml.marshal
import io.github.anvell.kotpass.xml.unmarshalTimeData
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.redundent.kotlin.xml.PrintOptions

class TimeDataSpec : DescribeSpec({

    describe("Parsing DateTime from Xml string") {
        it("Date time in ISO text format") {
            val root = TimeDataRes
                .getBaseXml(TimeDataRes.DateTimeText)
                .parseAsXml()
            val times = unmarshalTimeData(root)

            times.creationTime shouldBe TimeDataRes.ParsedDateTime
            times.lastAccessTime shouldBe TimeDataRes.ParsedDateTime
            times.expiryTime shouldBe null
        }

        it("Date time in binary timestamp") {
            val root = TimeDataRes
                .getBaseXml(TimeDataRes.Base64BinaryDateTimeText)
                .parseAsXml()
            val times = unmarshalTimeData(root)

            times.creationTime?.toString() shouldBe TimeDataRes.DateTimeText
            times.lastAccessTime?.toString() shouldBe TimeDataRes.DateTimeText
            times.expiryTime shouldBe null
        }
    }

    describe("Writing DateTime to Xml string") {
        it("Using text format") {
            val context = XmlContext.Encode(
                version = FormatVersion(3, 1),
                binaries = linkedMapOf()
            )
            val times = TimeData(
                creationTime = TimeDataRes.ParsedDateTime,
                lastAccessTime = TimeDataRes.ParsedDateTime,
                lastModificationTime = TimeDataRes.ParsedDateTime,
                locationChanged = TimeDataRes.ParsedDateTime,
                expiryTime = TimeDataRes.ParsedDateTime
            )

            times.marshal(context)
                .toString(PrintOptions(singleLineTextElements = true))
                .indexOf(TimeDataRes.DateTimeText) shouldNotBe -1
        }

        it("Using binary format") {
            val context = XmlContext.Encode(
                version = FormatVersion(4, 0),
                binaries = linkedMapOf()
            )
            val times = TimeData(
                creationTime = TimeDataRes.ParsedDateTime,
                lastAccessTime = TimeDataRes.ParsedDateTime,
                lastModificationTime = TimeDataRes.ParsedDateTime,
                locationChanged = TimeDataRes.ParsedDateTime,
                expiryTime = TimeDataRes.ParsedDateTime
            )

            times.marshal(context)
                .toString(PrintOptions(singleLineTextElements = true))
                .indexOf(TimeDataRes.Base64BinaryDateTimeText) shouldNotBe -1
        }
    }
})
