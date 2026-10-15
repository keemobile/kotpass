package app.keemobile.kotpass.database

import app.keemobile.kotpass.cryptography.EncryptedValue
import app.keemobile.kotpass.cryptography.format.AesKdf
import app.keemobile.kotpass.errors.CryptoError
import app.keemobile.kotpass.errors.FormatError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec

class DecodeErrorsSpec : DescribeSpec({

    describe("Decoding errors") {
        it("Rejects a KDBX 3.x file opened with a wrong key") {
            shouldThrow<CryptoError.InvalidKey> {
                KeePassDatabase.decode(
                    inputStream = ClassLoader.getSystemResourceAsStream("ver3_aes.kdbx")!!,
                    credentials = Credentials.from(EncryptedValue.fromString("not the key"))
                )
            }
        }

        it("Reports a KDF seed of the wrong length as a header error, not as a wrong key") {
            shouldThrow<FormatError.InvalidHeader> {
                AesKdf.transformKey(
                    key = ByteArray(32),
                    seed = ByteArray(5),
                    rounds = 1UL
                )
            }
        }
    }
})
