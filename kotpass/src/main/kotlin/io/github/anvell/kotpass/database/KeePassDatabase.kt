package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.database.header.DatabaseHeader
import io.github.anvell.kotpass.database.header.DatabaseInnerHeader
import io.github.anvell.kotpass.models.DatabaseContent

data class KeePassDatabase(
    val credentials: Credentials,
    val header: DatabaseHeader,
    val innerHeader: DatabaseInnerHeader?,
    val content: DatabaseContent
) {
    companion object {
        const val MinSupportedVersion = 3
        const val MaxSupportedVersion = 4
    }
}
