package com.loklok.app.feature.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokAvatar
import com.loklok.app.core.designsystem.theme.LokLokPrimary
import com.loklok.app.domain.model.Conversation
import com.loklok.app.domain.model.DeliveryStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationsListScreen(
    viewModel: ConversationsViewModel,
    onConversationClick: (String) -> Unit,
    onNewChatClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewChatClick,
                containerColor = LokLokPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.AddComment,
                    contentDescription = "New Chat",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Search chats or contacts…", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LokLokPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            // Conversations List
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = uiState.conversations,
                    key = { it.id }
                ) { conversation ->
                    ConversationListItem(
                        conversation = conversation,
                        onClick = { onConversationClick(conversation.id) }
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(start = 76.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationListItem(
    conversation: Conversation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LokLokAvatar(
            name = conversation.title,
            imageUrl = conversation.avatarUrl,
            size = 52.dp,
            isOnline = !conversation.isGroup
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault())
                    .format(Date(conversation.updatedAt))

                Text(
                    text = formattedTime,
                    fontSize = 12.sp,
                    color = if (conversation.unreadCount > 0) LokLokPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val lastMsg = conversation.lastMessage
                if (lastMsg != null && !lastMsg.isIncoming) {
                    Icon(
                        imageVector = if (lastMsg.status == DeliveryStatus.READ) Icons.Default.DoneAll else Icons.Default.Done,
                        contentDescription = "Status",
                        tint = if (lastMsg.status == DeliveryStatus.READ) LokLokPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                }

                Text(
                    text = conversation.lastMessage?.content ?: "No messages yet",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (conversation.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                }

                if (conversation.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(LokLokPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
