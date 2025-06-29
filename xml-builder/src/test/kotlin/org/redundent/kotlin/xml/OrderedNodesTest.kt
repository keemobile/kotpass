package org.redundent.kotlin.xml

import org.junit.Test

class OrderedNodesTest : TestBase() {
    @Test
    fun correctOrder() {
        val xml = structured {
            second()
            first()
        }

        validate(xml)
    }

    @XmlType(childOrder = ["first", "second"])
    inner class Structured internal constructor() : Node("xml") {
        fun first() = element("first")

        fun second() = element("second")
    }

    private fun structured(block: Structured.() -> Unit): Structured {
        return Structured().apply(block)
    }
}
