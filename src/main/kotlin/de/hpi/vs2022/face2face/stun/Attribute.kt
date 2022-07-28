package de.hpi.vs2022.face2face.stun

import de.hpi.vs2022.face2face.stun.attributes.MappedAddress
import de.hpi.vs2022.face2face.stun.attributes.XorMappedAddress
import io.ktor.utils.io.core.*
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

interface Attribute {
    val type: Short

    fun length(): Int
    fun putBytes(buffer: ByteBuffer, message: Message) {
        putHeaderBytes(buffer)
    }

    fun valueFromPacket(buffer: ByteReadPacket, message: Message) {
        val inType = buffer.readShort()
        buffer.readShort() // discard length
        if (inType != type) {
            throw IllegalStateException("This attribute is not of type ${this.javaClass.simpleName}!")
        }
    }

    private fun putHeaderBytes(buffer: ByteBuffer) {
        val length = length().toShort()
        buffer.putShort(type)
        buffer.putShort((length - headerSize).toShort())
    }

    val headerSize
        get() = 4

    companion object {
        private val supportedAttributes = listOf<KClass<out Attribute>>(
            MappedAddress::class,
            XorMappedAddress::class
        )

        fun tryFromPacket(buffer: ByteReadPacket, message: Message): Attribute {
            var attribute: Attribute? = null
            supportedAttributes.forEach {
                attribute = it.createInstance()
                val tempBuff = buffer.copy()
                try {
                    attribute!!.valueFromPacket(tempBuff, message)
                    return@forEach
                } catch (ex: IllegalStateException) {
                    attribute = null
                    tempBuff.close()
                } finally {
                    tempBuff.close()
                }
            }
            return attribute ?: throw IllegalStateException("This attribute type is not supported!")

        }
    }

}
