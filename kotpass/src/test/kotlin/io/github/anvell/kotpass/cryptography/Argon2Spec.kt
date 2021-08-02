package io.github.anvell.kotpass.cryptography

import io.github.anvell.kotpass.io.decodeHexToArray
import io.github.anvell.kotpass.resources.Argon2Res
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class Argon2Spec : DescribeSpec({

    describe("Argon2") {
        it("Type D") {
            val expected = "512b391b6f1162975371d30919734294f868e3be3984f3c1a13a4db9fabe4acb"
            val result = ByteArray(32)

            Argon2Engine(
                type = Argon2Engine.Type.Argon2D,
                version = Argon2Engine.Version.Ver13,
                salt = Argon2Res.TestSalt,
                secret = Argon2Res.TestSecret,
                additional = Argon2Res.TestAdditional,
                iterations = 3,
                parallelism = 4,
                memory = 32
            ).generateBytes(Argon2Res.TestPassword, result)

            result shouldBe expected.decodeHexToArray()
        }

        it("Type I") {
            val expected = "c814d9d1dc7f37aa13f0d77f2494bda1c8de6b016dd388d29952a4c4672b6ce8"
            val result = ByteArray(32)

            Argon2Engine(
                type = Argon2Engine.Type.Argon2I,
                version = Argon2Engine.Version.Ver13,
                salt = Argon2Res.TestSalt,
                secret = Argon2Res.TestSecret,
                additional = Argon2Res.TestAdditional,
                iterations = 3,
                parallelism = 4,
                memory = 32
            ).generateBytes(Argon2Res.TestPassword, result)

            result shouldBe expected.decodeHexToArray()
        }

        it("Type ID") {
            val expected = "0d640df58d78766c08c037a34a8b53c9d01ef0452d75b65eb52520e96b01e659"
            val result = ByteArray(32)

            Argon2Engine(
                type = Argon2Engine.Type.Argon2Id,
                version = Argon2Engine.Version.Ver13,
                salt = Argon2Res.TestSalt,
                secret = Argon2Res.TestSecret,
                additional = Argon2Res.TestAdditional,
                iterations = 3,
                parallelism = 4,
                memory = 32
            ).generateBytes(Argon2Res.TestPassword, result)

            result shouldBe expected.decodeHexToArray()
        }
    }
})
