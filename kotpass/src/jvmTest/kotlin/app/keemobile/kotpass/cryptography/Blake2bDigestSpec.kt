package app.keemobile.kotpass.cryptography

import app.keemobile.kotpass.resources.Blake2bDigestRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class Blake2bDigestSpec : DescribeSpec({

    describe("Blake2b digest") {
        it("Properly hashes input") {
            Blake2bDigestRes.TestCases.forEach {
                val output = ByteArray(it.outputLength)
                Blake2bDigest(
                    key = it.key,
                    digestLength = it.outputLength,
                    salt = it.salt,
                    personalization = it.personalization
                ).apply {
                    update(it.input, 0, it.input.size)
                    doFinal(output, 0)
                }

                output shouldBe it.output
            }
        }
    }
})
