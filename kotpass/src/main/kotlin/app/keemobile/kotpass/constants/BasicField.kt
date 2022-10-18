package app.keemobile.kotpass.constants

enum class BasicField(val key: String) {
    Title("Title"),
    UserName("UserName"),
    Password("Password"),
    Url("URL"),
    Notes("Notes");

    operator fun invoke() = this.key

    companion object {
        val keys = values()
            .map(BasicField::key)
            .toSet()
    }
}
