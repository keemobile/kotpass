package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.models.DatabaseContent
import io.github.anvell.kotpass.models.XmlContext
import java.io.InputStream

interface XmlContentParser {
    fun unmarshalContent(context: XmlContext.Decode, xmlData: ByteArray): DatabaseContent
    fun unmarshalContent(context: XmlContext.Decode, source: InputStream): DatabaseContent
    fun marshalContent(context: XmlContext.Encode, content: DatabaseContent): String
}
