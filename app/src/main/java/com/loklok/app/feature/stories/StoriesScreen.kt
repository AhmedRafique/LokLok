package com.loklok.app.feature.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokAvatar
import com.loklok.app.core.designsystem.theme.LokLokPrimary
import com.loklok.app.domain.model.UserStory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StoriesScreen(
    viewModel: StoriesViewModel,
    onStoryClick: (UserStory) -> Unit
) {
    val stories by viewModel.stories.collectAsState()
    val myStory = stories.firstOrNull { it.isMyStory }
    val friendStories = stories.filter { !it.isMyStory }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = { /* Add text status */ },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Text Status", modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.height(14.dp))
                FloatingActionButton(
                    onClick = { /* Add media status */ },
                    containerColor = LokLokPrimary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera Status", modifier = Modifier.size(24.dp))
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // My Status Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { myStory?.let(onStoryClick) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        LokLokAvatar(name = "Me", size = 54.dp)
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(LokLokPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Status",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "My Status",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tap to add status update",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Recent Updates",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Friends Stories List
            items(
                items = friendStories,
                key = { it.userId }
            ) { story ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStoryClick(story) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LokLokAvatar(
                        name = story.userName,
                        imageUrl = story.userAvatar,
                        size = 54.dp,
                        hasStoryRing = story.hasUnseen
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = story.userName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault())
                            .format(Date(story.lastUpdatedTimestamp))
                        Text(
                            text = "Today, $formattedTime",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
