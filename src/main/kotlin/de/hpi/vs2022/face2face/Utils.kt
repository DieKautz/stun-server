package de.hpi.vs2022.face2face

import io.ktor.utils.io.bits.*
import kotlin.experimental.xor

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

fun ByteArray.toUInt(): UInt {
    var result = 0u
    for (i in this.indices) {
        result = result or (this[i].toUInt() shl 8 * i)
    }
    return result
}

fun ByteArray.xor(other: ByteArray): ByteArray = mapIndexed { index: Int, byte: Byte ->
    return@mapIndexed byte xor other[index]
}.toByteArray()

fun Short.toByteArray() = byteArrayOf(highByte, lowByte)

fun Int.toByteArray() = byteArrayOf(
    (this shr 24).toByte(),
    (this shr 16).toByte(),
    (this shr 8).toByte(),
    (this shr 0).toByte(),
)