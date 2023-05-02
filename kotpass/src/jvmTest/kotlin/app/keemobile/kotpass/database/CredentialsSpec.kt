package app.keemobile.kotpass.database

import app.keemobile.kotpass.cryptography.KeyTransform
import app.keemobile.kotpass.io.encodeHex
import app.keemobile.kotpass.resources.CredentialsRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class CredentialsSpec : DescribeSpec({

    describe("Credentials") {
        it("Reads from Xml key file") {
            val (input, output) = CredentialsRes.XmlKeyFileVer1
            val credentials = Credentials.from(input.toByteArray())
            val hex = KeyTransform.compositeKey(credentials).encodeHex()
            hex shouldBe output
        }

        it("Creates Xml key file") {
            val key = ByteArray(32) { 1 }
            val keyfile = Credentials.createKeyfile(key)

            Credentials
                .from(keyfile.toByteArray())
                .key!!
                .getBinary()
                .encodeHex() shouldBe key.encodeHex()
        }
    }
})
