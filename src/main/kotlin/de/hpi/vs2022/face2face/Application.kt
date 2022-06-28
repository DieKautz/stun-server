package de.hpi.vs2022.face2face

import de.hpi.vs2022.face2face.rfc5389.MessageType
import de.hpi.vs2022.face2face.rfc5389.STUNBindingResponseMessage
import de.hpi.vs2022.face2face.rfc5389.STUNMessage
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

val LOG = LoggerFactory.getLogger("ktor.application")

fun main() {

    val host = System.getenv().getOrDefault("PORT", "127.0.0.1")
    val port = System.getenv().getOrDefault("PORT", "3478").toInt()
    runBlocking {
        val serverSocket  = aSocket(SelectorManager(Dispatchers.IO))
            .udp()
            .bind(InetSocketAddress(host, port))
        println("Listening on ${serverSocket.localAddress}")
        while (true) {
            val datagram = serverSocket.incoming.receive()
            val stunMessage = STUNMessage(datagram.packet)
            LOG.info("datagram received from: ${datagram.address.toJavaAddress().hostname}")
            LOG.info("STUN Message: $stunMessage")
            when(stunMessage.messageType) {
                MessageType.BindingRequest -> {
                    datagram.packet.close()
//                    val bindResponse = STUNBindingResponseMessage(stunMessage, datagram.address)
                    LOG.info("Message type is binding request!")
                }
                else -> LOG.info("unknown message type!")
            }
        }
    }
}
