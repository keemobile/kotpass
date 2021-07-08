package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.resources.ChaChaRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.codec.binary.Base16

private val HexDec = Base16()

class ChaChaSpec : DescribeSpec({

    describe("ChaCha stream cipher") {
        it("Properly outputs key stream") {
            ChaChaRes.ChaChaTestCases.forEach { testCase ->
                val engine = ChaChaEngine(testCase.rounds).apply {
                    init(
                        key = HexDec.decode(testCase.key),
                        iv = HexDec.decode(testCase.iv)
                    )
                }
                val expectedOutput = HexDec.decode(testCase.output)
                engine.getBytes(expectedOutput.size) shouldBe expectedOutput
            }
        }
    }
})
