package com.loklok.app.domain.model

enum class TransactionType {
    P2P_SEND,
    P2P_RECEIVE,
    TOP_UP,
    WITHDRAWAL,
    BILL_PAYMENT,
    INTERNATIONAL_REMITTANCE
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REVERSED
}

data class WalletTransaction(
    val id: String,
    val type: TransactionType,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val currency: String = "USD",
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val timestamp: Long = System.currentTimeMillis(),
    val recipientPhoneNumber: String? = null,
    val referenceId: String? = null
)

data class WalletAccount(
    val balance: Double = 1250.00,
    val currency: String = "USD",
    val isKycVerified: Boolean = true,
    val dailyLimit: Double = 5000.00
)
