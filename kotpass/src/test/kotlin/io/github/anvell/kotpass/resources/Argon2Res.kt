package io.github.anvell.kotpass.resources

import io.github.anvell.kotpass.cryptography.Argon2Engine
import io.github.anvell.kotpass.io.decodeHexToArray

internal object Argon2Res {
    val TestSalt: ByteArray = "02020202020202020202020202020202".decodeHexToArray()
    val TestSecret: ByteArray = "0303030303030303".decodeHexToArray()
    val TestAdditional: ByteArray = "040404040404040404040404".decodeHexToArray()
    val TestPassword: ByteArray =
        "0101010101010101010101010101010101010101010101010101010101010101".decodeHexToArray()

    val TestCases = listOf(
        Argon2TestCase(
            version = Argon2Engine.Version.Ver10,
            iterations = 2,
            memory = 16,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "f6c4db4a54e2a370627aff3db6176b94a2a209a62c8e36152711802f7b30c694",
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver10,
            iterations = 2,
            memory = 20,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "9690ec55d28d3ed32562f2e73ea62b02b018757643a2ae6e79528459de8106e9",
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver10,
            iterations = 2,
            memory = 18,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "3e689aaa3d28a77cf2bc72a51ac53166761751182f1ee292e3f677a7da4c2467",
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver10,
            iterations = 2,
            memory = 8,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "fd4dd83d762c49bdeaf57c47bdcd0c2f1babf863fdeb490df63ede9975fccf06",
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver10,
            iterations = 2,
            memory = 8,
            parallelism = 2,
            password = "password",
            salt = "somesalt",
            output = "b6c11560a6a9d61eac706b79a2f97d68b4463aa3ad87e00c07e2b01e90c564fb"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver10,
            iterations = 1,
            memory = 16,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "81630552b8f3b1f48cdb1992c4c678643d490b2b5eb4ff6c4b3438b5621724b2"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver10,
            iterations = 4,
            memory = 16,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "f212f01615e6eb5d74734dc3ef40ade2d51d052468d8c69440a3a1f2c1c2847b"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver10,
            iterations = 2,
            memory = 16,
            parallelism = 1,
            password = "differentpassword",
            salt = "somesalt",
            output = "e9c902074b6754531a3a0be519e5baf404b30ce69b3f01ac3bf21229960109a3"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver10,
            iterations = 2,
            memory = 16,
            parallelism = 1,
            password = "password",
            salt = "diffsalt",
            output = "79a103b90fe8aef8570cb31fc8b22259778916f8336b7bdac3892569d4f1c497"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver13,
            iterations = 2,
            memory = 16,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "c1628832147d9720c5bd1cfd61367078729f6dfb6f8fea9ff98158e0d7816ed0"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver13,
            iterations = 2,
            memory = 20,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "d1587aca0922c3b5d6a83edab31bee3c4ebaef342ed6127a55d19b2351ad1f41"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver13,
            iterations = 2,
            memory = 18,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "296dbae80b807cdceaad44ae741b506f14db0959267b183b118f9b24229bc7cb"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver13,
            iterations = 2,
            memory = 8,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "89e9029f4637b295beb027056a7336c414fadd43f6b208645281cb214a56452f"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver13,
            iterations = 2,
            memory = 8,
            parallelism = 2,
            password = "password",
            salt = "somesalt",
            output = "4ff5ce2769a1d7f4c8a491df09d41a9fbe90e5eb02155a13e4c01e20cd4eab61"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver13,
            iterations = 1,
            memory = 16,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "d168075c4d985e13ebeae560cf8b94c3b5d8a16c51916b6f4ac2da3ac11bbecf"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver13,
            iterations = 4,
            memory = 16,
            parallelism = 1,
            password = "password",
            salt = "somesalt",
            output = "aaa953d58af3706ce3df1aefd4a64a84e31d7f54175231f1285259f88174ce5b"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver13,
            iterations = 2,
            memory = 16,
            parallelism = 1,
            password = "differentpassword",
            salt = "somesalt",
            output = "14ae8da01afea8700c2358dcef7c5358d9021282bd88663a4562f59fb74d22ee"
        ),
        Argon2TestCase(
            version = Argon2Engine.Version.Ver13,
            iterations = 2,
            memory = 16,
            parallelism = 1,
            password = "password",
            salt = "diffsalt",
            output = "b0357cccfbef91f3860b0dba447b2348cbefecadaf990abfe9cc40726c521271"
        )
    )
}
