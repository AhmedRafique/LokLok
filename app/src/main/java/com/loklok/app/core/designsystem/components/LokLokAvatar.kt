package com.loklok.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.loklok.app.core.designsystem.theme.LokLokCallAccept
import com.loklok.app.core.designsystem.theme.LokLokPrimary

@Composable
fun LokLokAvatar(
    name: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    size: Dp = 50.dp,
    isOnline: Boolean = false,
    hasStoryRing: Boolean = false
) {
    val ringModifier = if (hasStoryRing) {
        modifier.border(2.5.dp, LokLokPrimary, CircleShape)
    } else {
        modifier
    }

    Box(
        modifier = ringModifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(if (hasStoryRing) size - 6.dp else size)
                    .clip(CircleShape)
            )
        } else {
            // Initials avatar
            val initials = name.split(" ")
                .filter { it.isNotEmpty() }
                .take(2)
                .map { it.first().uppercase() }
                .joinToString("")
                .ifEmpty { "?" }

            Box(
                modifier = Modifier
                    .size(if (hasStoryRing) size - 6.dp else size)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.4).sp
                )
            }
        }

        // Online status dot indicator
        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(LokLokCallAccept)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}
