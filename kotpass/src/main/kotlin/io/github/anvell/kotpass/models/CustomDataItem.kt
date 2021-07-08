package io.github.anvell.kotpass.models

import java.time.Instant

data class CustomDataItem(
    val value: String,
    val lastModified: Instant? = null
) {
    companion object
}
