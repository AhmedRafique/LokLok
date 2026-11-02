package com.loklok.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isGroup: Boolean,
    val participantIdsCsv: String,
    val avatarUrl: String? = null,
    val lastMessageText: String? = null,
    val lastMessageTimestamp: Long = 0L,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
