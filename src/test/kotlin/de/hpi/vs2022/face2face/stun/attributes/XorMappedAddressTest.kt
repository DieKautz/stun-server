package de.hpi.vs2022.face2face.stun.attributes

import de.hpi.vs2022.face2face.createRandomMessageNoAttr
import de.hpi.vs2022.face2face.stun.Attribute
import de.hpi.vs2022.face2face.stun.attributes.MappedAddress.IPFamily
import de.hpi.vs2022.face2face.toByteArray
import de.hpi.vs2022.face2face.xor
import io.ktor.utils.io.core.*
import org.junit.Test
import java.nio.ByteBuffer
import kotlin.experimental.xor
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class XorMappedAddressTest {
    private val magicCookie: Int = 0x2112A442

    private fun createMockAttributeBytes(
        family: IPFamily,
        port: Short = Random.Default.nextInt().toShort(),
        address: ByteArray = Random.Default.nextBytes(family.sizeInBytes),
        transactionId: ByteArray
    ): ByteArray {
        val testBuff = ByteBuffer.allocate(4 + 4 + address.size)

        val addressXored = when (family) {
            IPFamily.V4 -> address.xor(magicCookie.toByteArray())
            IPFamily.V6 -> address.xor(magicCookie.toByteArray() + transactionId)
        }
        testBuff.apply {
            putShort(0x0020)
            putShort((4 + address.size).toShort())
            put(0)
            put(family.value)
            putShort(port xor ((magicCookie shr 16).toShort()))
            put(addressXored)
        }
        return testBuff.array()
    }

    @Test
    fun `ipv4 address gets mapped correctly`() {
        val family = IPFamily.V4
        val port = Random.Default.nextInt().toShort()
        val address = Random.Default.nextBytes(family.sizeInBytes)
        // its not about the message object in this test, just the persistent tx id
        val _message = createRandomMessageNoAttr()

        val bytes = createMockAttributeBytes(family, port, address, _message.transactionId)
        val attribute = Attribute.tryFromPacket(ByteReadPacket(bytes), _message)
        assert(attribute is XorMappedAddress)
        attribute as XorMappedAddress

        assertEquals(attribute.family, family)
        assertEquals(attribute.port, port)
        assertContentEquals(attribute.address, address)
    }

    @Test
    fun `ipv6 address gets mapped correctly`() {
        val family = IPFamily.V6
        val port = Random.Default.nextInt().toShort()
        val address = Random.Default.nextBytes(family.sizeInBytes)
        // its not about the message object in this test, just the persistent tx id
        val _message = createRandomMessageNoAttr()

        val bytes = createMockAttributeBytes(family, port, address, _message.transactionId)
        val attribute = Attribute.tryFromPacket(ByteReadPacket(bytes), _message)
        assert(attribute is XorMappedAddress)
        attribute as XorMappedAddress

        assertEquals(attribute.family, family)
        assertEquals(attribute.port, port)
        assertContentEquals(attribute.address, address)
    }
}