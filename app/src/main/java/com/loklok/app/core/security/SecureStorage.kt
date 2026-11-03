package com.loklok.app.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "loklok_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveCurrentUser(userId: String, phoneNumber: String, displayName: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_PHONE_NUMBER, phoneNumber)
            .putString(KEY_DISPLAY_NAME, displayName)
            .apply()
    }

    fun getUserId(): String = prefs.getString(KEY_USER_ID, "") ?: ""
    fun getPhoneNumber(): String = prefs.getString(KEY_PHONE_NUMBER, "") ?: ""
    fun getDisplayName(): String = prefs.getString(KEY_DISPLAY_NAME, "") ?: ""

    fun getLiveKitServerUrl(): String = prefs.getString(KEY_LIVEKIT_URL, "wss://livekit.loklok.im") ?: "wss://livekit.loklok.im"
    fun setLiveKitServerUrl(url: String) {
        prefs.edit().putString(KEY_LIVEKIT_URL, url).apply()
    }

    fun getSignalingWsUrl(): String = prefs.getString(KEY_SIGNALING_URL, "wss://api.loklok.im/ws") ?: "wss://api.loklok.im/ws"
    fun setSignalingWsUrl(url: String) {
        prefs.edit().putString(KEY_SIGNALING_URL, url).apply()
    }

    fun isLoggedIn(): Boolean = !getAuthToken().isNullOrBlank()

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_LIVEKIT_URL = "livekit_server_url"
        private const val KEY_SIGNALING_URL = "signaling_ws_url"
    }
}
