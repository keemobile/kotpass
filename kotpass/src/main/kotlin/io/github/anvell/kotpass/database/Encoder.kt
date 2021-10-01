package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.cryptography.ContentEncryption
import io.github.anvell.kotpass.cryptography.EncryptionSaltGenerator
import io.github.anvell.kotpass.cryptography.KeyTransform
import io.github.anvell.kotpass.database.header.DatabaseHeader
import io.github.anvell.kotpass.database.modifiers.binaries
import io.github.anvell.kotpass.models.XmlContext
import io.github.anvell.kotpass.xml.DefaultXmlContentParser
import io.github.anvell.kotpass.xml.XmlContentParser
import okio.Buffer
import okio.BufferedSink
import okio.ByteString.Companion.toByteString
import okio.buffer
import okio.sink
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.zip.GZIPOutputStream

fun KeePassDatabase.encode(
    outputStream: OutputStream,
    contentParser: XmlContentParser = DefaultXmlContentParser
) {
    val transformedKey = KeyTransform.transformedKey(
        header = header,
        credentials = credentials
    )
    val headerBuffer = Buffer().apply {
        header.writeTo(this)
    }
    val headerHash = headerBuffer.sha256()

    var rawContent = when (this) {
        is KeePassDatabase.Ver3x -> {
            val saltGenerator = EncryptionSaltGenerator.create(
                id = header.innerRandomStreamId,
                key = header.innerRandomStreamKey
            )
            val context = XmlContext.Encode(
                version = header.version,
                encryption = saltGenerator,
                binaries = binaries,
                isXmlExport = false
            )
            val newMeta = content.meta.copy(headerHash = headerHash)

            contentParser
                .marshalContent(context, content.copy(meta = newMeta))
                .toByteArray(Charsets.UTF_8)
        }
        is KeePassDatabase.Ver4x -> {
            val saltGenerator = EncryptionSaltGenerator.create(
                id = innerHeader.randomStreamId,
                key = innerHeader.randomStreamKey
            )
            val context = XmlContext.Encode(
                version = header.version,
                encryption = saltGenerator,
                binaries = binaries,
                isXmlExport = false
            )
            val hmacKey = KeyTransform.hmacKey(
                masterSeed = header.masterSeed.toByteArray(),
                transformedKey = transformedKey
            )
            val hmacSha256 = headerBuffer.hmacSha256(hmacKey.toByteString())
            headerBuffer.write(headerHash)
            headerBuffer.write(hmacSha256)

            val rawContent = contentParser
                .marshalContent(context, content)
                .toByteArray(Charsets.UTF_8)

            val contentBuffer = Buffer()
            innerHeader.writeTo(contentBuffer)
            contentBuffer.write(rawContent)

            contentBuffer.readByteArray()
        }
    }

    if (header.compression == DatabaseHeader.Compression.GZip) {
        val gzipStream = ByteArrayOutputStream()
        GZIPOutputStream(gzipStream).use { it.write(rawContent) }
        rawContent = gzipStream.toByteArray()
    }

    outputStream.sink().buffer().use { sink ->
        sink.write(headerBuffer.snapshot().toByteArray())
        sink.writeEncryptedContent(header, rawContent, transformedKey)
    }
}

fun KeePassDatabase.encodeAsXml(
    contentParser: XmlContentParser = DefaultXmlContentParser
): String {
    val saltGenerator = when (this) {
        is KeePassDatabase.Ver3x -> {
            EncryptionSaltGenerator.create(
                id = header.innerRandomStreamId,
                key = header.innerRandomStreamKey
            )
        }
        is KeePassDatabase.Ver4x -> {
            EncryptionSaltGenerator.create(
                id = innerHeader.randomStreamId,
                key = innerHeader.randomStreamKey
            )
        }
    }

    return contentParser.marshalContent(
        context = XmlContext.Encode(
            version = header.version,
            encryption = saltGenerator,
            binaries = binaries,
            isXmlExport = true
        ),
        content = content,
        pretty = true
    )
}

private fun BufferedSink.writeEncryptedContent(
    header: DatabaseHeader,
    rawContent: ByteArray,
    transformedKey: ByteArray
) {
    val masterSeed = header.masterSeed.toByteArray()

    when (header) {
        is DatabaseHeader.Ver3x -> {
            val contentBuffer = Buffer()
            contentBuffer.write(header.streamStartBytes)
            ContentBlocks.writeContentBlocksVer3x(contentBuffer, rawContent)

            val encryptedContent = ContentEncryption.encrypt(
                cipherId = header.cipherId,
                key = KeyTransform.masterKey(masterSeed, transformedKey),
                iv = header.encryptionIV.toByteArray(),
                data = contentBuffer.readByteArray()
            )
            write(encryptedContent)
        }
        is DatabaseHeader.Ver4x -> {
            val encryptedContent = ContentEncryption.encrypt(
                cipherId = header.cipherId,
                key = KeyTransform.masterKey(masterSeed, transformedKey),
                iv = header.encryptionIV.toByteArray(),
                data = rawContent
            )
            ContentBlocks.writeContentBlocksVer4x(
                sink = this,
                contentData = encryptedContent,
                masterSeed = masterSeed,
                transformedKey = transformedKey
            )
        }
    }
}
