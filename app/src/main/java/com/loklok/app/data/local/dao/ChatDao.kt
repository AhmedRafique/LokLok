package com.loklok.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.loklok.app.data.local.entity.ChatMessageEntity
import com.loklok.app.data.local.entity.ConversationEntity
import com.loklok.app.domain.model.DeliveryStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // Conversations
    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :conversationId")
    suspend fun clearUnreadCount(conversationId: String)

    // Messages
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("UPDATE chat_messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: DeliveryStatus)

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)
}
