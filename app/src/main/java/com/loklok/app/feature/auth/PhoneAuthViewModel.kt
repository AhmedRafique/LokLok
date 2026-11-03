package com.loklok.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loklok.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val countryCode: String = "+971",
    val phoneNumber: String = "",
    val fullPhoneNumber: String = "",
    val otpCode: String = "",
    val displayName: String = "",
    val statusBio: String = "Hey there! I am using Lok Lok.",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOtpSent: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isProfileCompleted: Boolean = false
)

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onCountryCodeChange(code: String) {
        val clean = if (code.startsWith("+")) code else "+$code"
        _uiState.value = _uiState.value.copy(countryCode = clean, errorMessage = null)
    }

    fun onPhoneNumberChange(number: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = number, errorMessage = null)
    }

    fun onOtpChange(code: String) {
        _uiState.value = _uiState.value.copy(otpCode = code, errorMessage = null)
    }

    fun onDisplayNameChange(name: String) {
        _uiState.value = _uiState.value.copy(displayName = name)
    }

    fun onStatusBioChange(bio: String) {
        _uiState.value = _uiState.value.copy(statusBio = bio)
    }

    fun sendOtp() {
        val rawPhone = _uiState.value.phoneNumber.trim().replace(" ", "").replace("-", "")
        if (rawPhone.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid phone number")
            return
        }

        val fullPhone = "${_uiState.value.countryCode.trim()}$rawPhone"

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                fullPhoneNumber = fullPhone
            )
            val result = authRepository.requestOtp(fullPhone)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isOtpSent = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
            }
        }
    }

    fun autoFillTestOtp() {
        _uiState.value = _uiState.value.copy(otpCode = "123456", errorMessage = null)
        verifyOtp()
    }

    fun verifyOtp() {
        val phone = _uiState.value.fullPhoneNumber.ifBlank { _uiState.value.phoneNumber.trim() }
        val code = _uiState.value.otpCode.trim()
        if (code.length != 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter all 6 digits")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.verifyOtp(phone, code)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
            }
        }
    }

    fun completeProfile() {
        val name = _uiState.value.displayName.trim()
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your name")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.updateProfile(name, _uiState.value.statusBio, null)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isProfileCompleted = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
            }
        }
    }
}
