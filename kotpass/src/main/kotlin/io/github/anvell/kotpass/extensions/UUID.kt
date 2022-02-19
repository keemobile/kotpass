package io.github.anvell.kotpass.extensions

import io.github.anvell.kotpass.io.encodeHex
import io.github.anvell.kotpass.models.Entry
import java.nio.ByteBuffer
import java.util.*

/**
 * Converts [UUID] to hex string representation which
 * is used to reference fields in another [Entry].
 */
fun UUID.toHexString(): String {
    val buffer = ByteBuffer.allocate(16).apply {
        putLong(mostSignificantBits)
        putLong(leastSignificantBits)
    }
    return buffer.array().encodeHex()
}

@PublishedApi
internal fun UUID.isZero() = leastSignificantBits == 0L && mostSignificantBits == 0L

@PublishedApi
internal fun UUID?.isNullOrZero() = this?.isZero() ?: true
