package de.hpi.vs2022.face2face.stun

data class Message(
    val type: Type,
    val transactionId: Long,
    val attributes: List<Attribute>
) {

    fun getBytes(): ByteArray = attributes.flatMap { it.getBytes().toList() }.toByteArray()

    enum class Type(val value: UShort) {
        BindingRequest(0x0001u),
        BindingResponse(0x0101u);
    }
}
