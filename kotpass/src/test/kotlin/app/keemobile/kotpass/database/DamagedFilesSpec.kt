package app.keemobile.kotpass.database

import app.keemobile.kotpass.cryptography.EncryptedValue
import app.keemobile.kotpass.errors.CryptoError
import app.keemobile.kotpass.errors.FormatError
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayInputStream
import java.io.IOException

private val Files = listOf("ver3_aes.kdbx", "ver4_aes.kdbx", "ver4_argon2.kdbx", "ver4_with_binaries.kdbx")

private fun credentials() = Credentials.from(EncryptedValue.fromString("1"))

private fun readResource(name: String) = ClassLoader.getSystemResourceAsStream(name)!!.use { it.readBytes() }

/**
 * Whether decoding [bytes] ends in a database or in an error the caller can expect: one of the library's own
 * ([FormatError], [CryptoError]) or an [IOException] for a stream that ends early. Any other exception, a hang or an
 * out of memory error is a failure.
 */
private fun decodesOrFailsAsExpected(bytes: ByteArray): Boolean =
    try {
        KeePassDatabase.decode(ByteArrayInputStream(bytes), credentials())
        true
    } catch (_: FormatError) {
        true
    } catch (_: CryptoError) {
        true
    } catch (_: IOException) {
        true
    }

class DamagedFilesSpec : DescribeSpec({

    describe("Damaged files") {
        it("A truncated file fails with an expected error at every length") {
            for (name in Files) {
                val original = readResource(name)
                // Every length through the header, then steps across the rest of the file.
                val lengths = (0..minOf(original.size, 512)) + (512 until original.size step 97)
                for (length in lengths) {
                    decodesOrFailsAsExpected(original.copyOf(length)) shouldBe true
                }
            }
        }

        it("A file with one damaged byte fails with an expected error") {
            for (name in Files) {
                val original = readResource(name)
                for (index in original.indices step 31) {
                    val damaged = original.copyOf().also { it[index] = (it[index].toInt() xor 0xFF).toByte() }
                    decodesOrFailsAsExpected(damaged) shouldBe true
                }
            }
        }
    }
})
