package de.hpi.vs2022.face2face.stun.attributes

import de.hpi.vs2022.face2face.stun.Attribute
import io.ktor.utils.io.core.*
import java.nio.ByteBuffer

class MappedAddress : Attribute {
    override val type: Short = 0x0001

    override fun length(): Short {
        TODO("Not yet implemented")
    }

    override fun putBytes(buffer: ByteBuffer) {
        TODO("Not yet implemented")
    }

    override fun valueFromPacket(buffer: ByteReadPacket) {
        TODO("Not yet implemented")
    }

}