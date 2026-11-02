package com.loklok.app.core.webrtc

import android.content.Context
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.livekit.android.LiveKit
import io.livekit.android.RoomOptions
import io.livekit.android.events.RoomEvent
import io.livekit.android.room.Room
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.participant.RemoteParticipant
import io.livekit.android.room.track.CameraPosition
import io.livekit.android.room.track.LocalVideoTrack
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class LiveKitRoomState(
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isMicMuted: Boolean = false,
    val isCameraEnabled: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val remoteVideoTrack: VideoTrack? = null,
    val localVideoTrack: LocalVideoTrack? = null,
    val connectionQuality: String = "EXCELLENT",
    val error: String? = null
)

@Singleton
class LiveKitCallManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    private var room: Room? = null
    private var localVideoTrack: LocalVideoTrack? = null

    private val _roomState = MutableStateFlow(LiveKitRoomState())
    val roomState: StateFlow<LiveKitRoomState> = _roomState.asStateFlow()

    fun initializeRoom(): Room {
        val activeRoom = room ?: LiveKit.create(
            appContext = context,
            options = RoomOptions(
                adaptiveStream = true,
                dynacast = true
            )
        ).also { room = it }

        scope.launch {
            activeRoom.events.events.collect { event: RoomEvent ->
                when (event) {
                    is RoomEvent.Connected -> {
                        _roomState.value = _roomState.value.copy(
                            isConnected = true,
                            isConnecting = false,
                            error = null
                        )
                    }
                    is RoomEvent.Disconnected -> {
                        _roomState.value = _roomState.value.copy(
                            isConnected = false,
                            isConnecting = false,
                            remoteVideoTrack = null
                        )
                    }
                    is RoomEvent.TrackSubscribed -> {
                        if (event.track is VideoTrack) {
                            _roomState.value = _roomState.value.copy(
                                remoteVideoTrack = event.track as VideoTrack
                            )
                        }
                    }
                    is RoomEvent.TrackUnsubscribed -> {
                        if (event.track is VideoTrack) {
                            _roomState.value = _roomState.value.copy(
                                remoteVideoTrack = null
                            )
                        }
                    }
                    is RoomEvent.FailedToConnect -> {
                        _roomState.value = _roomState.value.copy(
                            isConnected = false,
                            isConnecting = false,
                            error = event.error.message
                        )
                    }
                    else -> Unit
                }
            }
        }

        return activeRoom
    }

    suspend fun joinCall(
        serverUrl: String,
        token: String,
        enableVideo: Boolean
    ) {
        val currentRoom = initializeRoom()
        _roomState.value = _roomState.value.copy(isConnecting = true, error = null)

        try {
            currentRoom.connect(serverUrl, token)

            // Enable microphone by default
            currentRoom.localParticipant.setMicrophoneEnabled(true)
            _roomState.value = _roomState.value.copy(isMicMuted = false)

            if (enableVideo) {
                enableCamera(true)
            }
        } catch (e: Exception) {
            _roomState.value = _roomState.value.copy(
                isConnecting = false,
                isConnected = false,
                error = e.localizedMessage
            )
        }
    }

    suspend fun setMicrophoneMuted(muted: Boolean) {
        room?.localParticipant?.setMicrophoneEnabled(!muted)
        _roomState.value = _roomState.value.copy(isMicMuted = muted)
    }

    suspend fun enableCamera(enabled: Boolean) {
        val localParticipant = room?.localParticipant ?: return
        if (enabled) {
            localVideoTrack = localParticipant.createVideoTrack()
            localParticipant.publishVideoTrack(localVideoTrack!!)
            _roomState.value = _roomState.value.copy(
                isCameraEnabled = true,
                localVideoTrack = localVideoTrack
            )
        } else {
            localVideoTrack?.let { track ->
                localParticipant.unpublishTrack(track)
                track.stop()
            }
            localVideoTrack = null
            _roomState.value = _roomState.value.copy(
                isCameraEnabled = false,
                localVideoTrack = null
            )
        }
    }

    fun flipCamera() {
        localVideoTrack?.let { track ->
            val currentPos = track.options.position ?: CameraPosition.FRONT
            val nextPos = if (currentPos == CameraPosition.FRONT) CameraPosition.BACK else CameraPosition.FRONT
            track.switchCamera(position = nextPos)
        }
    }

    fun toggleSpeaker(speakerOn: Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        @Suppress("DEPRECATION")
        audioManager?.isSpeakerphoneOn = speakerOn
        _roomState.value = _roomState.value.copy(isSpeakerOn = speakerOn)
    }

    fun initVideoRenderer(renderer: io.livekit.android.renderer.TextureViewRenderer) {
        val activeRoom = room ?: initializeRoom()
        activeRoom.initVideoRenderer(renderer)
    }

    fun disconnect() {
        scope.launch {
            localVideoTrack?.stop()
            localVideoTrack = null
            room?.disconnect()
            room?.release()
            room = null
            _roomState.value = LiveKitRoomState()
        }
    }
}
