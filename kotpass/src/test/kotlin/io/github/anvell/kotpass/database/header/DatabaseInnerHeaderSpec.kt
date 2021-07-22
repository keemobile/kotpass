package io.github.anvell.kotpass.database.header

import io.github.anvell.kotpass.constants.CrsAlgorithm
import io.github.anvell.kotpass.io.decodeBase64ToArray
import io.github.anvell.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream

class DatabaseInnerHeaderSpec : DescribeSpec({

    describe("Database inner header") {
        it("Properly reads and writes data") {
            val buffer = Buffer()
            val data = DatabaseRes.InnerHeaderWithBinaries.decodeBase64ToArray()
            val source = ByteArrayInputStream(data)
                .source()
                .buffer()

            var innerHeader = DatabaseInnerHeader.readFrom(source)
            innerHeader.randomStreamId shouldBe CrsAlgorithm.ChaCha20
            innerHeader.binaries.size shouldBe 2

            innerHeader.writeTo(buffer)
            innerHeader = DatabaseInnerHeader.readFrom(buffer)
            innerHeader.randomStreamId shouldBe CrsAlgorithm.ChaCha20
            innerHeader.binaries.size shouldBe 2
        }
    }
})
