package com.loklok.app.core.telecom

import android.net.Uri
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import com.loklok.app.domain.model.CallDirection
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallSession
import com.loklok.app.domain.repository.CallRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LokLokConnectionService : ConnectionService() {

    @Inject
    lateinit var callRepository: CallRepository

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest
    ): Connection {
        val callerNumber = request.address?.schemeSpecificPart ?: "Unknown"
        val connection = LokLokTelecomConnection(callerNumber, CallDirection.INCOMING)
        connection.setRinging()
        return connection
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest
    ): Connection {
        val calleeNumber = request.address?.schemeSpecificPart ?: "Unknown"
        val connection = LokLokTelecomConnection(calleeNumber, CallDirection.OUTGOING)
        connection.setDialing()
        return connection
    }

    inner class LokLokTelecomConnection(
        private val number: String,
        private val direction: CallDirection
    ) : Connection() {

        init {
            connectionProperties = PROPERTY_SELF_MANAGED
            connectionCapabilities = CAPABILITY_HOLD or CAPABILITY_SUPPORT_HOLD or CAPABILITY_MUTE
            setAddress(Uri.fromParts("tel", number, null), TelecomManager.PRESENTATION_ALLOWED)
        }

        override fun onAnswer(videoState: Int) {
            setActive()
            scope.launch {
                callRepository.acceptCall()
            }
        }

        override fun onReject() {
            setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
            destroy()
            scope.launch {
                callRepository.endCall()
            }
        }

        override fun onDisconnect() {
            setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
            destroy()
            scope.launch {
                callRepository.endCall()
            }
        }

        override fun onSilence() {
            // Mute ringtone when user hits volume rocker
        }
    }
}
