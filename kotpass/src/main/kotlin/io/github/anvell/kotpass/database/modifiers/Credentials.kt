package io.github.anvell.kotpass.database.modifiers

import io.github.anvell.kotpass.database.Credentials
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.models.Meta
import java.time.Instant

/**
 * Modifies [Credentials] field in [KeePassDatabase] with result of [block] lambda.
 * If new [Credentials.passphrase] supplied modifies [Meta.masterKeyChanged] field.
 */
inline fun KeePassDatabase.modifyCredentials(
    crossinline block: Credentials.() -> Credentials
): KeePassDatabase {
    val newCredentials = block(credentials)
    val isModified = !credentials.passphrase?.getHash()
        .contentEquals(newCredentials.passphrase?.getHash())
    val newDatabase = when (this) {
        is KeePassDatabase.Ver3x -> copy(credentials = newCredentials)
        is KeePassDatabase.Ver4x -> copy(credentials = newCredentials)
    }

    return if (isModified) {
        newDatabase.modifyMeta {
            copy(masterKeyChanged = Instant.now())
        }
    } else {
        newDatabase
    }
}
