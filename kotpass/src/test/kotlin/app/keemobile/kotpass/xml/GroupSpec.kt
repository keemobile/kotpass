package app.keemobile.kotpass.xml

import app.keemobile.kotpass.common.renderTestXmlString
import app.keemobile.kotpass.cryptography.EncryptionSaltGenerator
import app.keemobile.kotpass.models.FormatVersion
import app.keemobile.kotpass.models.XmlContext
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.redundent.kotlin.xml.parse

class GroupSpec : DescribeSpec({

    describe("Group XML") {
        it("Deserialize XML") {
            val context = XmlContext.Decode(
                version = FormatVersion(4, 1),
                encryption = EncryptionSaltGenerator.ChaCha20(byteArrayOf()),
                binaries = linkedMapOf()
            )
            val document = ClassLoader
                .getSystemResourceAsStream("xml/group.xml")!!
                .use(::parse)
            val group = unmarshalGroup(context, document)

            group.groups.shouldNotBeEmpty()
            group.groups.first().groups.shouldNotBeEmpty()
        }

        it("Serialize XML") {
            val version = FormatVersion(4, 1)
            val encryption = EncryptionSaltGenerator.ChaCha20(byteArrayOf())
            val encodeCtx = XmlContext.Encode(version, encryption, linkedMapOf(), true)
            val decodeCtx = XmlContext.Decode(version, encryption, linkedMapOf())
            val resourceStream = { ClassLoader.getSystemResourceAsStream("xml/group.xml")!! }
            val document = resourceStream().use(::parse)
            val rawData = resourceStream().readAllBytes().decodeToString()
            val group = unmarshalGroup(decodeCtx, document)

            renderTestXmlString(group.marshal(encodeCtx)) shouldBe rawData
        }
    }
})
