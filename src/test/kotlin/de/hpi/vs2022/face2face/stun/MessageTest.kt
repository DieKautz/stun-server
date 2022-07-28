package de.hpi.vs2022.face2face.stun

import io.ktor.utils.io.core.*
import org.junit.Test
import java.nio.ByteBuffer
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class MessageTest {

    @Test
    fun `first two bits are always zero`() {
        val message = Message(Message.Type.BindingRequest, Random.nextBytes(12))

        val buffer = ByteBuffer.allocate(message.length())
        message.putBytes(buffer)
        buffer.rewind()
        val first = buffer.get()
        assert(first.countLeadingZeroBits() >= 2)
    }

    @Test
    fun `indication gets read correctly`() {
        val testBuff = ByteBuffer.allocate(20)

        val type = Message.Type.BindingResponse
        val length = 20
        val magicCookie = 0x2112A442
        val transactionId = Random.nextBytes(12)

        testBuff.putShort(type.value)
        testBuff.putShort(length.toShort())
        testBuff.putInt(magicCookie)
        testBuff.put(transactionId)

        val msg = Message.tryFromPacket(ByteReadPacket(testBuff.array()))

        assertEquals(type, msg.type)
        assertEquals(length, msg.length())
        assertContentEquals(transactionId, msg.transactionId)
    }
}