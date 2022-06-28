package de.hpi.vs2022.face2face.rfc5389

import io.ktor.network.sockets.*

class STUNBindingResponseMessage(
    val messageType: MessageType,
    val messageLength: UShort,
    val magicCookie: UInt = 0x2112A442u,
    val txId: ULong,
    val address: UInt,
) {
    constructor(stunMessage: STUNMessage, address: UInt) : this(
        stunMessage.messageType,
        stunMessage.messageLength,
        stunMessage.magicCookie,
        stunMessage.txId,
        address
    )

    fun datagram(address: SocketAddress) {
    }
}
