package app.keemobile.kotpass.resources

import app.keemobile.kotpass.cryptography.Argon2Engine

internal class Argon2TestCase(
    val version: Argon2Engine.Version,
    val iterations: Int,
    val memory: Int,
    val parallelism: Int,
    val password: String,
    val salt: String,
    val output: String
)
