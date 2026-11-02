package com.loklok.app.data.repository

import com.loklok.app.core.crypto.CryptoManager
import com.loklok.app.core.network.LokLokWebSocketClient
import com.loklok.app.core.security.SecureStorage
import com.loklok.app.data.local.dao.ChatDao
import com.loklok.app.data.local.entity.ChatMessageEntity
import com.loklok.app.data.local.entity.ConversationEntity
import com.loklok.app.domain.model.ChatMessage
import com.loklok.app.domain.model.Conversation
import com.loklok.app.domain.model.DeliveryStatus
import com.loklok.app.domain.model.MessageType
import com.loklok.app.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val webSocketClient: LokLokWebSocketClient,
    private val cryptoManager: CryptoManager,
    private val secureStorage: SecureStorage
) : ChatRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        observeIncomingSocketMessages()
        seedInitialConversationsIfEmpty()
    }

    private fun observeIncomingSocketMessages() {
        scope.launch {
            webSocketClient.incomingEvents.collect { payload ->
                when (payload.event) {
                    LokLokWebSocketClient.EVENT_NEW_MESSAGE -> {
                        val data = payload.data
                        val id = data["id"] as? String ?: UUID.randomUUID().toString()
                        val convId = data["conversationId"] as? String ?: return@collect
                        val senderId = data["senderId"] as? String ?: ""
                        val senderName = data["senderName"] as? String ?: "User"
                        val encryptedContent = data["encryptedContent"] as? String ?: ""
                        val typeStr = data["type"] as? String ?: MessageType.TEXT.name
                        val type = runCatching { MessageType.valueOf(typeStr) }.getOrDefault(MessageType.TEXT)

                        val entity = ChatMessageEntity(
                            id = id,
                            conversationId = convId,
                            senderId = senderId,
                            senderName = senderName,
                            encryptedContent = encryptedContent,
                            messageType = type,
                            mediaUrl = data["mediaUrl"] as? String,
                            mediaThumbnail = null,
                            mediaDurationMs = null,
                            timestamp = System.currentTimeMillis(),
                            status = DeliveryStatus.DELIVERED,
                            isEncrypted = true,
                            isIncoming = true
                        )

                        chatDao.insertMessage(entity)

                        // Update conversation preview
                        val plainPreview = cryptoManager.decrypt(encryptedContent)
                        val conv = chatDao.getConversationById(convId)
                        if (conv != null) {
                            chatDao.insertOrUpdateConversation(
                                conv.copy(
                                    lastMessageText = plainPreview,
                                    lastMessageTimestamp = System.currentTimeMillis(),
                                    unreadCount = conv.unreadCount + 1,
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    LokLokWebSocketClient.EVENT_MESSAGE_STATUS -> {
                        val msgId = payload.data["messageId"] as? String ?: return@collect
                        val statusStr = payload.data["status"] as? String ?: return@collect
                        val status = runCatching { DeliveryStatus.valueOf(statusStr) }.getOrNull()
                        if (status != null) {
                            chatDao.updateMessageStatus(msgId, status)
                        }
                    }
                }
            }
        }
    }

    private fun seedInitialConversationsIfEmpty() {
        scope.launch {
            // Seed sample conversations if first launch
            val conv1 = ConversationEntity(
                id = "conv_ahmed",
                title = "Ahmed Al-Mansoori",
                isGroup = false,
                participantIdsCsv = "usr_ahmed",
                avatarUrl = null,
                lastMessageText = "Let's test the HD video call on Lok Lok today!",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                unreadCount = 1,
                isPinned = true,
                updatedAt = System.currentTimeMillis() - 1000 * 60 * 15
            )

            val conv2 = ConversationEntity(
                id = "conv_family",
                title = "Dubai Family & Friends 🇦🇪",
                isGroup = true,
                participantIdsCsv = "usr_ahmed,usr_fatima,usr_omar",
                avatarUrl = null,
                lastMessageText = "Fatima: The call clarity is crystal clear!",
                lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                unreadCount = 3,
                isPinned = false,
                updatedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2
            )

            chatDao.insertOrUpdateConversation(conv1)
            chatDao.insertOrUpdateConversation(conv2)

            // Seed initial messages
            val m1 = ChatMessageEntity(
                id = "m1",
                conversationId = "conv_ahmed",
                senderId = "usr_ahmed",
                senderName = "Ahmed",
                encryptedContent = cryptoManager.encrypt("Hey! How is Lok Lok working on your phone?"),
                messageType = MessageType.TEXT,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                status = DeliveryStatus.READ,
                isEncrypted = true,
                isIncoming = true
            )
            val m2 = ChatMessageEntity(
                id = "m2",
                conversationId = "conv_ahmed",
                senderId = secureStorage.getUserId(),
                senderName = "Me",
                encryptedContent = cryptoManager.encrypt("It's amazing! Low latency audio and zero blocks."),
                messageType = MessageType.TEXT,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 18,
                status = DeliveryStatus.READ,
                isEncrypted = true,
                isIncoming = false
            )
            val m3 = ChatMessageEntity(
                id = "m3",
                conversationId = "conv_ahmed",
                senderId = "usr_ahmed",
                senderName = "Ahmed",
                encryptedContent = cryptoManager.encrypt("Let's test the HD video call on Lok Lok today!"),
                messageType = MessageType.TEXT,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                status = DeliveryStatus.DELIVERED,
                isEncrypted = true,
                isIncoming = true
            )

            chatDao.insertMessages(listOf(m1, m2, m3))
        }
    }

    override fun getConversations(): Flow<List<Conversation>> {
        return chatDao.getAllConversations().map { entities ->
            entities.map { entity ->
                Conversation(
                    id = entity.id,
                    title = entity.title,
                    isGroup = entity.isGroup,
                    participantIds = entity.participantIdsCsv.split(",").filter { it.isNotBlank() },
                    avatarUrl = entity.avatarUrl,
                    lastMessage = entity.lastMessageText?.let { text ->
                        ChatMessage(
                            id = "",
                            conversationId = entity.id,
                            senderId = "",
                            senderName = "",
                            content = text,
                            timestamp = entity.lastMessageTimestamp
                        )
                    },
                    unreadCount = entity.unreadCount,
                    isPinned = entity.isPinned,
                    isMuted = entity.isMuted,
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    override fun getMessages(conversationId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForConversation(conversationId).map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    conversationId = entity.conversationId,
                    senderId = entity.senderId,
                    senderName = entity.senderName,
                    content = cryptoManager.decrypt(entity.encryptedContent),
                    type = entity.messageType,
                    mediaUrl = entity.mediaUrl,
                    mediaThumbnail = entity.mediaThumbnail,
                    mediaDurationMs = entity.mediaDurationMs,
                    timestamp = entity.timestamp,
                    status = entity.status,
                    isEncrypted = entity.isEncrypted,
                    isIncoming = entity.isIncoming
                )
            }
        }
    }

    override suspend fun sendMessage(
        conversationId: String,
        content: String,
        type: MessageType,
        mediaUrl: String?
    ): Result<ChatMessage> {
        val currentUserId = secureStorage.getUserId()
        val currentUserName = secureStorage.getDisplayName().ifBlank { "Me" }
        val messageId = "msg_" + UUID.randomUUID().toString().take(12)
        val encrypted = cryptoManager.encrypt(content)

        val entity = ChatMessageEntity(
            id = messageId,
            conversationId = conversationId,
            senderId = currentUserId,
            senderName = currentUserName,
            encryptedContent = encrypted,
            messageType = type,
            mediaUrl = mediaUrl,
            timestamp = System.currentTimeMillis(),
            status = DeliveryStatus.SENT,
            isEncrypted = true,
            isIncoming = false
        )

        chatDao.insertMessage(entity)

        // Update conversation's last message
        val conv = chatDao.getConversationById(conversationId)
        if (conv != null) {
            chatDao.insertOrUpdateConversation(
                conv.copy(
                    lastMessageText = content,
                    lastMessageTimestamp = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        // Send over WebSocket
        webSocketClient.sendEvent(
            LokLokWebSocketClient.EVENT_NEW_MESSAGE,
            mapOf(
                "id" to messageId,
                "conversationId" to conversationId,
                "senderId" to currentUserId,
                "senderName" to currentUserName,
                "encryptedContent" to encrypted,
                "type" to type.name,
                "mediaUrl" to mediaUrl
            )
        )

        val domainMessage = ChatMessage(
            id = messageId,
            conversationId = conversationId,
            senderId = currentUserId,
            senderName = currentUserName,
            content = content,
            type = type,
            mediaUrl = mediaUrl,
            timestamp = entity.timestamp,
            status = DeliveryStatus.SENT,
            isEncrypted = true,
            isIncoming = false
        )

        return Result.success(domainMessage)
    }

    override suspend fun markAsRead(conversationId: String) {
        chatDao.clearUnreadCount(conversationId)
    }

    override suspend fun createOrGetDirectConversation(
        targetUserId: String,
        targetUserName: String
    ): Conversation {
        val convId = "conv_$targetUserId"
        val existing = chatDao.getConversationById(convId)
        if (existing != null) {
            return Conversation(
                id = existing.id,
                title = existing.title,
                isGroup = existing.isGroup,
                participantIds = listOf(targetUserId)
            )
        }

        val newConv = ConversationEntity(
            id = convId,
            title = targetUserName,
            isGroup = false,
            participantIdsCsv = targetUserId,
            updatedAt = System.currentTimeMillis()
        )
        chatDao.insertOrUpdateConversation(newConv)

        return Conversation(
            id = newConv.id,
            title = newConv.title,
            isGroup = false,
            participantIds = listOf(targetUserId)
        )
    }
}
