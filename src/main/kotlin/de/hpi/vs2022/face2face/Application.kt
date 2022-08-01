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
        val serverSocket = aSocket(SelectorManager(Dispatchers.IO))
            .udp()
            .bind(InetSocketAddress(host, port))
        LOG.info("Listening on ${serverSocket.localAddress}")
        while (true) {
            val datagram = serverSocket.incoming.receive()
            val incomingMessage = Message.tryFromPacket(datagram.packet)
            val socketAddress = (datagram.address.toJavaAddress() as java.net.InetSocketAddress)

            LOG.debug("datagram received from: ${socketAddress.address.hostAddress}")
            when (incomingMessage.type) {
                Message.Type.BindingRequest -> {
                    datagram.packet.close()
                    LOG.debug("Message type is binding request! txid:${incomingMessage.transactionId.toHex()}")

                    val response = Message(Message.Type.BindingResponse, incomingMessage.transactionId)
                    response.attributes += XorMappedAddress(socketAddress)
                    val responseBuff = ByteBuffer.allocate(response.length())
                    response.putBytes(responseBuff)

                    LOG.debug("Responding with ${responseBuff.array().toHex()}")
                    val responseDatagram = Datagram(ByteReadPacket(responseBuff.array()), datagram.address)
                    serverSocket.send(responseDatagram)
                }

                else -> LOG.warn("Unknown message type! Message: $incomingMessage")
            }
        }
    }
}
