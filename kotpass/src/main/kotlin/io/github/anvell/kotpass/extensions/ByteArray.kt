package io.github.anvell.kotpass.extensions

import java.security.MessageDigest

private const val Sha256 = "SHA-256"
private const val Sha512 = "SHA-512"

fun ByteArray.sha256(): ByteArray = MessageDigest
    .getInstance(Sha256)
    .digest(this)

fun ByteArray.sha512(): ByteArray = MessageDigest
    .getInstance(Sha512)
    .digest(this)
