package de.hpi.vs2022.face2face.rfc5389

enum class MessageType(val value: UShort) {

    BindingRequest(0x0001u),
    BindingResponse(0x0101u);

    companion object {
        private val map = MessageType.values().associateBy(MessageType::value)
        fun from(type: UShort) = map[type]
    }
}