package com.loklok.app.feature.stories

import androidx.lifecycle.ViewModel
import com.loklok.app.domain.model.StoryItem
import com.loklok.app.domain.model.UserStory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StoriesViewModel @Inject constructor() : ViewModel() {

    private val _stories = MutableStateFlow<List<UserStory>>(
        listOf(
            UserStory(
                userId = "my_user",
                userName = "My Status",
                isMyStory = true,
                items = emptyList(),
                hasUnseen = false
            ),
            UserStory(
                userId = "usr_ahmed",
                userName = "Ahmed Al-Mansoori",
                hasUnseen = true,
                lastUpdatedTimestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                items = listOf(
                    StoryItem(
                        id = "s1",
                        mediaUrl = "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=800",
                        caption = "Beautiful evening in Downtown Dubai ✨"
                    )
                )
            ),
            UserStory(
                userId = "usr_fatima",
                userName = "Fatima Zahra",
                hasUnseen = true,
                lastUpdatedTimestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                items = listOf(
                    StoryItem(
                        id = "s2",
                        mediaUrl = "https://images.unsplash.com/photo-1518684079-3c830dcef090?w=800",
                        caption = "Weekend coffee break ☕"
                    )
                )
            )
        )
    )
    val stories: StateFlow<List<UserStory>> = _stories.asStateFlow()
}
