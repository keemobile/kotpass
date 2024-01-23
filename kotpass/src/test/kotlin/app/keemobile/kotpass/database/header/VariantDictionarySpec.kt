package app.keemobile.kotpass.database.header

import app.keemobile.kotpass.constants.KdfConst
import app.keemobile.kotpass.io.decodeHexToArray
import app.keemobile.kotpass.resources.VariantDictionaryRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.ByteString.Companion.toByteString
import java.io.InputStream

class VariantDictionarySpec : DescribeSpec({

    describe("Variant dictionary") {
        it("Properly reads and writes data") {
            val dictionary = ClassLoader
                .getSystemResourceAsStream("kdf_params")!!
                .use(InputStream::readAllBytes)
                .toByteString()
                .let(VariantDictionary::readFrom)
                .let(VariantDictionary::writeToByteString)
                .let(VariantDictionary::readFrom)
            val uuid = dictionary[KdfConst.Keys.Uuid]

            uuid.shouldBeInstanceOf<VariantItem.Bytes>()
            uuid.value.toByteArray() shouldBe VariantDictionaryRes.Uuid.decodeHexToArray()
        }
    }
})
