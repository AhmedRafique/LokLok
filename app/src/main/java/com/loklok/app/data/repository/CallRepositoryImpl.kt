package com.loklok.app.data.repository

import com.loklok.app.core.network.LokLokWebSocketClient
import com.loklok.app.core.webrtc.LiveKitCallManager
import com.loklok.app.data.local.dao.CallDao
import com.loklok.app.data.local.entity.CallHistoryEntity
import com.loklok.app.domain.model.CallDirection
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallSession
import com.loklok.app.domain.model.CallState
import com.loklok.app.domain.repository.CallRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallRepositoryImpl @Inject constructor(
    private val callDao: CallDao,
    private val liveKitCallManager: LiveKitCallManager,
    private val webSocketClient: LokLokWebSocketClient
) : CallRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _activeCall = MutableStateFlow<CallSession?>(null)
    override val activeCall: StateFlow<CallSession?> = _activeCall.asStateFlow()

    init {
        observeSocketCallEvents()
        seedSampleCallHistory()
    }

    private fun observeSocketCallEvents() {
        scope.launch {
            webSocketClient.incomingEvents.collect { payload ->
                when (payload.event) {
                    LokLokWebSocketClient.EVENT_INCOMING_CALL -> {
                        val data = payload.data
                        val callId = data["callId"] as? String ?: return@collect
                        val callerId = data["callerId"] as? String ?: ""
                        val callerName = data["callerName"] as? String ?: "Caller"
                        val mediaTypeStr = data["mediaType"] as? String ?: CallMediaType.VOICE.name
                        val mediaType = runCatching { CallMediaType.valueOf(mediaTypeStr) }.getOrDefault(CallMediaType.VOICE)
                        val token = data["liveKitToken"] as? String
                        val serverUrl = data["liveKitServerUrl"] as? String ?: "wss://livekit.loklok.im"

                        val session = CallSession(
                            callId = callId,
                            remoteUserId = callerId,
                            remoteUserName = callerName,
                            mediaType = mediaType,
                            direction = CallDirection.INCOMING,
                            state = CallState.RINGING,
                            liveKitToken = token,
                            liveKitServerUrl = serverUrl
                        )
                        _activeCall.value = session
                    }
                    LokLokWebSocketClient.EVENT_CALL_ACCEPTED -> {
                        val current = _activeCall.value ?: return@collect
                        _activeCall.value = current.copy(
                            state = CallState.CONNECTED,
                            connectedTimestamp = System.currentTimeMillis()
                        )
                    }
                    LokLokWebSocketClient.EVENT_CALL_REJECTED,
                    LokLokWebSocketClient.EVENT_CALL_ENDED -> {
                        endCallInternal(CallState.ENDED)
                    }
                }
            }
        }
    }

    private fun seedSampleCallHistory() {
        scope.launch {
            val sampleCalls = listOf(
                CallHistoryEntity(
                    callId = "call_1",
                    remoteUserId = "usr_ahmed",
                    remoteUserName = "Ahmed Al-Mansoori",
                    remoteUserAvatar = null,
                    mediaType = CallMediaType.VIDEO,
                    direction = CallDirection.INCOMING,
                    state = CallState.CONNECTED,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 4,
                    durationSeconds = 245
                ),
                CallHistoryEntity(
                    callId = "call_2",
                    remoteUserId = "usr_fatima",
                    remoteUserName = "Fatima Zahra",
                    remoteUserAvatar = null,
                    mediaType = CallMediaType.VOICE,
                    direction = CallDirection.OUTGOING,
                    state = CallState.CONNECTED,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 26,
                    durationSeconds = 512
                ),
                CallHistoryEntity(
                    callId = "call_3",
                    remoteUserId = "usr_omar",
                    remoteUserName = "Omar Farooq",
                    remoteUserAvatar = null,
                    mediaType = CallMediaType.VOICE,
                    direction = CallDirection.INCOMING,
                    state = CallState.MISSED,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 52,
                    durationSeconds = 0
                )
            )
            sampleCalls.forEach { callDao.insertCallRecord(it) }
        }
    }

    override fun getCallHistory(): Flow<List<CallSession>> {
        return callDao.getAllCallHistory().map { entities ->
            entities.map { entity ->
                CallSession(
                    callId = entity.callId,
                    remoteUserId = entity.remoteUserId,
                    remoteUserName = entity.remoteUserName,
                    remoteUserAvatar = entity.remoteUserAvatar,
                    mediaType = entity.mediaType,
                    direction = entity.direction,
                    state = entity.state,
                    startTimestamp = entity.timestamp,
                    durationSeconds = entity.durationSeconds
                )
            }
        }
    }

    override suspend fun startCall(
        remoteUserId: String,
        remoteUserName: String,
        mediaType: CallMediaType
    ): Result<CallSession> {
        val callId = "call_" + UUID.randomUUID().toString().take(12)
        val serverUrl = "wss://livekit.loklok.im"
        val sampleToken = "jwt_livekit_" + UUID.randomUUID().toString()

        val session = CallSession(
            callId = callId,
            remoteUserId = remoteUserId,
            remoteUserName = remoteUserName,
            mediaType = mediaType,
            direction = CallDirection.OUTGOING,
            state = CallState.RINGING,
            liveKitToken = sampleToken,
            liveKitServerUrl = serverUrl
        )

        _activeCall.value = session

        // Send invite over WebSocket
        webSocketClient.sendEvent(
            LokLokWebSocketClient.EVENT_INCOMING_CALL,
            mapOf(
                "callId" to callId,
                "callerId" to "my_user_id",
                "callerName" to "Me",
                "mediaType" to mediaType.name,
                "liveKitToken" to sampleToken,
                "liveKitServerUrl" to serverUrl
            )
        )

        // Connect local LiveKit engine
        liveKitCallManager.joinCall(
            serverUrl = serverUrl,
            token = sampleToken,
            enableVideo = (mediaType == CallMediaType.VIDEO)
        )

        return Result.success(session)
    }

    override suspend fun acceptCall(): Result<Unit> {
        val current = _activeCall.value ?: return Result.failure(IllegalStateException("No active call"))
        _activeCall.value = current.copy(
            state = CallState.CONNECTING,
            connectedTimestamp = System.currentTimeMillis()
        )

        val serverUrl = current.liveKitServerUrl ?: "wss://livekit.loklok.im"
        val token = current.liveKitToken ?: ("jwt_livekit_" + UUID.randomUUID().toString())

        liveKitCallManager.joinCall(
            serverUrl = serverUrl,
            token = token,
            enableVideo = (current.mediaType == CallMediaType.VIDEO)
        )

        webSocketClient.sendEvent(
            LokLokWebSocketClient.EVENT_CALL_ACCEPTED,
            mapOf("callId" to current.callId)
        )

        _activeCall.value = current.copy(state = CallState.CONNECTED)
        return Result.success(Unit)
    }

    override suspend fun declineCall(): Result<Unit> {
        val current = _activeCall.value ?: return Result.success(Unit)
        webSocketClient.sendEvent(
            LokLokWebSocketClient.EVENT_CALL_REJECTED,
            mapOf("callId" to current.callId)
        )
        endCallInternal(CallState.DECLINED)
        return Result.success(Unit)
    }

    override suspend fun endCall(): Result<Unit> {
        val current = _activeCall.value ?: return Result.success(Unit)
        webSocketClient.sendEvent(
            LokLokWebSocketClient.EVENT_CALL_ENDED,
            mapOf("callId" to current.callId)
        )
        endCallInternal(CallState.ENDED)
        return Result.success(Unit)
    }

    private fun endCallInternal(finalState: CallState) {
        val current = _activeCall.value ?: return
        liveKitCallManager.disconnect()

        val duration = if (current.connectedTimestamp != null) {
            (System.currentTimeMillis() - current.connectedTimestamp) / 1000
        } else 0L

        scope.launch {
            callDao.insertCallRecord(
                CallHistoryEntity(
                    callId = current.callId,
                    remoteUserId = current.remoteUserId,
                    remoteUserName = current.remoteUserName,
                    remoteUserAvatar = current.remoteUserAvatar,
                    mediaType = current.mediaType,
                    direction = current.direction,
                    state = finalState,
                    timestamp = current.startTimestamp,
                    durationSeconds = duration
                )
            )
        }

        _activeCall.value = current.copy(
            state = finalState,
            endTimestamp = System.currentTimeMillis(),
            durationSeconds = duration
        )
        _activeCall.value = null
    }

    override suspend fun toggleMute(muted: Boolean) {
        liveKitCallManager.setMicrophoneMuted(muted)
        _activeCall.value = _activeCall.value?.copy(isMicMuted = muted)
    }

    override suspend fun toggleCamera(enabled: Boolean) {
        liveKitCallManager.enableCamera(enabled)
        _activeCall.value = _activeCall.value?.copy(isCameraEnabled = enabled)
    }

    override fun flipCamera() {
        liveKitCallManager.flipCamera()
    }

    override fun toggleSpeaker(speakerOn: Boolean) {
        liveKitCallManager.toggleSpeaker(speakerOn)
        _activeCall.value = _activeCall.value?.copy(isSpeakerOn = speakerOn)
    }
}
