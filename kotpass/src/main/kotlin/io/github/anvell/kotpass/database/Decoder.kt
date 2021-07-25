package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.cryptography.ContentEncryption
import io.github.anvell.kotpass.cryptography.EncryptionSaltGenerator
import io.github.anvell.kotpass.cryptography.KeyTransform
import io.github.anvell.kotpass.database.header.DatabaseHeader
import io.github.anvell.kotpass.database.header.DatabaseInnerHeader
import io.github.anvell.kotpass.database.header.FileHeaders
import io.github.anvell.kotpass.database.header.Signature
import io.github.anvell.kotpass.errors.CryptoError
import io.github.anvell.kotpass.errors.FormatError
import io.github.anvell.kotpass.extensions.teeBuffer
import io.github.anvell.kotpass.models.FormatContext
import io.github.anvell.kotpass.xml.DefaultXmlContentParser
import io.github.anvell.kotpass.xml.XmlContentParser
import okio.Buffer
import okio.BufferedSource
import okio.ByteString.Companion.toByteString
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.GZIPInputStream

fun KeePassDatabase.Companion.decode(
    inputStream: InputStream,
    credentials: Credentials,
    validateHashes: Boolean = true,
    contentParser: XmlContentParser = DefaultXmlContentParser
): KeePassDatabase {
    val headerBuffer = Buffer()

    inputStream.source().teeBuffer(headerBuffer).use { source ->
        val header = DatabaseHeader.readFrom(source)

        if (header.signature.base != Signature.Base) {
            throw FormatError.UnknownFormat("File has unexpected signature.")
        }
        if (header.signature.secondary != Signature.Secondary ||
            header.version.major < MinSupportedVersion ||
            header.version.major > MaxSupportedVersion
        ) {
            throw FormatError.UnsupportedVersion("File version is not supported.")
        }
        val rawHeaderData = headerBuffer.snapshot()
        val transformedKey = KeyTransform.transformedKey(header.fileHeaders, credentials)

        return when (header.fileHeaders) {
            is FileHeaders.Ver3x -> {
                val saltGenerator = with(header.fileHeaders) {
                    EncryptionSaltGenerator.create(innerRandomStreamId, innerRandomStreamKey)
                }
                val context = FormatContext(header.version, saltGenerator)
                val rawContent = decryptRawContent(header, source, transformedKey)
                val content = contentParser.unmarshalContent(context, rawContent)
                val headerHash = content.meta.headerHash?.toByteString()

                if (validateHashes && headerHash != null && headerHash != rawHeaderData.sha256()) {
                    throw FormatError.InvalidHeader("HeaderHash value does not match Sha256 of the header.")
                }
                KeePassDatabase(credentials, header, null, content)
            }
            is FileHeaders.Ver4x -> {
                val expectedSha256 = source.readByteString(32)
                val expectedHmacSha256 = source.readByteString(32)

                if (validateHashes) {
                    if (rawHeaderData.sha256() != expectedSha256) {
                        throw FormatError.InvalidHeader("Header's Sha256 does not match.")
                    }

                    val hmacKey = KeyTransform.hmacKey(
                        masterSeed = header.fileHeaders.masterSeed.toByteArray(),
                        transformedKey = transformedKey
                    )
                    val hmacSha256 = rawHeaderData.hmacSha256(hmacKey.toByteString())
                    if (hmacSha256 != expectedHmacSha256) {
                        throw CryptoError.InvalidKey("Wrong key used for decryption.")
                    }
                }
                val innerHeaderBuffer = Buffer()
                val contentSource = decryptRawContent(header, source, transformedKey)
                    .inputStream()
                    .source()
                    .teeBuffer(innerHeaderBuffer)
                val innerHeader = DatabaseInnerHeader.readFrom(contentSource)
                val saltGenerator = EncryptionSaltGenerator.create(
                    id = innerHeader.randomStreamId,
                    key = innerHeader.randomStreamKey
                )
                val context = FormatContext(header.version, saltGenerator)
                val content = contentParser.unmarshalContent(
                    context = context,
                    source = contentSource.inputStream()
                )

                KeePassDatabase(credentials, header, innerHeader, content)
            }
        }
    }
}

private fun decryptRawContent(
    header: DatabaseHeader,
    source: BufferedSource,
    transformedKey: ByteArray
): ByteArray {
    val masterSeed = header.fileHeaders.masterSeed.toByteArray()
    val encryptedContent = when (header.fileHeaders) {
        is FileHeaders.Ver3x -> source.readByteArray()
        is FileHeaders.Ver4x -> ContentBlocks.readContentBlocksVer4x(
            source = source,
            masterSeed = masterSeed,
            transformedKey = transformedKey
        )
    }

    var decryptedContent = ContentEncryption.decrypt(
        cipherId = header.fileHeaders.cipherId,
        key = KeyTransform.masterKey(masterSeed, transformedKey),
        iv = header.fileHeaders.encryptionIV.toByteArray(),
        data = encryptedContent
    )

    if (header.fileHeaders is FileHeaders.Ver3x) {
        val streamStartBytes = header.fileHeaders.streamStartBytes
        if (!streamStartBytes.rangeEquals(0, decryptedContent, 0, streamStartBytes.size)) {
            FormatError.InvalidContent("Database content could be corrupted or cannot be decrypted.")
        }
        decryptedContent = ContentBlocks.readContentBlocksVer3x(
            decryptedContent
                .inputStream(streamStartBytes.size, decryptedContent.size)
                .source()
                .buffer()
        )
    }

    if (header.fileHeaders.compression == FileHeaders.Compression.GZip) {
        decryptedContent = try {
            GZIPInputStream(ByteArrayInputStream(decryptedContent))
                .use(GZIPInputStream::readBytes)
        } catch (e: IOException) {
            throw FormatError.FailedCompression("Failed to unzip content.")
        }
    }

    return decryptedContent
}
