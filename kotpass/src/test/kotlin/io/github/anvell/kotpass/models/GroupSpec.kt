package io.github.anvell.kotpass.models

import io.github.anvell.kotpass.cryptography.EncryptionSaltGenerator
import io.github.anvell.kotpass.extensions.parseAsXml
import io.github.anvell.kotpass.resources.GroupRes
import io.github.anvell.kotpass.xml.unmarshalGroup
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.*

class GroupSpec : DescribeSpec({

    describe("Group") {
        it("Properly deserialized from Xml") {
            val context = FormatContext(
                version = FormatVersion(4, 1),
                encryption = EncryptionSaltGenerator.ChaCha20(byteArrayOf())
            )
            val group = unmarshalGroup(context, GroupRes.BasicXml.parseAsXml())

            group.name shouldBe "Lorem"
            group.iconId shouldBe Group.DefaultIconId
            group.enableAutoType shouldBe null
            group.enableSearching shouldBe null
            group.lastTopVisibleEntry shouldNotBe null
            group.groups.size shouldBe 1
            group.groups.first().name shouldBe "Ipsum"
            group.entries.size shouldBe 1
        }
    }
})
