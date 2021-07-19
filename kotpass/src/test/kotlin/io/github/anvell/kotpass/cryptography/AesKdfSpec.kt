package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.database.Credentials
import io.github.anvell.kotpass.extensions.b
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class AesKdfSpec : DescribeSpec({

    describe("Aes key derivation") {
        it("Transforms key values as expected 1") {
            val result = AesKdf.transformKey(
                key = "8ee89711330c1ccf39a2e65ad12bbd7d".toByteArray(),
                seed = "a25ca73c7189e2a2ca5acf2088b57e28".toByteArray(),
                rounds = 6000UL
            )
            val expected = intArrayOf(
                0x2f, 0xfa, 0x2c, 0x11, 0xeb, 0x4a, 0xcc, 0xe3,
                0x45, 0xd9, 0x9b, 0x53, 0xab, 0x4b, 0x71, 0x9c,
                0xbe, 0x3a, 0x8c, 0x80, 0x99, 0x6f, 0xc7, 0xae,
                0xb5, 0xde, 0x76, 0xef, 0x3e, 0x4c, 0x2d, 0x57
            ).map(Int::toByte)

            result shouldBe expected
        }

        it("Transforms key values as expected 2") {
            val credentials = Credentials.from(EncryptedValue.fromString("secret"))
            val seed = ByteArray(32) { 0x1.b }
            val result = AesKdf.transformKey(
                key = KeyTransform.compositeKey(credentials),
                seed = seed,
                rounds = 10UL
            )
            val expected = intArrayOf(
                208, 2, 238, 193, 16, 181, 39, 109, 254, 40,
                67, 20, 154, 21, 202, 174, 234, 11, 183, 136,
                22, 136, 58, 102, 52, 40, 129, 244, 194, 223,
                211, 108
            ).map(Int::toByte)

            result shouldBe expected
        }
    }
})
