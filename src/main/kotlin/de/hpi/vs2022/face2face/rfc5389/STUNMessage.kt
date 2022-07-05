package de.hpi.vs2022.face2face.rfc5389

import io.ktor.utils.io.core.*

// 0                   1                   2                   3
// 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
// |0 0|     STUN Message Type     |         Message Length        |
// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
// |                         Magic Cookie                          |
// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
// |                                                               |
// |                     Transaction ID (96 bits)                  |
// |                                                               |
// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

// ref: https://datatracker.ietf.org/doc/html/rfc5389#section-6
/*

 */
data class STUNMessage(
    val messageType: MessageType,
    val messageLength: UShort,
    val magicCookie: UInt = 0x2112A442u,
    val txId: ByteArray
) {
    init {
        if(magicCookie != 0x2112A442u) {
            throw IllegalStateException("Datagram is not a STUN Message as of RFC5389!")
        }
    }
    @OptIn(ExperimentalUnsignedTypes::class)
    // throw away first 2 bits
    constructor(buffer: ByteReadPacket): this(
        MessageType.from(buffer.readUShort() and (0xFF shr 2).toUShort())!!,
        buffer.readUShort(),
        buffer.readUInt(),
        buffer.readBytes(12)
    )
}
