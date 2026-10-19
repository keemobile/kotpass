package app.keemobile.kotpass.xml

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import java.io.File
import org.redundent.kotlin.xml.parse

class SecureParsingSpec : DescribeSpec({

    describe("XML parsing of untrusted input") {
        it("Does not resolve external entities") {
            val secret = File.createTempFile("secret", ".txt").apply {
                writeText("TOP-SECRET-CONTENT")
                deleteOnExit()
            }
            val hostile = """
                <?xml version="1.0"?>
                <!DOCTYPE KeyFile [<!ENTITY leak SYSTEM "${secret.toURI()}">]>
                <KeyFile><Key><Data>&leak;</Data></Key></KeyFile>
            """.trimIndent()

            val text = runCatching { parse(hostile.byteInputStream()).toString() }.getOrDefault("")

            text shouldNotContain "TOP-SECRET-CONTENT"
        }

        it("Does not expand entities declared in the document") {
            val laugh = "lol"
            val hostile = """
                <?xml version="1.0"?>
                <!DOCTYPE root [
                  <!ENTITY a "$laugh$laugh$laugh$laugh$laugh$laugh$laugh$laugh$laugh$laugh">
                  <!ENTITY b "&a;&a;&a;&a;&a;&a;&a;&a;&a;&a;">
                  <!ENTITY c "&b;&b;&b;&b;&b;&b;&b;&b;&b;&b;">
                ]>
                <root>&c;</root>
            """.trimIndent()

            val text = runCatching { parse(hostile.byteInputStream()).toString() }.getOrDefault("")

            text shouldNotContain laugh
        }

        it("Still parses an ordinary document with predefined entities") {
            val node = parse("<Root><Value>a &amp; b</Value></Root>".byteInputStream())

            node.toString().contains("a &amp; b") shouldBe true
        }
    }
})
