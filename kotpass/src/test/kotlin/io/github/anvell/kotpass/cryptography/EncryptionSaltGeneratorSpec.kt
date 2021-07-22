package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EncryptionSaltGeneratorSpec : DescribeSpec({

    describe("EncryptionSaltGenerator") {
        it("Generates random sequences with Salsa20") {
            EncryptionSaltGenerator.Salsa20(
                key = byteArrayOf(1, 2, 3),
            ).run {
                getSalt(0).size shouldBe 0

                getSalt(10) shouldBe "q1l4McuyQYDcDg==".decodeBase64ToArray()

                getSalt(10) shouldBe "LJTKXBjqlTS8cg==".decodeBase64ToArray()

                getSalt(20) shouldBe "jKVBKKNUnieRr47Wxh0YTKn82Pw=".decodeBase64ToArray()
            }
        }

        it("Generates random sequences with ChaCha20") {
            EncryptionSaltGenerator.ChaCha20(
                key = byteArrayOf(1, 2, 3),
            ).run {
                getSalt(0).size shouldBe 0

                getSalt(10) shouldBe "iUIv7m2BJN2ubQ==".decodeBase64ToArray()

                getSalt(10) shouldBe "BILRgZKxaxbRzg==".decodeBase64ToArray()

                getSalt(20) shouldBe "KUeBUGjNBYhAoJstSqnMXQwuD6E=".decodeBase64ToArray()
            }
        }
    }
})
