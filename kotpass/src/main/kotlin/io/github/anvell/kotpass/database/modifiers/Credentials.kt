package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.database.Credentials
import io.github.anvell.kotpass.database.KeePassDatabase

/**
 * Modifies [Credentials] field in [KeePassDatabase] with result of [block] lambda.
 */
inline fun KeePassDatabase.modifyCredentials(
    crossinline block: Credentials.() -> Credentials
) = when (this) {
    is KeePassDatabase.Ver3x -> copy(
        credentials = block(credentials)
    )
    is KeePassDatabase.Ver4x -> copy(
        credentials = block(credentials)
    )
}
