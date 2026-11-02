package com.loklok.app.domain.model

data class Conversation(
    val id: String,
    val title: String,
    val isGroup: Boolean = false,
    val participantIds: List<String> = emptyList(),
    val avatarUrl: String? = null,
    val lastMessage: ChatMessage? = null,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isTyping: Boolean = false,
    val typingUserName: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
