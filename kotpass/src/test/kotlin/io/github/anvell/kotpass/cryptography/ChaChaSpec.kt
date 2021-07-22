package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.io.decodeHexToArray
import io.github.anvell.kotpass.resources.ChaChaRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ChaChaSpec : DescribeSpec({

    describe("ChaCha stream cipher") {
        it("Properly outputs key stream") {
            ChaChaRes.ChaChaTestCases.forEach { testCase ->
                val engine = ChaChaEngine(testCase.rounds).apply {
                    init(
                        key = testCase.key.decodeHexToArray(),
                        iv = testCase.iv.decodeHexToArray()
                    )
                }
                val expectedOutput = testCase.output.decodeHexToArray()
                engine.getBytes(expectedOutput.size) shouldBe expectedOutput
            }
        }
    }
})
