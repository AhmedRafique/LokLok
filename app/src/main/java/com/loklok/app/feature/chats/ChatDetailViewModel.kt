package com.loklok.app.feature.chats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.ChatMessage
import com.loklok.app.domain.model.MessageType
import com.loklok.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatDetailUiState(
    val conversationTitle: String = "Chat",
    val isOnline: Boolean = true,
    val inputText: String = "",
    val isSending: Boolean = false,
    val isRecordingVoice: Boolean = false
)

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val conversationId: String = checkNotNull(savedStateHandle["conversationId"])

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    val messages: StateFlow<List<ChatMessage>> = chatRepository.getMessages(conversationId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        markAsRead()
    }

    private fun markAsRead() {
        viewModelScope.launch {
            chatRepository.markAsRead(conversationId)
        }
    }

    fun onInputTextChange(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) return

        _uiState.value = _uiState.value.copy(inputText = "", isSending = true)

        viewModelScope.launch {
            chatRepository.sendMessage(
                conversationId = conversationId,
                content = text,
                type = MessageType.TEXT
            )
            _uiState.value = _uiState.value.copy(isSending = false)
        }
    }

    fun sendVoiceNote(durationMs: Long) {
        viewModelScope.launch {
            chatRepository.sendMessage(
                conversationId = conversationId,
                content = "Voice message (${durationMs / 1000}s)",
                type = MessageType.VOICE_NOTE,
                mediaUrl = "https://loklok.media/sample_voice.opus"
            )
        }
    }
}
