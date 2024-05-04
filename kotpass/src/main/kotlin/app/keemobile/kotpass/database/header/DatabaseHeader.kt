package app.keemobile.kotpass.database.header

import app.keemobile.kotpass.constants.CrsAlgorithm
import app.keemobile.kotpass.constants.HeaderFieldId
import app.keemobile.kotpass.constants.KdfConst
import app.keemobile.kotpass.cryptography.Argon2Engine
import app.keemobile.kotpass.errors.FormatError
import app.keemobile.kotpass.extensions.asIntLe
import app.keemobile.kotpass.extensions.asLongLe
import app.keemobile.kotpass.extensions.asUuid
import app.keemobile.kotpass.extensions.nextByteString
import app.keemobile.kotpass.models.FormatVersion
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.UUID

private val EndOfHeaderBytes = ByteString.of(0x0D, 0x0A, 0x0D, 0x0A)

sealed class DatabaseHeader {
    abstract val signature: Signature
    abstract val version: FormatVersion
    abstract val cipherId: CipherId
    abstract val compression: Compression
    abstract val masterSeed: ByteString
    abstract val encryptionIV: ByteString

    data class Ver3x(
        override val signature: Signature,
        override val version: FormatVersion,
        override val cipherId: CipherId,
        override val compression: Compression,
        override val masterSeed: ByteString,
        override val encryptionIV: ByteString,
        val transformSeed: ByteString,
        val transformRounds: ULong,
        val innerRandomStreamId: CrsAlgorithm,
        val innerRandomStreamKey: ByteString,
        val streamStartBytes: ByteString
    ) : DatabaseHeader() {
        companion object {
            /**
             * Create an instance of [DatabaseHeader] with the default parameters.
             */
            fun create(random: SecureRandom = SecureRandom()) = with(random) {
                Ver3x(
                    signature = Signature.Default,
                    version = FormatVersion(3, 1),
                    cipherId = CipherId.Aes,
                    compression = Compression.GZip,
                    masterSeed = nextByteString(32),
                    encryptionIV = nextByteString(CipherId.Aes.ivLength),
                    transformSeed = nextByteString(32),
                    transformRounds = 6000U,
                    innerRandomStreamId = CrsAlgorithm.Salsa20,
                    innerRandomStreamKey = nextByteString(32),
                    streamStartBytes = nextByteString(32)
                )
            }
        }
    }

    data class Ver4x(
        override val signature: Signature,
        override val version: FormatVersion,
        override val cipherId: CipherId,
        override val compression: Compression,
        override val masterSeed: ByteString,
        override val encryptionIV: ByteString,
        val kdfParameters: KdfParameters,
        val publicCustomData: Map<String, VariantItem>
    ) : DatabaseHeader() {
        companion object {
            /**
             * Create an instance of [DatabaseHeader] with the default parameters.
             */
            fun create(random: SecureRandom = SecureRandom()) = with(random) {
                Ver4x(
                    signature = Signature.Default,
                    version = FormatVersion(4, 1),
                    cipherId = CipherId.Aes,
                    compression = Compression.GZip,
                    masterSeed = nextByteString(32),
                    encryptionIV = nextByteString(CipherId.Aes.ivLength),
                    kdfParameters = KdfParameters.Argon2(
                        uuid = KdfConst.KdfArgon2d,
                        salt = nextByteString(32),
                        parallelism = 2U,
                        memory = 32UL * 1024UL * 1024UL,
                        iterations = 8U,
                        version = Argon2Engine.Version.Ver13.id.toUInt(),
                        secretKey = null,
                        associatedData = null
                    ),
                    publicCustomData = mapOf()
                )
            }
        }
    }

    enum class CipherId(val uuid: UUID, val ivLength: Int) {
        Aes(
            uuid = UUID.fromString("31c1f2e6-bf71-4350-be58-05216afc5aff"),
            ivLength = 16
        ),
        ChaCha20(
            uuid = UUID.fromString("d6038a2b-8b6f-4cb5-a524-339a31dbb59a"),
            ivLength = 12
        )
    }

    enum class Compression {
        None,
        GZip
    }

    internal fun writeTo(sink: BufferedSink) {
        signature.writeTo(sink)
        version.writeTo(sink)

        writeHeaderValue(sink, HeaderFieldId.CipherId, 16) {
            val buffer = ByteBuffer.allocate(16).apply {
                putLong(cipherId.uuid.mostSignificantBits)
                putLong(cipherId.uuid.leastSignificantBits)
            }
            write(buffer.array())
        }
        writeHeaderValue(sink, HeaderFieldId.Compression, Int.SIZE_BYTES) {
            writeIntLe(compression.ordinal)
        }
        writeHeaderValue(sink, HeaderFieldId.MasterSeed, masterSeed.size) {
            write(masterSeed)
        }
        writeHeaderValue(sink, HeaderFieldId.EncryptionIV, encryptionIV.size) {
            write(encryptionIV)
        }

        when (this) {
            is Ver3x -> {
                writeHeaderValue(sink, HeaderFieldId.TransformSeed, transformSeed.size) {
                    write(transformSeed)
                }
                writeHeaderValue(sink, HeaderFieldId.TransformRounds, Long.SIZE_BYTES) {
                    writeLongLe(transformRounds.toLong())
                }
                writeHeaderValue(sink, HeaderFieldId.InnerRandomStreamId, Int.SIZE_BYTES) {
                    writeIntLe(innerRandomStreamId.ordinal)
                }
                writeHeaderValue(
                    sink,
                    HeaderFieldId.InnerRandomStreamKey,
                    innerRandomStreamKey.size
                ) {
                    write(innerRandomStreamKey)
                }
                writeHeaderValue(sink, HeaderFieldId.StreamStartBytes, streamStartBytes.size) {
                    write(streamStartBytes)
                }
            }
            is Ver4x -> {
                val params = kdfParameters.writeToByteString()
                writeHeaderValue(sink, HeaderFieldId.KdfParameters, params.size) {
                    write(params)
                }
                val customData = VariantDictionary.writeToByteString(publicCustomData)
                writeHeaderValue(sink, HeaderFieldId.PublicCustomData, customData.size) {
                    write(customData)
                }
            }
        }

        writeHeaderValue(sink, HeaderFieldId.EndOfHeader, EndOfHeaderBytes.size) {
            write(EndOfHeaderBytes)
        }
    }

