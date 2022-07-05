package de.hpi.vs2022.face2face.rfc5389

import de.hpi.vs2022.face2face.rfc5389.attributes.STUNMappedAddress
import io.ktor.utils.io.bits.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class STUNBindingResponseMessage(
    val messageType: MessageType,
    val messageLength: UShort,
    val magicCookie: UInt = 0x2112A442u,
    val txId: ByteArray,
    val port: Int,
    val address: ByteArray,
) {
    constructor(stunMessage: STUNMessage, address: java.net.InetSocketAddress) : this(
        MessageType.BindingResponse,
        stunMessage.messageLength,
        stunMessage.magicCookie,
        stunMessage.txId,
        address.port,
        address.address.address,
    )

    fun bytes(): ByteArray {
        val mappedAddress = STUNMappedAddress(port, address)
        val mappedAddressBytes = mappedAddress.bytes()
        val payloadSize = mappedAddressBytes.size.toShort()

        val byteBuffer = ByteBuffer.allocate(20 + payloadSize)

        val typeShort = messageType.value.toShort()
        byteBuffer.putShort(typeShort)
        byteBuffer.putShort(payloadSize)
        byteBuffer.putInt(magicCookie.toInt())
        byteBuffer.put(txId)
        byteBuffer.put(mappedAddressBytes)

        return byteBuffer.array()
    }
}
