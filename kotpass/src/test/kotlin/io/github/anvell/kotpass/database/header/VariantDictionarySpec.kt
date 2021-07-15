package io.github.anvell.kotpass.database.header

import io.github.anvell.kotpass.constants.KdfConst
import io.github.anvell.kotpass.resources.VariantDictionaryRes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.ByteString.Companion.toByteString
import org.apache.commons.codec.binary.Base16
import org.apache.commons.codec.binary.Base64.decodeBase64

class VariantDictionarySpec : DescribeSpec({

    describe("Variant dictionary") {
        it("Properly reads and writes data") {
            val dictionary = decodeBase64(VariantDictionaryRes.KdfParams)
                .toByteString()
                .let(VariantDictionary::readFrom)
                .let(VariantDictionary::writeToByteString)
                .let(VariantDictionary::readFrom)
            val uuid = dictionary[KdfConst.Keys.Uuid]

            uuid.shouldBeInstanceOf<VariantItem.Bytes>()
            uuid.value.toByteArray() shouldBe Base16(true).decode(VariantDictionaryRes.Uuid)
        }
    }
})
