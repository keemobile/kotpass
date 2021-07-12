package io.github.anvell.kotpass.models

import java.time.Instant
import java.util.*

data class DeletedObject(
    val id: UUID,
    val deletionTime: Instant
)
