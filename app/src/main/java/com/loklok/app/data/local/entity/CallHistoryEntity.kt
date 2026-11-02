package com.loklok.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.loklok.app.domain.model.CallDirection
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.model.CallState

@Entity(tableName = "call_history")
data class CallHistoryEntity(
    @PrimaryKey
    val callId: String,
    val remoteUserId: String,
    val remoteUserName: String,
    val remoteUserAvatar: String? = null,
    val mediaType: CallMediaType,
    val direction: CallDirection,
    val state: CallState,
    val timestamp: Long,
    val durationSeconds: Long
)
