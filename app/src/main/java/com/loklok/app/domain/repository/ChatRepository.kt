package com.loklok.app.domain.repository

import com.loklok.app.domain.model.ChatMessage
import com.loklok.app.domain.model.Conversation
import com.loklok.app.domain.model.MessageType
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getConversations(): Flow<List<Conversation>>
    fun getMessages(conversationId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(
        conversationId: String,
        content: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String? = null
    ): Result<ChatMessage>
    suspend fun markAsRead(conversationId: String)
    suspend fun createOrGetDirectConversation(targetUserId: String, targetUserName: String): Conversation
}
