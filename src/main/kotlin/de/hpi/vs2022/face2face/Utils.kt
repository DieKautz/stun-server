package de.hpi.vs2022.face2face

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

fun ByteArray.toUInt(): UInt {
    var result = 0u
    for (i in this.indices) {
        result = result or (this[i].toUInt() shl 8 * i)
    }
    return result
}