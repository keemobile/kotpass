package app.keemobile.kotpass.database

import app.keemobile.kotpass.resources.ContentBlocksRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import okio.Buffer
import kotlin.random.Random

class ContentBlocksSpec : DescribeSpec({

    describe("Content blocks IO") {
        it("Reads/writes SHA content blocks") {
            val buffer = Buffer()
            ContentBlocks.writeContentBlocksVer3x(
                sink = buffer,
                contentData = ContentBlocksRes.TestData.toByteArray()
            )
            val output = ContentBlocks
                .readContentBlocksVer3x(buffer)
                .toString(Charsets.UTF_8)

            output.indexOf(ContentBlocksRes.TestSentence) shouldNotBe -1
        }

        it("Reads/writes HMAC content blocks") {
            val seed = Random.nextBytes(32)
            val key = Random.nextBytes(32)

            val buffer = Buffer()
            ContentBlocks.writeContentBlocksVer4x(
                sink = buffer,
                contentData = ContentBlocksRes.TestData.toByteArray(),
                masterSeed = seed,
                transformedKey = key,
            )
            val output = ContentBlocks
                .readContentBlocksVer4x(buffer, seed, key)
                .toString(Charsets.UTF_8)

            output.indexOf(ContentBlocksRes.TestSentence) shouldNotBe -1
        }
    }
})
