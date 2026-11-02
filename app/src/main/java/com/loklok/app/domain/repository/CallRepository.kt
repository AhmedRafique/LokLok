package com.loklok.app.domain.repository

import com.loklok.app.domain.model.CallHistory
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CallRepository {
    val activeCall: StateFlow<CallSession?>
    fun getCallHistory(): Flow<List<CallSession>>
    suspend fun startCall(remoteUserId: String, remoteUserName: String, mediaType: CallMediaType): Result<CallSession>
    suspend fun acceptCall(): Result<Unit>
    suspend fun declineCall(): Result<Unit>
    suspend fun endCall(): Result<Unit>
    suspend fun toggleMute(muted: Boolean)
    suspend fun toggleCamera(enabled: Boolean)
    fun flipCamera()
    fun toggleSpeaker(speakerOn: Boolean)
}
