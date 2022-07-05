package de.hpi.vs2022.face2face.rfc5389.attributes

import io.ktor.utils.io.bits.*
import java.nio.ByteBuffer

data class STUNMappedAddress(val port: Int, val addressBytes: ByteArray) {

    fun bytes(): ByteArray {
        val length = 4 + 4 + addressBytes.size
        val byteBuffer = ByteBuffer.allocate(length)
        byteBuffer.putShort(0x0001)
        byteBuffer.putShort((length - 4).toShort())

        val family = if (addressBytes.size == 4) 1.toByte() else 2.toByte()

        byteBuffer.put(0)
        byteBuffer.put(family)
        byteBuffer.putShort(port.toShort())

        byteBuffer.put(addressBytes)

        return byteBuffer.array()
    }

}
