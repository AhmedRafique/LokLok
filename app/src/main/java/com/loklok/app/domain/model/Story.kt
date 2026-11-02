package com.loklok.app.domain.model

data class StoryItem(
    val id: String,
    val mediaUrl: String,
    val caption: String? = null,
    val isVideo: Boolean = false,
    val durationSeconds: Int = 5,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserStory(
    val userId: String,
    val userName: String,
    val userAvatar: String? = null,
    val isMyStory: Boolean = false,
    val items: List<StoryItem> = emptyList(),
    val hasUnseen: Boolean = true,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
