package de.hpi.vs2022.face2face.stun.attributes

import de.hpi.vs2022.face2face.stun.Attribute

class MappedAddress : Attribute {
    override val type: Short = 0x0001

    override fun length(): Short {
        TODO("Not yet implemented")
    }

    override fun getBytes(): ByteArray {
        TODO("Not yet implemented")
    }
}