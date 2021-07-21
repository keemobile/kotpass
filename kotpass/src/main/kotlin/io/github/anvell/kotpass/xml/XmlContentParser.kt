package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.models.DatabaseContent
import io.github.anvell.kotpass.models.FormatContext
import java.io.InputStream

interface XmlContentParser {
    fun unmarshalContent(context: FormatContext, xmlData: ByteArray): DatabaseContent
    fun unmarshalContent(context: FormatContext, source: InputStream): DatabaseContent
    fun marshalContent(context: FormatContext, content: DatabaseContent): String
}
