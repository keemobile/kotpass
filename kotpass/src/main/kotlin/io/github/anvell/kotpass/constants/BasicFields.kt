package io.github.anvell.kotpass.constants

enum class BasicFields(val value: String) {
    Title("Title"),
    UserName("UserName"),
    Password("Password"),
    Url("URL"),
    Notes("Notes");

    operator fun invoke() = this.value
}
