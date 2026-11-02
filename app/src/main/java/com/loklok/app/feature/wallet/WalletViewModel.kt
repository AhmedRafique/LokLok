package com.loklok.app.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loklok.app.domain.model.TransactionStatus
import com.loklok.app.domain.model.TransactionType
import com.loklok.app.domain.model.WalletAccount
import com.loklok.app.domain.model.WalletTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WalletUiState(
    val account: WalletAccount = WalletAccount(),
    val recentTransactions: List<WalletTransaction> = emptyList(),
    val isTransferring: Boolean = false,
    val successMessage: String? = null
)

@HiltViewModel
class WalletViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        val sample = listOf(
            WalletTransaction(
                id = "tx_1",
                type = TransactionType.P2P_RECEIVE,
                title = "From Ahmed Al-Mansoori",
                subtitle = "Dinner split",
                amount = 45.00,
                currency = "AED",
                status = TransactionStatus.COMPLETED,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 3
            ),
            WalletTransaction(
                id = "tx_2",
                type = TransactionType.INTERNATIONAL_REMITTANCE,
                title = "International Transfer",
                subtitle = "Bank Account •••• 8912",
                amount = 250.00,
                currency = "AED",
                status = TransactionStatus.COMPLETED,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48
            ),
            WalletTransaction(
                id = "tx_3",
                type = TransactionType.BILL_PAYMENT,
                title = "Du Telecom Bill",
                subtitle = "Account 0501234567",
                amount = 120.00,
                currency = "AED",
                status = TransactionStatus.COMPLETED,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 96
            )
        )
        _uiState.value = _uiState.value.copy(
            account = WalletAccount(balance = 3450.50, currency = "AED"),
            recentTransactions = sample
        )
    }

    fun sendP2pMoney(recipientPhone: String, amount: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTransferring = true)
            val newTx = WalletTransaction(
                id = "tx_" + System.currentTimeMillis(),
                type = TransactionType.P2P_SEND,
                title = "Transfer to $recipientPhone",
                subtitle = "Direct Lok Lok Transfer",
                amount = amount,
                currency = _uiState.value.account.currency,
                status = TransactionStatus.COMPLETED
            )
            val updatedBalance = _uiState.value.account.balance - amount
            _uiState.value = _uiState.value.copy(
                isTransferring = false,
                account = _uiState.value.account.copy(balance = updatedBalance),
                recentTransactions = listOf(newTx) + _uiState.value.recentTransactions,
                successMessage = "Successfully transferred ${amount} ${_uiState.value.account.currency} to $recipientPhone"
            )
        }
    }
}
