package com.loklok.app.core.network

import com.google.gson.Gson
import com.loklok.app.core.security.SecureStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class SocketPayload(
    val event: String,
    val data: Map<String, Any?>
)

@Singleton
class LokLokWebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val secureStorage: SecureStorage,
    private val gson: Gson
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _incomingEvents = MutableSharedFlow<SocketPayload>(extraBufferCapacity = 64)
    val incomingEvents: SharedFlow<SocketPayload> = _incomingEvents.asSharedFlow()

    private val _connectionState = MutableSharedFlow<Boolean>(replay = 1)
    val connectionState: SharedFlow<Boolean> = _connectionState.asSharedFlow()

    fun connect(wsUrl: String = DEFAULT_WS_URL) {
        val token = secureStorage.getAuthToken() ?: return
        val request = Request.Builder()
            .url("$wsUrl?token=$token&userId=${secureStorage.getUserId()}")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                scope.launch { _connectionState.emit(true) }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val payload = gson.fromJson(text, SocketPayload::class.java)
                    scope.launch { _incomingEvents.emit(payload) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                scope.launch { _connectionState.emit(false) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                scope.launch { _connectionState.emit(false) }
            }
        })
    }

    fun sendEvent(event: String, data: Map<String, Any?>) {
        val payload = SocketPayload(event = event, data = data)
        val json = gson.toJson(payload)
        webSocket?.send(json)
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }

    companion object {
        const val DEFAULT_WS_URL = "wss://api.loklok.im/ws"
        const val EVENT_NEW_MESSAGE = "new_message"
        const val EVENT_MESSAGE_STATUS = "message_status"
        const val EVENT_TYPING = "typing"
        const val EVENT_INCOMING_CALL = "incoming_call"
        const val EVENT_CALL_ACCEPTED = "call_accepted"
        const val EVENT_CALL_REJECTED = "call_rejected"
        const val EVENT_CALL_ENDED = "call_ended"
    }
}
