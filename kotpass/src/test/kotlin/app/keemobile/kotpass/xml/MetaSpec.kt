package app.keemobile.kotpass.xml

import app.keemobile.kotpass.common.renderTestXmlString
import app.keemobile.kotpass.cryptography.EncryptionSaltGenerator
import app.keemobile.kotpass.models.FormatVersion
import app.keemobile.kotpass.models.XmlContext
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.redundent.kotlin.xml.parse

class MetaSpec : DescribeSpec({

    describe("Meta XML") {
        it("Deserialize XML") {
            val document = ClassLoader
                .getSystemResourceAsStream("xml/meta.xml")!!
                .use(::parse)
            val meta = unmarshalMeta(document)

            meta.generator shouldBe "None"
            meta.maintenanceHistoryDays shouldBe 365U
        }

        it("Serialize XML") {
            val context = XmlContext.Encode(
                version = FormatVersion(4, 1),
                encryption = EncryptionSaltGenerator.ChaCha20(byteArrayOf()),
                binaries = linkedMapOf(),
                isXmlExport = true
            )
            val resourceStream = { ClassLoader.getSystemResourceAsStream("xml/meta.xml")!! }
            val document = resourceStream().use(::parse)
            val rawData = resourceStream().readAllBytes().decodeToString()
            val meta = unmarshalMeta(document)

            renderTestXmlString(meta.marshal(context)) shouldBe rawData
        }
    }
})
