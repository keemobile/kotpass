package app.keemobile.kotpass.models

import app.keemobile.kotpass.cryptography.EncryptedValue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EntryValueSpec : DescribeSpec({

    describe("EntryValue") {
        it("isEmpty check properly evaluates empty string") {
            val emptyPlain = EntryValue.Plain("")
            val emptyEncrypted = EntryValue.Encrypted(EncryptedValue.fromString(""))
            val somePlain = EntryValue.Encrypted(EncryptedValue.fromString("123"))
            val someEncrypted = EntryValue.Encrypted(EncryptedValue.fromString("123"))

            emptyPlain.isEmpty() shouldBe true
            emptyEncrypted.isEmpty() shouldBe true

            somePlain.isEmpty() shouldBe false
            someEncrypted.isEmpty() shouldBe false
        }
    }
})
