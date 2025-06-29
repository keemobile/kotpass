package org.redundent.kotlin.xml

import org.apache.commons.lang3.builder.HashCodeBuilder

/**
 * Similar to a [TextElement] except that the inner text is wrapped inside a `<![CDATA[]]>` tag.
 */
class CDATAElement internal constructor(text: String) : TextElement(text) {
    override fun renderedText(printOptions: PrintOptions): String {
        return "<![CDATA[${text.escapeCData()}]]>"
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is CDATAElement
    }

    // Need to use javaClass here to avoid a normal TextElement and a CDATAElement having the same hashCode if they have
    // the same text
    override fun hashCode(): Int = HashCodeBuilder()
        .appendSuper(super.hashCode())
        .append(javaClass.hashCode())
        .toHashCode()

    private fun String.escapeCData(): String {
        val cdataEnd = "]]>"
        val cdataStart = "<![CDATA["

        // Split `cdataEnd` into two pieces so XML parser doesn't recognize it
        return replace(cdataEnd, "]]$cdataEnd$cdataStart>")
    }
}
