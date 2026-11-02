package com.loklok.app.domain.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val isLokLokUser: Boolean = false,
    val avatarUrl: String? = null,
    val statusBio: String? = null,
    val registeredUserId: String? = null
)
