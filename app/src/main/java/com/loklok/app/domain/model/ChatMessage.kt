package com.loklok.app.domain.model

enum class MessageType {
    TEXT,
    IMAGE,
    VOICE_NOTE,
    VIDEO,
    DOCUMENT,
    LOCATION,
    CALL_LOG
}

enum class DeliveryStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val mediaThumbnail: String? = null,
    val mediaDurationMs: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: DeliveryStatus = DeliveryStatus.SENT,
    val isEncrypted: Boolean = true,
    val isIncoming: Boolean = false
)
