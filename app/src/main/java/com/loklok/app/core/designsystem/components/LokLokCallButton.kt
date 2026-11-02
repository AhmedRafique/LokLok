package com.loklok.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LokLokCallButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White.copy(alpha = 0.2f),
    iconTint: Color = Color.White,
    size: Dp = 64.dp,
    isActive: Boolean = false,
    activeBackgroundColor: Color = Color.White,
    activeIconTint: Color = Color.Black
) {
    val currentBg = if (isActive) activeBackgroundColor else backgroundColor
    val currentTint = if (isActive) activeIconTint else iconTint

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(currentBg)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = currentTint,
                modifier = Modifier.size(size * 0.45f)
            )
        }
        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
            )
        }
    }
}
