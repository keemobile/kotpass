package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.io.decodeHexToArray
import io.github.anvell.kotpass.io.encodeHex
import io.github.anvell.kotpass.resources.Salsa20Res
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class Salsa20Spec : DescribeSpec({

    describe("Salsa20 stream cipher") {
        it("Properly encrypts values") {
            Salsa20Res.SalsaTestCases.forEach { testCase ->
                val engine = Salsa20Engine(testCase.rounds).apply {
                    init(
                        key = testCase.key.decodeHexToArray(),
                        iv = testCase.iv.decodeHexToArray()
                    )
                }
                val output = engine.processBytes(testCase.plaintext.decodeHexToArray())
                output.encodeHex() shouldBe testCase.cipher
            }
        }
    }
})
