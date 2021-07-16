package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.cryptography.EncryptionSaltGenerator
import io.github.anvell.kotpass.models.FormatContext
import io.github.anvell.kotpass.models.FormatVersion
import io.github.anvell.kotpass.resources.DefaultXmlContentParserRes
import io.github.anvell.kotpass.resources.MetaRes
import io.github.anvell.kotpass.resources.TimeDataRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DefaultXmlContentParserSpec : DescribeSpec({

    describe("Default Xml content parser") {
        it("Is able to deserialize xml database") {
            val context = FormatContext(
                version = FormatVersion(4, 1),
                encryption = EncryptionSaltGenerator.ChaCha20(byteArrayOf())
            )
            val content = DefaultXmlContentParser.unmarshalContent(
                context = context,
                xmlData = DefaultXmlContentParserRes.BasicXml.toByteArray()
            )
            content.meta.generator shouldBe MetaRes.DummyText
            content.meta.description shouldBe MetaRes.DummyText
            content.meta.nameChanged shouldBe TimeDataRes.ParsedDateTime

            content.group.groups.size shouldBe 1
            content.deletedObjects.size shouldBe 0
        }
    }
})
