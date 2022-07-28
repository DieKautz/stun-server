package de.hpi.vs2022.face2face.stun.attributes

import de.hpi.vs2022.face2face.stun.Attribute
import de.hpi.vs2022.face2face.stun.Message
import de.hpi.vs2022.face2face.stun.attributes.MappedAddress.IPFamily
import de.hpi.vs2022.face2face.toByteArray
import de.hpi.vs2022.face2face.xor
import io.ktor.utils.io.core.*
import java.nio.ByteBuffer
import kotlin.experimental.xor

data class XorMappedAddress(
    var family: IPFamily,
    var port: Short,
    var address: ByteArray
) : Attribute {
    constructor() : this(IPFamily.V4, 0, byteArrayOf())

    private val magicCookie: Int = 0x2112A442

    override var type: Short = 0x0020

    override fun length(): Int = headerSize + 4 + family.sizeInBytes

    override fun putBytes(buffer: ByteBuffer, message: Message) {
        super.putBytes(buffer, message)
        val xorPort = port xor ((magicCookie shr 16).toShort())
        val xorAddress = when (family) {
            IPFamily.V4 -> address.xor(magicCookie.toByteArray())
            IPFamily.V6 -> address.xor(magicCookie.toByteArray() + message.transactionId)
        }
        buffer.apply {
            put(0)
            put(family.value)
            putShort(xorPort)
            put(xorAddress)
        }
    }

    override fun valueFromPacket(buffer: ByteReadPacket, message: Message) {
        super.valueFromPacket(buffer, message)
        if (!buffer.hasBytes(length() - headerSize)) {
            throw IllegalStateException("Not a valid Mapped-Address-Attribute!")
        }
        buffer.apply {
            readByte()
            family = IPFamily.fromValue(readByte())
                ?: throw IllegalStateException("Given attribute stream does not contain a valid ip family!")
            port = readShort() xor ((magicCookie shr 16).toShort())
            address = when (family) {
                IPFamily.V4 -> readBytes(family.sizeInBytes).xor(magicCookie.toByteArray())
                IPFamily.V6 -> readBytes(family.sizeInBytes).xor(magicCookie.toByteArray() + message.transactionId)
            }
        }
    }
}