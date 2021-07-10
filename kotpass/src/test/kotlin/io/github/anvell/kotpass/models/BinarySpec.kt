package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.extensions.getText
import io.github.anvell.kotpass.extensions.parseAsXml
import io.github.anvell.kotpass.xml.marshal
import io.github.anvell.kotpass.xml.unmarshal
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.codec.binary.Base64.encodeBase64String

private const val Contents = "hello kotpass"
private const val ContentsAsXml =
    """<Binary ID="0" Compressed="true">H4sIAAAAAAAAAMtIzcnJV8jOLylILC4GACxwuZMNAAAA</Binary>"""

class BinarySpec : DescribeSpec({

    describe("Binaries") {
        it("Binary serialization does not affect compression") {
            Binary(
                id = 0,
                memoryProtection = false,
                data = BinaryData.Uncompressed(Contents.toByteArray())
            )
                .marshal()
                .getText() shouldBe encodeBase64String(Contents.toByteArray())
        }

        it("Binary is properly decompressed") {
            Binary
                .unmarshal(ContentsAsXml.parseAsXml())
                .data.getContent() shouldBe Contents.toByteArray()
        }
    }
})
