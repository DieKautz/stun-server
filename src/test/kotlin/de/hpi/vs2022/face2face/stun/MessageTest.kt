package de.hpi.vs2022.face2face.stun

import io.ktor.utils.io.core.*
import org.jetbrains.annotations.TestOnly
import org.junit.Test
import java.nio.ByteBuffer
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class MessageTest {

    @TestOnly
    internal fun createRandomMessageNoAttr() = Message(
        Message.Type.values().random(),
        Random.Default.nextBytes(12)
    )

    @TestOnly
    private fun createMockMessageBytes(
        type: Message.Type = Message.Type.BindingResponse,
        length: Int = 20,
        cookie: Int = 0x2112A442,
        transactionId: ByteArray
    ): ByteArray {
        val testBuff = ByteBuffer.allocate(20)

        testBuff.apply {
            putShort(type.value)
            putShort(length.toShort())
            putInt(cookie)
            put(transactionId)
        }
        return testBuff.array()
    }

    @Test
    fun `first two bits are always zero`() {
        val message = createRandomMessageNoAttr()

        val buffer = ByteBuffer.allocate(message.length())
        message.putBytes(buffer)
        buffer.rewind()
        val first = buffer.get()
        assert(first.countLeadingZeroBits() >= 2)
    }

    @Test
    fun `indication gets read correctly`() {
        val testMessage = createRandomMessageNoAttr()

        val magicCookie = 0x2112A442
        val length = 20

        val msgBytes = createMockMessageBytes(
            testMessage.type,
            length,
            magicCookie,
            testMessage.transactionId
        )
        val msg = Message.tryFromPacket(ByteReadPacket(msgBytes))

        assertEquals(testMessage.type, msg.type)
        assertEquals(length, msg.length())
        assertContentEquals(testMessage.transactionId, msg.transactionId)
    }

    @Test
    fun `magic cookie gets verified`() {
        val msgBytes = createMockMessageBytes(
            cookie = 1337,
            transactionId = Random.Default.nextBytes(12)
        )
        assertFailsWith(AssertionError::class) {
            val msg = Message.tryFromPacket(ByteReadPacket(msgBytes))
        }
    }
}