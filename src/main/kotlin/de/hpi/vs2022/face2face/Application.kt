package de.hpi.vs2022.face2face

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
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
            LOG.info("datagram received: ${datagram.address}, ${datagram.packet.readText()}")
        }
    }
}
