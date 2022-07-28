package de.hpi.vs2022.face2face

import de.hpi.vs2022.face2face.stun.Message
import org.jetbrains.annotations.TestOnly
import kotlin.experimental.xor
import kotlin.random.Random

@TestOnly
internal fun createRandomMessageNoAttr() = Message(
    Message.Type.values().random(),
    Random.nextBytes(12)
)

fun ByteArray.xor(other: ByteArray): ByteArray = mapIndexed { index: Int, byte: Byte ->
    return@mapIndexed byte xor other[index]
}.toByteArray()


fun Int.toByteArray() = byteArrayOf(
    (this shr 24).toByte(),
    (this shr 16).toByte(),
    (this shr 8).toByte(),
    (this shr 0).toByte(),
)