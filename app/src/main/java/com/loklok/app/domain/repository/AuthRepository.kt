package com.loklok.app.domain.repository

import com.loklok.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    fun isLoggedIn(): Boolean
    suspend fun requestOtp(phoneNumber: String): Result<Boolean>
    suspend fun verifyOtp(phoneNumber: String, code: String): Result<User>
    suspend fun updateProfile(displayName: String, statusBio: String, avatarUrl: String?): Result<User>
    suspend fun logout()
}
