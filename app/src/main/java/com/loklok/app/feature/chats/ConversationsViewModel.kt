package com.loklok.app.feature.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loklok.app.domain.model.Conversation
import com.loklok.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ConversationsUiState(
    val conversations: List<Conversation> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ConversationsUiState> = combine(
        chatRepository.getConversations(),
        _searchQuery
    ) { conversations, query ->
        val filtered = if (query.isBlank()) {
            conversations
        } else {
            conversations.filter { it.title.contains(query, ignoreCase = true) }
        }
        ConversationsUiState(
            conversations = filtered,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConversationsUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
