package io.github.anvell.kotpass.extensions

import io.github.anvell.kotpass.xml.FormatXml

internal fun Boolean.toXmlString() = if (this) {
    FormatXml.Values.True
} else {
    FormatXml.Values.False
}
