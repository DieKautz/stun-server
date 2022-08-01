package de.hpi.vs2022.face2face

import de.hpi.vs2022.face2face.stun.Message
import de.hpi.vs2022.face2face.stun.attributes.XorMappedAddress
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

val LOG = LoggerFactory.getLogger("ktor.application")

fun main() {

    val host = System.getenv().getOrDefault("HOST", "127.0.0.1")
    val port = System.getenv().getOrDefault("PORT", "3478").toInt()
    runBlocking {
        val udpServerSocket = aSocket(SelectorManager(Dispatchers.IO))
            .udp()
            .bind(InetSocketAddress(host, port))
        LOG.info("UDP Listening on ${udpServerSocket.localAddress}")
        while (true) {
            val datagram = udpServerSocket.incoming.receive()
            val javaSocketAddress = (datagram.address.toJavaAddress() as java.net.InetSocketAddress)
            LOG.debug("UDP datagram received from: ${javaSocketAddress.address}")
            LOG.debug("UDP datagram content: ${datagram.packet.copy().readBytes().toHex()}")
            handlePacket(datagram.packet, javaSocketAddress)?.let { responsePacket ->
                udpServerSocket.send(Datagram(responsePacket, datagram.address))
            }
        }
    }
}

private fun handlePacket(readPacket: ByteReadPacket, javaSocketAddress: java.net.InetSocketAddress): ByteReadPacket? {
    try {
        val incomingMessage = Message.tryFromPacket(readPacket)
        when (incomingMessage.type) {
            Message.Type.BindingRequest -> {
                LOG.debug("Message type is binding request! txid:${incomingMessage.transactionId.toHex()}")
                readPacket.close()

                val response = Message(Message.Type.BindingResponse, incomingMessage.transactionId)
                response.attributes += XorMappedAddress(javaSocketAddress)
                val responseBuff = ByteBuffer.allocate(response.length())
                response.putBytes(responseBuff)

                LOG.debug("Responding with ${responseBuff.array().toHex()}")
                return ByteReadPacket(responseBuff.array())
            }

            else -> LOG.warn("Unknown message type! Message: $incomingMessage")
        }
    } catch (ex: Exception) {
        LOG.warn("Uncaught Exception! ${ex.localizedMessage}")
    }
    return null
}
