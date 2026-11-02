package com.loklok.app.domain.model

enum class CallMediaType {
    VOICE,
    VIDEO
}

enum class CallDirection {
    INCOMING,
    OUTGOING
}

enum class CallState {
    INITIALIZING,
    RINGING,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    ENDED,
    MISSED,
    DECLINED,
    BUSY
}

data class CallSession(
    val callId: String,
    val remoteUserId: String,
    val remoteUserName: String,
    val remoteUserAvatar: String? = null,
    val mediaType: CallMediaType = CallMediaType.VOICE,
    val direction: CallDirection = CallDirection.OUTGOING,
    val state: CallState = CallState.INITIALIZING,
    val liveKitRoomName: String = callId,
    val liveKitToken: String? = null,
    val liveKitServerUrl: String? = null,
    val isMicMuted: Boolean = false,
    val isCameraEnabled: Boolean = (mediaType == CallMediaType.VIDEO),
    val isSpeakerOn: Boolean = false,
    val startTimestamp: Long = System.currentTimeMillis(),
    val connectedTimestamp: Long? = null,
    val endTimestamp: Long? = null,
    val durationSeconds: Long = 0L
)
