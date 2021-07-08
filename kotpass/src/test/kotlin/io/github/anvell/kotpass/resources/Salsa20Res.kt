package io.github.anvell.kotpass.resources

internal object Salsa20Res {
    val SalsaTestCases = listOf(
        StreamCipherEncryptionTestCase(
            rounds = 12,
            key = "80000000000000000000000000000000",
            iv = "0000000000000000",
            plaintext = "0000000000000000",
            cipher = "FC207DBFC76C5E17"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 12,
            key = "00400000000000000000000000000000",
            iv = "0000000000000000",
            plaintext = "0000000000000000",
            cipher = "6C11A3F95FEC7F48"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 12,
            key = "09090909090909090909090909090909",
            iv = "0000000000000000",
            plaintext = "0000000000000000",
            cipher = "78E11FC333DEDE88"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 12,
            key = "1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B1B",
            iv = "0000000000000000",
            plaintext = "0000000000000000",
            cipher = "A67474611DF551FF"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 12,
            key = "8000000000000000000000000000000000000000000000000000000000000000",
            iv = "0000000000000000",
            plaintext = "0000000000000000",
            cipher = "AFE411ED1C4E07E4"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 12,
            key = "0053A6F94C9FF24598EB3E91E4378ADD3083D6297CCF2275C81B6EC11467BA0D",
            iv = "0D74DB42A91077DE",
            plaintext = "0000000000000000",
            cipher = "52E20CF8775AE882"
        ),

        StreamCipherEncryptionTestCase(
            rounds = 20,
            key = "80000000000000000000000000000000",
            iv = "0000000000000000",
            plaintext = "0000000000000000",
            cipher = "4DFA5E481DA23EA0"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 20,
            key = "00000000000000000000000000000000",
            iv = "8000000000000000",
            plaintext = "0000000000000000",
            cipher = "B66C1E4446DD9557"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 20,
            key = "0053A6F94C9FF24598EB3E91E4378ADD",
            iv = "0D74DB42A91077DE",
            plaintext = "0000000000000000",
            cipher = "05E1E7BEB697D999"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 20,
            key = "8000000000000000000000000000000000000000000000000000000000000000",
            iv = "0000000000000000",
            plaintext = "0000000000000000",
            cipher = "E3BE8FDD8BECA2E3"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 20,
            key = "0000000000000000000000000000000000000000000000000000000000000000",
            iv = "8000000000000000",
            plaintext = "0000000000000000",
            cipher = "2ABA3DC45B494700"
        ),
        StreamCipherEncryptionTestCase(
            rounds = 20,
            key = "0053A6F94C9FF24598EB3E91E4378ADD3083D6297CCF2275C81B6EC11467BA0D",
            iv = "0D74DB42A91077DE",
            plaintext = "0000000000000000",
            cipher = "F5FAD53F79F9DF58"
        )
    )
}
