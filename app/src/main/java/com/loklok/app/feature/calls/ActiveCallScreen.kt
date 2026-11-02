package com.loklok.app.feature.calls

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.loklok.app.core.designsystem.components.LokLokAvatar
import com.loklok.app.core.designsystem.components.LokLokCallButton
import com.loklok.app.core.designsystem.theme.BgDark
import com.loklok.app.core.designsystem.theme.LokLokCallAccept
import com.loklok.app.core.designsystem.theme.LokLokCallEnd
import com.loklok.app.core.designsystem.theme.LokLokPrimary
import com.loklok.app.domain.model.CallDirection
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallState
import io.livekit.android.renderer.TextureViewRenderer

@Composable
fun ActiveCallScreen(
    viewModel: CallViewModel,
    onCallEnded: () -> Unit
) {
    val session by viewModel.activeSession.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val roomState by viewModel.liveKitCallManager.roomState.collectAsState()

    if (session == null || session?.state == CallState.ENDED || session?.state == CallState.DECLINED) {
        onCallEnded()
        return
    }

    val currentSession = session!!
    val isIncomingRinging = currentSession.direction == CallDirection.INCOMING && currentSession.state == CallState.RINGING
    val isVideo = currentSession.mediaType == CallMediaType.VIDEO

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            )
    ) {
        // Fullscreen Remote Video Track if available
        if (isVideo && roomState.remoteVideoTrack != null) {
            AndroidView(
                factory = { context ->
                    TextureViewRenderer(context).apply {
                        viewModel.liveKitCallManager.initVideoRenderer(this)
                        roomState.remoteVideoTrack?.addRenderer(this)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Floating Local Video Track (PIP) in top-right
        if (isVideo && roomState.localVideoTrack != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
                    .size(width = 110.dp, height = 150.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            ) {
                AndroidView(
                    factory = { context ->
                        TextureViewRenderer(context).apply {
                            viewModel.liveKitCallManager.initVideoRenderer(this)
                            roomState.localVideoTrack?.addRenderer(this)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Top Info Bar: Caller details & Encrypted tag
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // E2EE Lock Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted",
                    tint = LokLokPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "End-to-End Encrypted",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentSession.remoteUserName,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            val callStatusText = when (currentSession.state) {
                CallState.RINGING -> if (currentSession.direction == CallDirection.INCOMING) "Incoming ${currentSession.mediaType.name.lowercase().replaceFirstChar { it.uppercase() }} Call…" else "Ringing…"
                CallState.CONNECTING -> "Connecting…"
                CallState.CONNECTED -> uiState.callDurationText
                CallState.RECONNECTING -> "Reconnecting…"
                else -> ""
            }

            Text(
                text = callStatusText,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 15.sp
            )
        }

        // Center: Pulsing Avatar for Voice Calls
        if (!isVideo || roomState.remoteVideoTrack == null) {
            val infiniteTransition = rememberInfiniteTransition()
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                if (currentSession.state == CallState.RINGING) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(LokLokPrimary.copy(alpha = 0.25f))
                    )
                }

                LokLokAvatar(
                    name = currentSession.remoteUserName,
                    imageUrl = currentSession.remoteUserAvatar,
                    size = 110.dp
                )
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isIncomingRinging) {
                // Incoming Call: Accept (Green) and Decline (Red)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LokLokCallButton(
                        icon = Icons.Default.CallEnd,
                        label = "Decline",
                        backgroundColor = LokLokCallEnd,
                        onClick = viewModel::declineCall,
                        size = 72.dp
                    )

                    LokLokCallButton(
                        icon = Icons.Default.Call,
                        label = "Accept",
                        backgroundColor = LokLokCallAccept,
                        onClick = viewModel::acceptCall,
                        size = 72.dp
                    )
                }
            } else {
                // Active In-Call Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LokLokCallButton(
                        icon = if (uiState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        label = if (uiState.isMicMuted) "Unmute" else "Mute",
                        isActive = uiState.isMicMuted,
                        onClick = viewModel::toggleMute
                    )

                    LokLokCallButton(
                        icon = Icons.Default.VolumeUp,
                        label = "Speaker",
                        isActive = uiState.isSpeakerOn,
                        onClick = viewModel::toggleSpeaker
                    )

                    if (isVideo) {
                        LokLokCallButton(
                            icon = Icons.Default.Cameraswitch,
                            label = "Flip",
                            onClick = viewModel::flipCamera
                        )

                        LokLokCallButton(
                            icon = if (uiState.isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            label = "Video",
                            isActive = uiState.isCameraEnabled,
                            onClick = viewModel::toggleCamera
                        )
                    }

                    LokLokCallButton(
                        icon = Icons.Default.CallEnd,
                        label = "End",
                        backgroundColor = LokLokCallEnd,
                        onClick = viewModel::endCall
                    )
                }
            }
        }
    }
}
