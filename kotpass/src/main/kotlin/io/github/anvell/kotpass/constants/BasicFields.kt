package io.github.anvell.kotpass.constants

enum class BasicFields(val key: String) {
    Title("Title"),
    UserName("UserName"),
    Password("Password"),
    Url("URL"),
    Notes("Notes");

    operator fun invoke() = this.key

    companion object {
        val keys = values()
            .map(BasicFields::key)
            .toSet()
    }
}
