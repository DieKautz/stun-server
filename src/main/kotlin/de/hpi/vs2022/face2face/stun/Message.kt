package de.hpi.vs2022.face2face.stun

import io.ktor.utils.io.core.*
import java.nio.ByteBuffer

data class Message(
    val type: Type,
    val transactionId: ByteArray, // 96 bits = 12 bytes
    var attributes: List<Attribute> = emptyList()
) {

    fun length(): Int = 4 + 4 + 12 + attributes.map { it.length() }.sum()
    fun putBytes(buffer: ByteBuffer) {
        val attributesLength = attributes.map { it.length() }.sum()

        buffer.putShort(type.value)
        buffer.putShort((4 + attributesLength).toShort())
        buffer.putInt(magicCookie)
        buffer.put(transactionId)
        attributes.forEach {
            it.putBytes(buffer)
        }
    }

    enum class Type(val value: Short) {
        BindingRequest(0x0001),
        BindingResponse(0x0101);
        companion object {
            private val map = Type.values().associateBy(Type::value)
            fun fromValue(type: Short) = map[type]
        }
    }

    companion object {
        private const val magicCookie: Int = 0x2112A442
        fun tryFromPacket(buffer: ByteReadPacket): Message {
            val type = Type.fromValue(buffer.readShort())
                ?: throw IllegalArgumentException("Given stream does not contain a valid stun message type!")
            val length = buffer.readShort()
            val cookie = buffer.readInt()
            assert(cookie == magicCookie)
            val message = Message(
                type,
                buffer.readBytes(12)
            )
            while (buffer.hasBytes(4)) {
                message.attributes += Attribute.tryFromPacket(buffer)
            }
            return message
        }
    }
}