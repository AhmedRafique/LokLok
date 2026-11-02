package com.loklok.app.feature.chats

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokAvatar
import com.loklok.app.core.designsystem.components.LokLokTopBar
import com.loklok.app.core.designsystem.theme.BubbleReceiverDark
import com.loklok.app.core.designsystem.theme.BubbleReceiverLight
import com.loklok.app.core.designsystem.theme.BubbleSenderDark
import com.loklok.app.core.designsystem.theme.BubbleSenderLight
import com.loklok.app.core.designsystem.theme.LokLokPrimary
import com.loklok.app.domain.model.ChatMessage
import com.loklok.app.domain.model.DeliveryStatus
import com.loklok.app.domain.model.MessageType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel,
    onVoiceCallClick: (String, String) -> Unit,
    onVideoCallClick: (String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat Top Bar with Calling actions
        LokLokTopBar(
            title = uiState.conversationTitle,
            subtitle = if (uiState.isOnline) "Online" else "Last seen recently",
            onNavigateBack = onNavigateBack,
            actions = {
                IconButton(onClick = { onVideoCallClick(viewModel.conversationId, uiState.conversationTitle) }) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = LokLokPrimary
                    )
                }
                IconButton(onClick = { onVoiceCallClick(viewModel.conversationId, uiState.conversationTitle) }) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Voice Call",
                        tint = LokLokPrimary
                    )
                }
            }
        )

        // E2EE Security Shield Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = LokLokPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Messages and calls are end-to-end encrypted.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            reverseLayout = false
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                ChatBubble(message = message)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Bottom Chat Composer
        ChatComposer(
            inputText = uiState.inputText,
            onInputChange = viewModel::onInputTextChange,
            onSendMessage = viewModel::sendMessage,
            onSendVoiceNote = { viewModel.sendVoiceNote(4000) }
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isIncoming = message.isIncoming
    val alignment = if (isIncoming) Alignment.Start else Alignment.End

    val bubbleColor = if (isIncoming) {
        if (androidx.compose.foundation.isSystemInDarkTheme()) BubbleReceiverDark else BubbleReceiverLight
    } else {
        if (androidx.compose.foundation.isSystemInDarkTheme()) BubbleSenderDark else BubbleSenderLight
    }

    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isIncoming) 2.dp else 16.dp,
        bottomEnd = if (isIncoming) 16.dp else 2.dp
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            shadowElevation = 0.5.dp,
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (message.type == MessageType.VOICE_NOTE) {
                    // Voice Note Player widget
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LokLokPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(LokLokPrimary.copy(alpha = 0.3f))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "0:04",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    Text(
                        text = message.content,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault())
                        .format(Date(message.timestamp))

                    Text(
                        text = timeStr,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    if (!isIncoming) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (message.status == DeliveryStatus.READ) Icons.Default.DoneAll else Icons.Default.Done,
                            contentDescription = "Status",
                            tint = if (message.status == DeliveryStatus.READ) LokLokPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatComposer(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onSendVoiceNote: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Open media picker */ }) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = { Text("Type a message…", fontSize = 15.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LokLokPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (inputText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(LokLokPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onSendMessage) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(LokLokPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onSendVoiceNote) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Note",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
