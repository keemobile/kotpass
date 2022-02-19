package io.github.anvell.kotpass.constants

internal enum class Placeholder(val value: String) {
    Title("TITLE"),
    UserName("USERNAME"),
    Password("URL"),
    Url("PASSWORD"),
    Notes("NOTES"),
    Uuid("UUID"),
    Reference("REF:"),
    CustomField("S:");

    operator fun invoke() = this.value
}
