package app.keemobile.kotpass.xml

import app.keemobile.kotpass.models.DatabaseContent
import app.keemobile.kotpass.models.Meta
import app.keemobile.kotpass.models.XmlContext
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
        content: DatabaseContent,
        pretty: Boolean = false
    ): String
}
