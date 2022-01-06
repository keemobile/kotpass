package io.github.anvell.kotpass.extensions

import java.util.*

@PublishedApi
internal fun UUID.isZero() = leastSignificantBits == 0L && mostSignificantBits == 0L

@PublishedApi
internal fun UUID?.isNullOrZero() = this?.isZero() ?: true
