package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.cryptography.KeyTransform
import io.github.anvell.kotpass.io.encodeHex
import io.github.anvell.kotpass.resources.CredentialsRes
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
    }
})
