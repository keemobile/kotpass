package app.keemobile.kotpass.database.header

import app.keemobile.kotpass.constants.KdfConst
import app.keemobile.kotpass.io.decodeBase64ToArray
import app.keemobile.kotpass.io.decodeHexToArray
import app.keemobile.kotpass.resources.VariantDictionaryRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.ByteString.Companion.toByteString

class VariantDictionarySpec : DescribeSpec({

    describe("Variant dictionary") {
        it("Properly reads and writes data") {
            val dictionary = VariantDictionaryRes.KdfParams.decodeBase64ToArray()
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
