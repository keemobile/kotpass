package app.keemobile.kotpass.xml

import app.keemobile.kotpass.common.renderTestXmlString
import app.keemobile.kotpass.models.CustomDataValue
import app.keemobile.kotpass.models.CustomIcon
import app.keemobile.kotpass.models.FormatVersion
import app.keemobile.kotpass.models.XmlContext
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.util.UUID

class AbsentModificationTimeSpec : DescribeSpec({

    val context = XmlContext.Encode.Plain(
        version = FormatVersion(4, 1),
        binaries = linkedMapOf(),
        memoryProtectionFlags = emptySet()
    )

    describe("A modification time that was absent") {
        it("Is not written for a custom icon") {
            val icons = mapOf(UUID.randomUUID() to CustomIcon("icon".toByteArray(), null, null))

            val xml = renderTestXmlString(CustomIcons.marshal(context, icons))

            xml shouldContain "<Data>"
            xml shouldNotContain "LastModificationTime"
        }

        it("Is not written for a custom data item") {
            val xml = renderTestXmlString(CustomData.marshal(context, mapOf("k" to CustomDataValue("v"))))

            xml shouldContain "<Key>k</Key>"
            xml shouldNotContain "LastModificationTime"
        }
    }
})
