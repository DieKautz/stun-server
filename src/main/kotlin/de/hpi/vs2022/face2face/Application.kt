package de.hpi.vs2022.face2face

import de.hpi.vs2022.face2face.rfc5389.MessageType
import de.hpi.vs2022.face2face.rfc5389.STUNBindingResponseMessage
import de.hpi.vs2022.face2face.rfc5389.STUNMessage
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

val LOG = LoggerFactory.getLogger("ktor.application")

fun main() {

    val host = System.getenv().getOrDefault("HOST", "127.0.0.1")
    val port = System.getenv().getOrDefault("PORT", "3478").toInt()
    runBlocking {
        val serverSocket  = aSocket(SelectorManager(Dispatchers.IO))
            .udp()
            .bind(InetSocketAddress(host, port))
        LOG.info("Listening on ${serverSocket.localAddress}")
        while (true) {
            val datagram = serverSocket.incoming.receive()
            val stunMessage = STUNMessage(datagram.packet)
            val socketAddress = (datagram.address.toJavaAddress() as java.net.InetSocketAddress)

            LOG.info("datagram received from: ${socketAddress.address.hostAddress}")
            when(stunMessage.messageType) {
                MessageType.BindingRequest -> {
                    datagram.packet.close()
                    LOG.info("Message type is binding request! txid:${stunMessage.txId.toHex()}")
                    val response = STUNBindingResponseMessage(stunMessage, socketAddress)
                    LOG.info("Responding with ${response.bytes().toHex()}")
                    val responseDatagram = Datagram(ByteReadPacket(response.bytes()), datagram.address)
                    serverSocket.send(responseDatagram)
                }
                else -> LOG.info("unknown message type!")
            }
        }
    }
}
