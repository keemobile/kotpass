/**
 * The Bouncy Castle License
 *
 * Copyright (c) 2000-2021 The Legion Of The Bouncy Castle Inc. (https://www.bouncycastle.org)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package io.github.anvell.kotpass.cryptography

internal fun littleEndianToInt(bs: ByteArray, offset: Int): Int {
    var off = offset
    var n: Int = bs[off].toInt() and 0xff
    n = n or (bs[++off].toInt() and 0xff shl 8)
    n = n or (bs[++off].toInt() and 0xff shl 16)
    n = n or (bs[++off].toInt() shl 24)
    return n
}

internal fun littleEndianToInt(bs: ByteArray, bOff: Int, ns: IntArray, nOff: Int, count: Int) {
    var byteArrayOffset = bOff
    for (i in 0 until count) {
        ns[nOff + i] = littleEndianToInt(bs, byteArrayOffset)
        byteArrayOffset += 4
    }
}

internal fun littleEndianToInt(bs: ByteArray, off: Int, count: Int): IntArray {
    var offset = off
    val ns = IntArray(count)
    for (i in ns.indices) {
        ns[i] = littleEndianToInt(bs, offset)
        offset += 4
    }
    return ns
}

internal fun intToLittleEndian(n: Int, bs: ByteArray, off: Int) {
    bs[off] = n.toByte()
    bs[off + 1] = (n ushr 8).toByte()
    bs[off + 2] = (n ushr 16).toByte()
    bs[off + 3] = (n ushr 24).toByte()
}

internal fun intToLittleEndian(ns: IntArray, bs: ByteArray, off: Int) {
    var offset = off
    for (i in ns.indices) {
        intToLittleEndian(ns[i], bs, offset)
        offset += 4
    }
}
