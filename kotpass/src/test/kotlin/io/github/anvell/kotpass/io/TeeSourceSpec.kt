@file:Suppress("BlockingMethodInNonBlockingContext")

package io.github.anvell.kotpass.io

import io.github.anvell.kotpass.extensions.teeBuffer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString
import okio.source
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer

private val DummyData = "Lorem ipsum".toByteArray()

class TeeSourceSpec : DescribeSpec({

    describe("TeeBufferedSource") {
        it("Writes data to side buffer") {
            val buffer = Buffer()
            val data = ByteString.of(*DummyData)
            val tee = ByteArrayInputStream(DummyData)
                .source()
                .teeBuffer(buffer)

            tee.read(Buffer(), 5)
            buffer.snapshot() shouldBe data.substring(0, 5)

            tee.read(ByteBuffer.allocate(1))
            buffer.snapshot() shouldBe data.substring(0, 6)

            tee.readFully(ByteArray((data.size - buffer.size).toInt()))
            buffer.snapshot() shouldBe data
        }
    }
})
