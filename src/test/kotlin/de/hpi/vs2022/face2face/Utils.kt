package de.hpi.vs2022.face2face

import de.hpi.vs2022.face2face.stun.Message
import org.jetbrains.annotations.TestOnly
import kotlin.random.Random

@TestOnly
internal fun createRandomMessageNoAttr() = Message(
    Message.Type.values().random(),
    Random.nextBytes(12)
)