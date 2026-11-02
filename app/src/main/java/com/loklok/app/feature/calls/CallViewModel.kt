package com.loklok.app.feature.calls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loklok.app.core.webrtc.LiveKitCallManager
import com.loklok.app.domain.model.CallDirection
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallSession
import com.loklok.app.domain.model.CallState
import com.loklok.app.domain.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveCallUiState(
    val session: CallSession? = null,
    val callDurationText: String = "00:00",
    val isMicMuted: Boolean = false,
    val isCameraEnabled: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val networkQualityText: String = "HD Audio (Encrypted)"
)

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callRepository: CallRepository,
    val liveKitCallManager: LiveKitCallManager
) : ViewModel() {

    val activeSession: StateFlow<CallSession?> = callRepository.activeCall

    private val _uiState = MutableStateFlow(ActiveCallUiState())
    val uiState: StateFlow<ActiveCallUiState> = _uiState.asStateFlow()

    init {
        trackCallDuration()
    }

    private fun trackCallDuration() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val session = activeSession.value
                if (session?.state == CallState.CONNECTED && session.connectedTimestamp != null) {
                    val seconds = (System.currentTimeMillis() - session.connectedTimestamp) / 1000
                    val minutes = seconds / 60
                    val remainingSeconds = seconds % 60
                    val formatted = String.format("%02d:%02d", minutes, remainingSeconds)
                    _uiState.value = _uiState.value.copy(callDurationText = formatted)
                }
            }
        }
    }

    fun acceptCall() {
        viewModelScope.launch {
            callRepository.acceptCall()
        }
    }

    fun declineCall() {
        viewModelScope.launch {
            callRepository.declineCall()
        }
    }

    fun endCall() {
        viewModelScope.launch {
            callRepository.endCall()
        }
    }

    fun toggleMute() {
        viewModelScope.launch {
            val next = !_uiState.value.isMicMuted
            callRepository.toggleMute(next)
            _uiState.value = _uiState.value.copy(isMicMuted = next)
        }
    }

    fun toggleCamera() {
        viewModelScope.launch {
            val next = !_uiState.value.isCameraEnabled
            callRepository.toggleCamera(next)
            _uiState.value = _uiState.value.copy(isCameraEnabled = next)
        }
    }

    fun flipCamera() {
        callRepository.flipCamera()
    }

    fun toggleSpeaker() {
        val next = !_uiState.value.isSpeakerOn
        callRepository.toggleSpeaker(next)
        _uiState.value = _uiState.value.copy(isSpeakerOn = next)
    }
}
