package app.keemobile.kotpass.database.header

import app.keemobile.kotpass.constants.CrsAlgorithm
import app.keemobile.kotpass.io.decodeBase64ToArray
import app.keemobile.kotpass.resources.DatabaseRes
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
