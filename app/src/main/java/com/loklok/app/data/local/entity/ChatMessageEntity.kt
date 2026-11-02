package com.loklok.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.loklok.app.domain.model.DeliveryStatus
import com.loklok.app.domain.model.MessageType

@Entity(
    tableName = "chat_messages",
    indices = [
        Index(value = ["conversationId"]),
        Index(value = ["timestamp"])
    ]
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val encryptedContent: String,
    val messageType: MessageType,
    val mediaUrl: String? = null,
    val mediaThumbnail: String? = null,
    val mediaDurationMs: Long? = null,
    val timestamp: Long,
    val status: DeliveryStatus,
    val isEncrypted: Boolean,
    val isIncoming: Boolean
)
