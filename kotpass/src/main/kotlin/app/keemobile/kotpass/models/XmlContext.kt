package app.keemobile.kotpass.models

import app.keemobile.kotpass.constants.BasicField
import app.keemobile.kotpass.constants.Defaults
import app.keemobile.kotpass.constants.MemoryProtectionFlag
import app.keemobile.kotpass.cryptography.EncryptionSaltGenerator
import okio.ByteString

sealed class XmlContext {
    abstract val version: FormatVersion

    sealed class Encode : XmlContext() {
        abstract val binaries: Map<ByteString, BinaryData>

        class Encrypted(
            override val version: FormatVersion,
            override val binaries: Map<ByteString, BinaryData>,
            val innerEncryption: EncryptionSaltGenerator
        ) : Encode()

        class Plain(
            override val version: FormatVersion,
            override val binaries: Map<ByteString, BinaryData>,
            val memoryProtectionFlags: Set<MemoryProtectionFlag>
        ) : Encode() {
            val memoryProtectionKeys = memoryProtectionFlags
                .map(MemoryProtectionFlag::toBasicField)
                .map(BasicField::key)
                .toSet()
        }
    }

    class Decode(
        override val version: FormatVersion,
        val encryption: EncryptionSaltGenerator,
        val binaries: Map<ByteString, BinaryData>,
        val untitledLabel: String = Defaults.UntitledLabel
    ) : XmlContext()
}
