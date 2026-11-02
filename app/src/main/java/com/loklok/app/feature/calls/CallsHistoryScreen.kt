package com.loklok.app.feature.calls

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.AddCall
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokAvatar
import com.loklok.app.core.designsystem.theme.LokLokCallAccept
import com.loklok.app.core.designsystem.theme.LokLokCallEnd
import com.loklok.app.core.designsystem.theme.LokLokPrimary
import com.loklok.app.domain.model.CallDirection
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallSession
import com.loklok.app.domain.model.CallState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallsHistoryScreen(
    viewModel: CallsHistoryViewModel,
    onStartCall: (String, String, CallMediaType) -> Unit,
    onNewCallClick: () -> Unit
) {
    val callHistory by viewModel.callHistory.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewCallClick,
                containerColor = LokLokPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.AddCall,
                    contentDescription = "New Call",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(
                items = callHistory,
                key = { it.callId }
            ) { call ->
                CallHistoryItem(
                    call = call,
                    onRedial = {
                        onStartCall(call.remoteUserId, call.remoteUserName, call.mediaType)
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
fun CallHistoryItem(
    call: CallSession,
    onRedial: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onRedial)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LokLokAvatar(
            name = call.remoteUserName,
            imageUrl = call.remoteUserAvatar,
            size = 50.dp
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            val isMissed = call.state == CallState.MISSED || call.state == CallState.DECLINED
            Text(
                text = call.remoteUserName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isMissed) LokLokCallEnd else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when {
                    isMissed -> Icons.AutoMirrored.Filled.CallMissed
                    call.direction == CallDirection.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
                    else -> Icons.AutoMirrored.Filled.CallMade
                }

                val iconColor = when {
                    isMissed -> LokLokCallEnd
                    call.direction == CallDirection.INCOMING -> LokLokCallAccept
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }

                Icon(
                    imageVector = icon,
                    contentDescription = "Direction",
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                val formattedDate = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                    .format(Date(call.startTimestamp))

                Text(
                    text = formattedDate,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        IconButton(onClick = onRedial) {
            Icon(
                imageVector = if (call.mediaType == CallMediaType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                contentDescription = "Redial",
                tint = LokLokPrimary
            )
        }
    }
}
