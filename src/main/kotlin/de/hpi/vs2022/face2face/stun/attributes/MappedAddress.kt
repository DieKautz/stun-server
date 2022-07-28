package de.hpi.vs2022.face2face.stun.attributes

import de.hpi.vs2022.face2face.stun.Attribute
import io.ktor.utils.io.core.*
import java.nio.ByteBuffer

data class MappedAddress(
    var family: IPFamily,
    var port: Short,
    var address: ByteArray
) : Attribute {
    constructor() : this(IPFamily.V4, 0, byteArrayOf())

    override var type: Short = 0x0001

    override fun length(): Int = headerSize + 4 + family.sizeInBytes

    override fun putBytes(buffer: ByteBuffer) {
        super.putBytes(buffer)
        buffer.apply {
            put(0)
            put(family.value)
            putShort(port)
            put(address)
        }
    }

    override fun valueFromPacket(buffer: ByteReadPacket) {
        super.valueFromPacket(buffer)
        if (!buffer.hasBytes(length() - headerSize)) {
            throw IllegalStateException("Not a valid Mapped-Address-Attribute!")
        }
        buffer.apply {
            readByte()
            family = IPFamily.fromValue(readByte())
                ?: throw IllegalStateException("Given attribute stream does not contain a valid ip family!")
            port = readShort()
            address = readBytes(family.sizeInBytes)
        }
    }

    enum class IPFamily(val value: Byte, val sizeInBytes: Int) {
        V4(0x01, 4),
        V6(0x02, 16);

        companion object {
            private val map = IPFamily.values().associateBy(IPFamily::value)
            fun fromValue(type: Byte) = map[type]
        }
    }
}