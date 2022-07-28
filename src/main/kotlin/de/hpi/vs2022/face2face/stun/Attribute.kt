package de.hpi.vs2022.face2face.stun

import io.ktor.utils.io.bits.*

interface Attribute {
    val type: Short

    fun length(): Short
    fun getBytes(): ByteArray

    private val headerSize
        get() = 4
    private fun headerBytes(): ByteArray {
        val length = length()
        return byteArrayOf(
            type.highByte,
            type.lowByte,
            length.highByte,
            length.lowByte
        )
    }
}
