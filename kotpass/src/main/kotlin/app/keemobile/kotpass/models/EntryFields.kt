package app.keemobile.kotpass.models

import app.keemobile.kotpass.constants.BasicField
import app.keemobile.kotpass.cryptography.EncryptedValue

class EntryFields(
    private val fields: Map<String, EntryValue>
) : Map<String, EntryValue> by fields {
    val title
        get() = fields[BasicField.Title()]
    val userName
        get() = fields[BasicField.UserName()]
    val password
        get() = fields[BasicField.Password()]
    val url
        get() = fields[BasicField.Url()]
    val notes
        get() = fields[BasicField.Notes()]

    operator fun get(key: BasicField): EntryValue? = fields[key()]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val iterator = fields.iterator()
        val otherIterator = (other as EntryFields).iterator()

        while (true) {
            val diff = iterator.hasNext() xor otherIterator.hasNext()
            if (diff) return false

            if (iterator.hasNext()) {
                if (iterator.next() != otherIterator.next()) {
                    return false
                }
            } else {
                break
            }
        }
        return true
    }

    override fun hashCode(): Int = fields.hashCode()

    companion object {
        fun of(vararg pairs: Pair<String, EntryValue>) = EntryFields(mapOf(*pairs))

        fun createDefault() = EntryFields(
            buildMap {
                BasicField
                    .values()
                    .filter { it != BasicField.Password }
                    .forEach { field -> put(field(), EntryValue.Plain("")) }

                val password = EncryptedValue.fromString("")
                put(BasicField.Password(), EntryValue.Encrypted(password))
            }
        )
    }
}
