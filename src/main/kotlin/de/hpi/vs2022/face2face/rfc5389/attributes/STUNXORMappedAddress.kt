package de.hpi.vs2022.face2face.rfc5389.attributes

import de.hpi.vs2022.face2face.LOG
import de.hpi.vs2022.face2face.toByteArray
import de.hpi.vs2022.face2face.toHex
import de.hpi.vs2022.face2face.xor
import io.ktor.utils.io.bits.*
import java.nio.ByteBuffer
import kotlin.experimental.xor

data class STUNXORMappedAddress(val txId: ByteArray, val port: Int, val addressBytes: ByteArray) {

    private val cookie: Int = 0x2112A442

    fun bytes(): ByteArray {
        val length = 4 + 4 + addressBytes.size
        val byteBuffer = ByteBuffer.allocate(length)
        byteBuffer.putShort(0x0020)
        byteBuffer.putShort((length - 4).toShort())

        var family: Byte = 0x1
        var xoredBytes: ByteArray
        if (addressBytes.size == 4) {
            xoredBytes = addressBytes.xor(cookie.toByteArray())
        } else {
            family = 0x2
            xoredBytes = addressBytes.xor(cookie.toByteArray() + txId)
        }

        byteBuffer.put(0)
        byteBuffer.put(family)
        byteBuffer.putShort(port.toShort() xor ((cookie shr 16).toShort()))

        byteBuffer.put(xoredBytes)

        return byteBuffer.array()
    }

}
