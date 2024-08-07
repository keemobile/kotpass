package app.keemobile.kotpass.cryptography

import app.keemobile.kotpass.cryptography.engines.ChaChaEngine
import app.keemobile.kotpass.io.decodeHexToArray
import app.keemobile.kotpass.resources.ChaChaRes
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
