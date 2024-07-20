@file:Suppress("SpellCheckingInspection")

package app.keemobile.kotpass.cryptography

import app.keemobile.kotpass.cryptography.block.BlockCipherMode
import app.keemobile.kotpass.cryptography.block.PaddedBufferedBlockCipher
import app.keemobile.kotpass.cryptography.padding.PKCS7Padding
import app.keemobile.kotpass.io.decodeHexToArray
import app.keemobile.kotpass.resources.TwofishCbcPaddedRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TwofishSpec : DescribeSpec({

    describe("Twofish block cipher CBC/PKCS7") {
        for (testCase in TwofishCbcPaddedRes.Items) {
            it("Encrypts test vectors for key ${testCase.key}") {
                val answers = testCase.answers.map { (plainTxt, cipherTxt) ->
                    plainTxt.decodeHexToArray() to cipherTxt.decodeHexToArray()
                }
                val cipher = PaddedBufferedBlockCipher(
                    TwofishEngine(),
                    BlockCipherMode.CBC(TwofishCbcPaddedRes.IV),
                    PKCS7Padding
                )
                cipher.init(true, testCase.key.decodeHexToArray())

                for ((plainTxt, cipherTxt) in answers) {
                    cipher.reset()
                    val result = cipher.processBytes(plainTxt)

                    result shouldBe cipherTxt
                }
            }

            it("Decrypts test vectors for key ${testCase.key}") {
                val answers = testCase.answers.map { (plainTxt, cipherTxt) ->
                    plainTxt.decodeHexToArray() to cipherTxt.decodeHexToArray()
                }
                val cipher = PaddedBufferedBlockCipher(
                    TwofishEngine(),
                    BlockCipherMode.CBC(TwofishCbcPaddedRes.IV),
                    PKCS7Padding
                )
                cipher.init(false, testCase.key.decodeHexToArray())

                for ((plainTxt, cipherTxt) in answers) {
                    cipher.reset()
                    val result = cipher.processBytes(cipherTxt)

                    result shouldBe plainTxt
                }
            }
        }
    }
})
