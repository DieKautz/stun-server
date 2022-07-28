package de.hpi.vs2022.face2face.stun

import de.hpi.vs2022.face2face.stun.attributes.MappedAddress
import io.ktor.utils.io.core.*
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

interface Attribute {
    var type: Short

    fun length(): Int
    fun putBytes(buffer: ByteBuffer) {
        putHeaderBytes(buffer)
    }

    fun valueFromPacket(buffer: ByteReadPacket) {
        buffer.apply {
            type = readShort()
            readShort() // discard length
        }
    }

    private fun putHeaderBytes(buffer: ByteBuffer) {
        val length = length().toShort()
        buffer.putShort(type)
        buffer.putShort(length)
    }

    val headerSize
        get() = 4

    companion object {
        private val supportedAttributes = listOf<KClass<out Attribute>>(MappedAddress::class)
        fun tryFromPacket(buffer: ByteReadPacket): Attribute {
            var attribute: Attribute? = null
            supportedAttributes.forEach {
                attribute = it.createInstance()
                val tempBuff = buffer.copy()
                try {
                    attribute!!.valueFromPacket(tempBuff)
                    return@forEach
                } catch (ex: IllegalStateException) {
                    attribute = null
                    tempBuff.close()
                } finally {
                    tempBuff.close()
                }
            }
            return attribute ?: throw IllegalStateException("Unsupported attribute type found!")

        }
    }

}
