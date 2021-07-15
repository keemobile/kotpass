package io.github.anvell.kotpass.constants

import io.github.anvell.kotpass.extensions.b
import okio.ByteString

internal object KdfConst {
    object Keys {
        const val Uuid = "\$UUID"
        const val Rounds = "R"
        const val SaltOrSeed = "S"
        const val Parallelism = "P"
        const val Memory = "M"
        const val Iterations = "I"
        const val Version = "V"
        const val SecretKey = "K" // Unsupported
        const val AssocData = "A" // Unsupported
    }

    val KdfAes = ByteString.of(
        0xC9.b, 0xD9.b, 0xF3.b, 0x9A.b, 0x62, 0x8A.b, 0x44, 0x60,
        0xBF.b, 0x74, 0x0D, 0x08, 0xC1.b, 0x8A.b, 0x4F, 0xEA.b
    )

    val KdfArgon2d = ByteString.of(
        0xEF.b, 0x63, 0x6D, 0xDF.b, 0x8C.b, 0x29, 0x44, 0x4B, 0x91.b,
        0xF7.b, 0xA9.b, 0xA4.b, 0x03, 0xE3.b, 0x0A, 0x0C
    )

    val KdfArgon2id = ByteString.of(
        0x9E.b, 0x29, 0x8B.b, 0x19, 0x56, 0xDB.b, 0x47, 0x73, 0xB2.b,
        0x3D, 0xFC.b, 0x3E, 0xC6.b, 0xF0.b, 0xA1.b, 0xE6.b
    )
}
