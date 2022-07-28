package de.hpi.vs2022.face2face.stun

import de.hpi.vs2022.face2face.stun.attributes.MappedAddress
import io.ktor.utils.io.core.*
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

interface Attribute {
    val type: Short

    fun length(): Short
    fun putBytes(buffer: ByteBuffer)
    fun valueFromPacket(buffer: ByteReadPacket)

    private fun putHeaderBytes(buffer: ByteBuffer) {
        val length = length()
        buffer.putShort(type)
        buffer.putShort(length)
    }

    private val headerSize
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
