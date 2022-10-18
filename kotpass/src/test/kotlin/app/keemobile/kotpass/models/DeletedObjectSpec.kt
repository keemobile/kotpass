package app.keemobile.kotpass.models

import app.keemobile.kotpass.cryptography.EncryptionSaltGenerator
import app.keemobile.kotpass.extensions.parseAsXml
import app.keemobile.kotpass.resources.DeletedObjectRes
import app.keemobile.kotpass.xml.marshal
import app.keemobile.kotpass.xml.unmarshalDeletedObject
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.redundent.kotlin.xml.PrintOptions

class DeletedObjectSpec : DescribeSpec({

    describe("DeletedObject") {
        it("Parsing from Xml string") {
            val root = DeletedObjectRes
                .BasicXml
                .parseAsXml()
            val deletedObject = unmarshalDeletedObject(root)

            deletedObject shouldBe DeletedObjectRes.BasicObject
        }

        it("Uuid is encoded as Base64") {
            val context = XmlContext.Encode(
                version = FormatVersion(4, 0),
                encryption = EncryptionSaltGenerator.ChaCha20(byteArrayOf()),
                binaries = linkedMapOf()
            )

            DeletedObjectRes
                .BasicObject
                .marshal(context)
                .toString(PrintOptions(singleLineTextElements = true))
                .indexOf(DeletedObjectRes.Base64StringUuid) shouldNotBe -1
        }
    }
})
