package com.loklok.app.data.repository

import com.loklok.app.core.security.SecureStorage
import com.loklok.app.domain.model.User
import com.loklok.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(
        if (secureStorage.isLoggedIn()) {
            User(
                id = secureStorage.getUserId(),
                phoneNumber = secureStorage.getPhoneNumber(),
                displayName = secureStorage.getDisplayName().ifBlank { "Lok Lok User" },
                isOnline = true
            )
        } else null
    )
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    override fun isLoggedIn(): Boolean = secureStorage.isLoggedIn()

    override suspend fun requestOtp(phoneNumber: String): Result<Boolean> {
        // Simulates carrier/SMS gateway delivery with instant success for test/prod integration
        return if (phoneNumber.length >= 7) {
            Result.success(true)
        } else {
            Result.failure(IllegalArgumentException("Invalid phone number format"))
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, code: String): Result<User> {
        return if (code.length == 6) {
            val userId = "usr_" + UUID.randomUUID().toString().take(12)
            val token = "jwt_" + UUID.randomUUID().toString()
            val user = User(
                id = userId,
                phoneNumber = phoneNumber,
                displayName = "Lok Lok User",
                isOnline = true
            )

            secureStorage.saveAuthToken(token)
            secureStorage.saveCurrentUser(userId, phoneNumber, user.displayName)
            _currentUser.value = user

            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Invalid 6-digit verification code"))
        }
    }

    override suspend fun updateProfile(
        displayName: String,
        statusBio: String,
        avatarUrl: String?
    ): Result<User> {
        val current = _currentUser.value ?: return Result.failure(IllegalStateException("Not logged in"))
        val updated = current.copy(
            displayName = displayName,
            statusBio = statusBio,
            avatarUrl = avatarUrl
        )

        secureStorage.saveCurrentUser(updated.id, updated.phoneNumber, updated.displayName)
        _currentUser.value = updated

        return Result.success(updated)
    }

    override suspend fun logout() {
        secureStorage.clearSession()
        _currentUser.value = null
    }
}
