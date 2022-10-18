package app.keemobile.kotpass.extensions

import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.security.SecureRandom

internal fun SecureRandom.nextByteString(length: Int): ByteString {
    return ByteArray(length)
        .apply { nextBytes(this) }
        .toByteString()
}
