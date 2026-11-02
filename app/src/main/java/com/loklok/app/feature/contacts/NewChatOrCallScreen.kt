package com.loklok.app.feature.contacts

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokAvatar
import com.loklok.app.core.designsystem.components.LokLokTopBar
import com.loklok.app.core.designsystem.theme.LokLokPrimary
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.Contact

@Composable
fun NewChatOrCallScreen(
    viewModel: ContactsViewModel,
    onContactSelectForChat: (Contact) -> Unit,
    onStartCall: (String, String, CallMediaType) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LokLokTopBar(
            title = "Select Contact",
            subtitle = "${uiState.contacts.size} contacts",
            onNavigateBack = onNavigateBack
        )

        // Search Input
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text("Search name or number…", fontSize = 14.sp) },
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

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // New Group & New Contact shortcuts
            item {
                ActionShortcutRow(icon = Icons.Default.GroupAdd, title = "New Group")
                ActionShortcutRow(icon = Icons.Default.PersonAdd, title = "New Contact")
                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 76.dp)
                )
            }

            items(
                items = uiState.contacts,
                key = { it.id }
            ) { contact ->
                ContactListItem(
                    contact = contact,
                    onChatClick = { onContactSelectForChat(contact) },
                    onVoiceCallClick = {
                        val userId = contact.registeredUserId ?: contact.phoneNumber
                        onStartCall(userId, contact.name, CallMediaType.VOICE)
                    },
                    onVideoCallClick = {
                        val userId = contact.registeredUserId ?: contact.phoneNumber
                        onStartCall(userId, contact.name, CallMediaType.VIDEO)
                    }
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

@Composable
fun ActionShortcutRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = LokLokPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ContactListItem(
    contact: Contact,
    onChatClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onVideoCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onChatClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LokLokAvatar(
            name = contact.name,
            imageUrl = contact.avatarUrl,
            size = 48.dp,
            isOnline = contact.isLokLokUser
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = contact.statusBio ?: contact.phoneNumber,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1
            )
        }

        if (contact.isLokLokUser) {
            IconButton(onClick = onVoiceCallClick) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Voice Call",
                    tint = LokLokPrimary
                )
            }
            IconButton(onClick = onVideoCallClick) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video Call",
                    tint = LokLokPrimary
                )
            }
        } else {
            Text(
                text = "INVITE",
                color = LokLokPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { /* Send invite SMS */ }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}