    private inline fun writeHeaderValue(
        sink: BufferedSink,
        id: HeaderFieldId,
        length: Int,
        block: BufferedSink.() -> Unit
    ) {
        sink.writeByte(id.ordinal)
        if (this is Ver4x) {
            sink.writeIntLe(length)
        } else {
            sink.writeShortLe(length)
        }
        block(sink)
    }

    companion object {
        internal fun readFrom(source: BufferedSource): DatabaseHeader {
            var cipherId: CipherId? = null
            var compression: Compression? = null
            var masterSeed: ByteString? = null
            var transformSeed: ByteString? = null
            var transformRounds: ULong? = null
            var encryptionIV: ByteString? = null
            var innerRandomStreamKey: ByteString? = null
            var streamStartBytes: ByteString? = null
            var innerRandomStreamID: CrsAlgorithm? = null
            var kdfParameters: KdfParameters? = null
            var publicCustomData: Map<String, VariantItem> = mapOf()

            val signature = Signature.readFrom(source)
            val version = FormatVersion.readFrom(source)

            while (true) {
                val (id, data) = readHeaderValue(source, version)
                val fieldId = HeaderFieldId
                    .values()
                    .getOrNull(id)
                    ?: throw FormatError.InvalidHeader("Unsupported header field ID.")

                when (fieldId) {
                    HeaderFieldId.EndOfHeader -> break
                    HeaderFieldId.Comment -> Unit
                    HeaderFieldId.CipherId -> {
                        cipherId = data.asUuid().let { cipherUuid ->
                            CipherId.values().firstOrNull { it.uuid == cipherUuid }
                        }
                    }
                    HeaderFieldId.Compression -> {
                        compression = Compression.values()[data.asIntLe()]
                    }
                    HeaderFieldId.MasterSeed -> masterSeed = data
                    HeaderFieldId.TransformSeed -> transformSeed = data
                    HeaderFieldId.TransformRounds -> transformRounds = data.asLongLe().toULong()
                    HeaderFieldId.EncryptionIV -> encryptionIV = data
                    HeaderFieldId.InnerRandomStreamKey -> innerRandomStreamKey = data
                    HeaderFieldId.StreamStartBytes -> streamStartBytes = data
                    HeaderFieldId.InnerRandomStreamId -> {
                        innerRandomStreamID = CrsAlgorithm.values()[data.asIntLe()]
                    }
                    HeaderFieldId.KdfParameters -> {
                        kdfParameters = KdfParameters.readFrom(data)
                    }
                    HeaderFieldId.PublicCustomData -> {
                        publicCustomData = VariantDictionary.readFrom(data)
                    }
                }
            }

            return if (version.major < 4) {
                Ver3x(
                    signature = signature,
                    version = version,
                    cipherId = cipherId
                        ?: throw FormatError.InvalidHeader("No cipher ID."),
                    compression = compression
                        ?: throw FormatError.InvalidHeader("No compression."),
                    masterSeed = masterSeed
                        ?: throw FormatError.InvalidHeader("No master seed."),
                    encryptionIV = encryptionIV
                        ?: throw FormatError.InvalidHeader("No encryption IV."),
                    transformSeed = transformSeed
                        ?: throw FormatError.InvalidHeader("No transform seed."),
                    transformRounds = transformRounds
                        ?: throw FormatError.InvalidHeader("No transform rounds."),
                    innerRandomStreamId = innerRandomStreamID
                        ?: throw FormatError.InvalidHeader("No inner random stream ID."),
                    innerRandomStreamKey = innerRandomStreamKey
                        ?: throw FormatError.InvalidHeader("No protected stream key."),
                    streamStartBytes = streamStartBytes
                        ?: throw FormatError.InvalidHeader("No stream start bytes.")
                )
            } else {
                Ver4x(
                    signature = signature,
                    version = version,
                    cipherId = cipherId
                        ?: throw FormatError.InvalidHeader("No cipher ID."),
                    compression = compression
                        ?: throw FormatError.InvalidHeader("No compression."),
                    masterSeed = masterSeed
                        ?: throw FormatError.InvalidHeader("No master seed."),
                    encryptionIV = encryptionIV
                        ?: throw FormatError.InvalidHeader("No encryption IV."),
                    kdfParameters = kdfParameters
                        ?: throw FormatError.InvalidHeader("No kdf parameters found."),
                    publicCustomData = publicCustomData
                )
            }
        }

        private fun readHeaderValue(
            source: BufferedSource,
            version: FormatVersion
        ): Pair<Int, ByteString> {
            val id = source.readByte()
            val length = if (version.major >= 4) {
                source.readIntLe().toLong()
            } else {
                source.readShortLe().toLong()
            }
            val data = if (length > 0) {
                source.readByteString(length)
            } else {
                ByteString.EMPTY
            }
            return id.toInt() to data
        }
    }
}
