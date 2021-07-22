package io.github.anvell.kotpass.resources

import io.github.anvell.kotpass.io.decodeHexToArray

internal class Blake2bDigestTestCase(
    input: String,
    output: String,
    key: String?,
    salt: String?,
    personalization: String?,
    val outputLength: Int
) {
    val input = input.decodeHexToArray()
    val output = output.decodeHexToArray()
    val key = key?.decodeHexToArray()
    val salt = salt?.decodeHexToArray()
    val personalization = personalization?.decodeHexToArray()
}
