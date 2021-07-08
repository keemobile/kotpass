package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.resources.Salsa20Res
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.codec.binary.Base16

private val HexDec = Base16()

class Salsa20Spec : DescribeSpec({

    describe("Salsa20 stream cipher") {
        it("Properly encrypts values") {
            Salsa20Res.SalsaTestCases.forEach { testCase ->
                val engine = Salsa20Engine(testCase.rounds).apply {
                    init(
                        key = HexDec.decode(testCase.key),
                        iv = HexDec.decode(testCase.iv)
                    )
                }
                val output = engine.processBytes(HexDec.decode(testCase.plaintext))
                HexDec.encodeToString(output) shouldBe testCase.cipher
            }
        }
    }
})
