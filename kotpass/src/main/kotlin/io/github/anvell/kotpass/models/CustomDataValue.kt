package io.github.anvell.kotpass.models

import java.time.Instant

data class CustomDataValue(
    val value: String,
    val lastModified: Instant? = null
)
