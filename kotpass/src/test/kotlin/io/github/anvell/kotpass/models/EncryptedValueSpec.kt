package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.codec.binary.Base16
import kotlin.experimental.xor

class EncryptedValueSpec : DescribeSpec({

    describe("Encrypted value") {
        val valueBytes = "strvalue".toByteArray()
        val encValueBytes = "strvalue".toByteArray()
        val saltBytes = ByteArray(valueBytes.size)
        for (i in saltBytes.indices) {
            saltBytes[i] = i.toByte()
            encValueBytes[i] = encValueBytes[i] xor i.toByte()
        }

        it("Decrypts salted value in string") {
            EncryptedValue(encValueBytes, saltBytes)
                .text shouldBe "strvalue"
        }

        it("Returns string in binary") {
            EncryptedValue(encValueBytes, saltBytes)
                .getBinary() shouldBe valueBytes
        }

        it("Calculates SHA256 hash") {
            EncryptedValue(encValueBytes, saltBytes)
                .let { Base16().encodeToString(it.getHash()) }
                .also {
                    it shouldBe "1F5C3EF76D43E72EE2C5216C36187C799B153CAB3D0CB63A6F3ECCCC2627F535"
                }
        }

        it("Creates value from string") {
            EncryptedValue
                .fromString("test")
                .text shouldBe "test"
        }

        it("Creates value from binary") {
            EncryptedValue
                .fromBinary("test".toByteArray())
                .text shouldBe "test"
        }

        it("Returns byte length") {
            EncryptedValue
                .fromBinary("test".toByteArray())
                .byteLength shouldBe 4
        }

        it("Can change salt") {
            val value = EncryptedValue.fromString("test")

            value.text shouldBe "test"

            value.setSalt(byteArrayOf(1, 2, 3, 4))

            value.text shouldBe "test"
        }

        it("Returns protected value as base64 string") {
            val value = EncryptedValue
                .fromBinary("test".toByteArray())
            value.setSalt(byteArrayOf(1, 2, 3, 4))

            value.toString() shouldBe "dWdwcA=="
        }

        it("Creates a value from base64") {
            EncryptedValue
                .fromBase64("aGVsbG8=")
                .text shouldBe "hello"
        }

        it("Returns base64 of the value") {
            EncryptedValue
                .fromString("hello")
                .toBase64() shouldBe "aGVsbG8="
        }
    }
})
