package app.keemobile.kotpass.database

import app.keemobile.kotpass.database.header.DatabaseHeader
import app.keemobile.kotpass.errors.FormatError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import java.io.ByteArrayInputStream

class ReadHeaderSpec : DescribeSpec({

    describe("KeePassDatabase.readHeader") {
        it("Reads the header of a KDBX 3.x file without the key") {
            ClassLoader.getSystemResourceAsStream("ver3_aes.kdbx")!!.use {
                KeePassDatabase.readHeader(it).shouldBeInstanceOf<DatabaseHeader.Ver3x>()
            }
        }

        it("Reads the header of a KDBX 4.x file without the key") {
            ClassLoader.getSystemResourceAsStream("ver4_argon2.kdbx")!!.use {
                KeePassDatabase.readHeader(it).shouldBeInstanceOf<DatabaseHeader.Ver4x>()
            }
        }

        it("Rejects data that does not start with the KDBX signature") {
            shouldThrow<FormatError.UnknownFormat> {
                KeePassDatabase.readHeader(ByteArrayInputStream(ByteArray(64) { it.toByte() }))
            }
        }
    }
})
