package io.github.anvell.kotpass.cryptography

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.codec.binary.Base64.decodeBase64

class EncryptionSaltGeneratorSpec : DescribeSpec({

    describe("EncryptionSaltGenerator") {
        it("Generates random sequences with Salsa20") {
            EncryptionSaltGenerator.Salsa20(
                key = byteArrayOf(1, 2, 3),
            ).run {
                getSalt(0).size shouldBe 0

                getSalt(10) shouldBe decodeBase64("q1l4McuyQYDcDg==")

                getSalt(10) shouldBe decodeBase64("LJTKXBjqlTS8cg==")

                getSalt(20) shouldBe decodeBase64("jKVBKKNUnieRr47Wxh0YTKn82Pw=")
            }
        }

        it("Generates random sequences with ChaCha20") {
            EncryptionSaltGenerator.ChaCha20(
                key = byteArrayOf(1, 2, 3),
            ).run {
                getSalt(0).size shouldBe 0

                getSalt(10) shouldBe decodeBase64("iUIv7m2BJN2ubQ==")

                getSalt(10) shouldBe decodeBase64("BILRgZKxaxbRzg==")

                getSalt(20) shouldBe decodeBase64("KUeBUGjNBYhAoJstSqnMXQwuD6E=")
            }
        }
    }
})
