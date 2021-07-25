package io.github.anvell.kotpass.database

import io.github.anvell.kotpass.database.header.DatabaseHeader
import io.github.anvell.kotpass.database.header.DatabaseInnerHeader
import io.github.anvell.kotpass.models.DatabaseContent

sealed class KeePassDatabase {
    abstract val credentials: Credentials
    abstract val header: DatabaseHeader
    abstract val content: DatabaseContent

    data class Ver3x(
        override val credentials: Credentials,
        override val header: DatabaseHeader.Ver3x,
        override val content: DatabaseContent
    ): KeePassDatabase()

    data class Ver4x(
        override val credentials: Credentials,
        override val header: DatabaseHeader.Ver4x,
        override val content: DatabaseContent,
        internal val innerHeader: DatabaseInnerHeader
    ): KeePassDatabase()

    companion object {
        const val MinSupportedVersion = 3
        const val MaxSupportedVersion = 4
    }
}
