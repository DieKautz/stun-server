package de.hpi.vs2022.face2face

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import de.hpi.vs2022.face2face.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSockets()
    }.start(wait = true)
}
