package com.loklok.app.domain.model

data class User(
    val id: String,
    val phoneNumber: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val statusBio: String = "Hey there! I am using Lok Lok.",
    val publicKey: String? = null,
    val isOnline: Boolean = false,
    val lastSeenTimestamp: Long = System.currentTimeMillis()
)
