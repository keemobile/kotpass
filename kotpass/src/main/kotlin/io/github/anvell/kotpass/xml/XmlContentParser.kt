package io.github.anvell.kotpass.xml

import io.github.anvell.kotpass.models.DatabaseContent
import io.github.anvell.kotpass.models.Meta
import io.github.anvell.kotpass.models.XmlContext
import java.io.InputStream

interface XmlContentParser {

    fun unmarshalContent(
        xmlData: ByteArray,
        contextBlock: (Meta) -> XmlContext.Decode
    ): DatabaseContent

    fun unmarshalContent(
        source: InputStream,
        contextBlock: (Meta) -> XmlContext.Decode
    ): DatabaseContent

    fun marshalContent(
        context: XmlContext.Encode,
        content: DatabaseContent
    ): String
}
