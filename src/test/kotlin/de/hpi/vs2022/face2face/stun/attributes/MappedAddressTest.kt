package de.hpi.vs2022.face2face.stun.attributes

import de.hpi.vs2022.face2face.stun.Attribute
import de.hpi.vs2022.face2face.stun.attributes.MappedAddress.IPFamily
import io.ktor.utils.io.core.*
import org.junit.Test
import java.nio.ByteBuffer
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class MappedAddressTest {

    private fun createMockAttributeBytes(
        family: IPFamily,
        port: Short = Random.Default.nextInt().toShort(),
        address: ByteArray = Random.Default.nextBytes(family.sizeInBytes)
    ): ByteArray {
        val testBuff = ByteBuffer.allocate(4 + 4 + address.size)
        testBuff.apply {
            putShort(0x0001)
            putShort((4 + address.size).toShort())
            put(0)
            put(family.value)
            putShort(port)
            put(address)
        }
        return testBuff.array()
    }

    @Test
    fun `ipv4 address gets mapped correctly`() {
        val family = IPFamily.V4
        val port = Random.Default.nextInt().toShort()
        val address = Random.Default.nextBytes(family.sizeInBytes)

        val bytes = createMockAttributeBytes(family, port, address)
        val attribute = Attribute.tryFromPacket(ByteReadPacket(bytes))
        assert(attribute is MappedAddress)
        attribute as MappedAddress

        assertEquals(attribute.family, family)
        assertEquals(attribute.port, port)
        assertContentEquals(attribute.address, address)
    }

    @Test
    fun `ipv6 address gets mapped correctly`() {
        val family = IPFamily.V6
        val port = Random.Default.nextInt().toShort()
        val address = Random.Default.nextBytes(family.sizeInBytes)

        val bytes = createMockAttributeBytes(family, port, address)
        val attribute = Attribute.tryFromPacket(ByteReadPacket(bytes))
        assert(attribute is MappedAddress)
        attribute as MappedAddress

        assertEquals(attribute.family, family)
        assertEquals(attribute.port, port)
        assertContentEquals(attribute.address, address)
    }
}