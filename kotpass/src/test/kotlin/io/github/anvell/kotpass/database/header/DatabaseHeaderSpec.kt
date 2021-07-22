package io.github.anvell.kotpass.database.header

import io.github.anvell.kotpass.resources.DatabaseRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.Buffer
import okio.buffer
import okio.source
import org.apache.commons.codec.binary.Base64.decodeBase64
import java.io.ByteArrayInputStream

class DatabaseHeaderSpec : DescribeSpec({

    describe("Database header") {
        it("Properly reads KDF parameters") {
            val ver4Argon2 = decodeHeader(DatabaseRes.DbVer4Argon2)
            ver4Argon2.signature.base shouldBe Signature.Base
            ver4Argon2.fileHeaders.shouldBeInstanceOf<FileHeaders.Ver4x>()
            with(ver4Argon2.fileHeaders as FileHeaders.Ver4x) {
                kdfParameters.shouldBeInstanceOf<KdfParameters.Argon2>()
            }

            val ver4Aes = decodeHeader(DatabaseRes.DbVer4Aes)
            ver4Aes.fileHeaders.shouldBeInstanceOf<FileHeaders.Ver4x>()
            with(ver4Aes.fileHeaders as FileHeaders.Ver4x) {
                kdfParameters.shouldBeInstanceOf<KdfParameters.Aes>()
            }

            val ver3Aes = decodeHeader(DatabaseRes.DbVer3Aes)
            ver3Aes.fileHeaders.shouldBeInstanceOf<FileHeaders.Ver3x>()
        }

        it("Try to read/write header") {
            val ver4Argon2 = decodeHeader(DatabaseRes.DbVer4Argon2).let { header ->
                val buffer = Buffer()
                header.writeTo(buffer)
                buffer.snapshot()
                    .toByteArray()
                    .inputStream()
                    .source()
                    .buffer()
            }.let(DatabaseHeader.Companion::readFrom)

            ver4Argon2.signature.base shouldBe Signature.Base
            ver4Argon2.fileHeaders.shouldBeInstanceOf<FileHeaders.Ver4x>()
            with(ver4Argon2.fileHeaders as FileHeaders.Ver4x) {
                kdfParameters.shouldBeInstanceOf<KdfParameters.Argon2>()
            }
        }
    }
})

private fun decodeHeader(base64Data: String): DatabaseHeader {
    val bytes = decodeBase64(base64Data)
    return DatabaseHeader.readFrom(ByteArrayInputStream(bytes).source().buffer())
}
