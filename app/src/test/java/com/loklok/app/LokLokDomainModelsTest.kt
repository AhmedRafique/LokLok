package com.loklok.app

import com.loklok.app.domain.model.CallDirection
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallSession
import com.loklok.app.domain.model.CallState
import com.loklok.app.domain.model.ChatMessage
import com.loklok.app.domain.model.DeliveryStatus
import com.loklok.app.domain.model.MessageType
import com.loklok.app.domain.model.TransactionStatus
import com.loklok.app.domain.model.TransactionType
import com.loklok.app.domain.model.WalletAccount
import com.loklok.app.domain.model.WalletTransaction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LokLokDomainModelsTest {

    @Test
    fun testChatMessageCreation() {
        val message = ChatMessage(
            id = "msg_123",
            conversationId = "conv_abc",
            senderId = "usr_001",
            senderName = "Ahmed",
            content = "Testing encrypted message",
            type = MessageType.TEXT,
            status = DeliveryStatus.SENT,
            isEncrypted = true,
            isIncoming = false
        )

        assertEquals("msg_123", message.id)
        assertEquals("Testing encrypted message", message.content)
        assertTrue(message.isEncrypted)
        assertFalse(message.isIncoming)
        assertEquals(DeliveryStatus.SENT, message.status)
    }

    @Test
    fun testCallSessionTransitions() {
        val session = CallSession(
            callId = "call_456",
            remoteUserId = "usr_002",
            remoteUserName = "Fatima",
            mediaType = CallMediaType.VIDEO,
            direction = CallDirection.OUTGOING,
            state = CallState.RINGING
        )

        assertEquals(CallMediaType.VIDEO, session.mediaType)
        assertEquals(CallDirection.OUTGOING, session.direction)
        assertEquals(CallState.RINGING, session.state)

        val connected = session.copy(
            state = CallState.CONNECTED,
            connectedTimestamp = System.currentTimeMillis()
        )
        assertEquals(CallState.CONNECTED, connected.state)
        assertTrue(connected.connectedTimestamp != null)
    }

    @Test
    fun testWalletAccountAndTransactions() {
        val account = WalletAccount(balance = 1000.0, currency = "AED")
        val tx = WalletTransaction(
            id = "tx_01",
            type = TransactionType.P2P_SEND,
            title = "Sent to Omar",
            subtitle = "Direct Transfer",
            amount = 150.0,
            currency = "AED",
            status = TransactionStatus.COMPLETED
        )

        val remainingBalance = account.balance - tx.amount
        assertEquals(850.0, remainingBalance, 0.001)
        assertEquals(TransactionType.P2P_SEND, tx.type)
        assertEquals(TransactionStatus.COMPLETED, tx.status)
    }
}
