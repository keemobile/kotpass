package app.keemobile.kotpass.database

import app.keemobile.kotpass.cryptography.EncryptedValue
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldContain

class UnknownElementsSpec : DescribeSpec({

    describe("Unknown XML elements") {
        it("Survive a decode and encode round trip") {
            val credentials = Credentials.from(EncryptedValue.fromString("1"))
            val original = KeePassDatabase.decode(
                inputStream = ClassLoader.getSystemResourceAsStream("ver4_with_binaries.kdbx")!!,
                credentials = credentials
            )
            val withUnknown = original
                .encodeAsXml()
                .replaceFirst("<Meta>", "<Meta><FutureMeta>m</FutureMeta>")
                .replaceFirst("<Root>", "<Root><FutureRoot>r</FutureRoot>")
                .replaceFirst("<Group>", "<Group><FutureGroup>g</FutureGroup>")
                .replaceFirst("<Entry>", "<Entry><FutureEntry>e</FutureEntry>")
            val reread = KeePassDatabase.decodeFromXml(
                inputStream = withUnknown.byteInputStream(),
                credentials = credentials
            )

            val written = reread.encodeAsXml()

            written shouldContain "<FutureMeta>m</FutureMeta>"
            written shouldContain "<FutureRoot>r</FutureRoot>"
            written shouldContain "<FutureGroup>g</FutureGroup>"
            written shouldContain "<FutureEntry>e</FutureEntry>"
        }
    }
})
