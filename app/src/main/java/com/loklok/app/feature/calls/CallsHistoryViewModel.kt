package com.loklok.app.feature.calls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallSession
import com.loklok.app.domain.repository.CallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallsHistoryViewModel @Inject constructor(
    private val callRepository: CallRepository
) : ViewModel() {

    val callHistory: StateFlow<List<CallSession>> = callRepository.getCallHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun startCall(remoteUserId: String, remoteUserName: String, mediaType: CallMediaType) {
        viewModelScope.launch {
            callRepository.startCall(remoteUserId, remoteUserName, mediaType)
        }
    }
}
