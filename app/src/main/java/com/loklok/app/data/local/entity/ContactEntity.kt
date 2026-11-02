package com.loklok.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val phoneNumber: String,
    val isLokLokUser: Boolean,
    val avatarUrl: String? = null,
    val statusBio: String? = null,
    val registeredUserId: String? = null
)
