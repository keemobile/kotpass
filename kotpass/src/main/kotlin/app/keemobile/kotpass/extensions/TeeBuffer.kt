package app.keemobile.kotpass.extensions

import app.keemobile.kotpass.io.TeeBufferedSource
import okio.Buffer
import okio.BufferedSource
import okio.Source

internal fun Source.teeBuffer(mirrorBuffer: Buffer): BufferedSource {
    return TeeBufferedSource(this, mirrorBuffer)
}
