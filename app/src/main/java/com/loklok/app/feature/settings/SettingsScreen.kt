package com.loklok.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokAvatar
import com.loklok.app.core.designsystem.theme.LokLokPrimary

@Composable
fun SettingsScreen(
    userName: String = "Lok Lok User",
    phoneNumber: String = "+971 50 123 4567",
    onLogoutClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // User Profile Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LokLokAvatar(name = userName, size = 62.dp)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = phoneNumber,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = { /* Show QR */ }) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "My QR Code",
                        tint = LokLokPrimary
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Settings items
        item {
            SettingsCategoryTitle(title = "General & Security")
            SettingsRow(icon = Icons.Default.Lock, title = "Account & Privacy", subtitle = "Security keys, two-step verification")
            SettingsRow(icon = Icons.Default.Devices, title = "Linked Devices", subtitle = "Lok Lok Web & Desktop")
            SettingsRow(icon = Icons.Default.Notifications, title = "Notifications & Sounds", subtitle = "VoIP push alerts, ringtones")
            SettingsRow(icon = Icons.Default.Storage, title = "Data & Storage", subtitle = "Network usage, media auto-download")

            Spacer(modifier = Modifier.height(16.dp))
            SettingsCategoryTitle(title = "More")
            SettingsRow(icon = Icons.Default.PersonAdd, title = "Invite a Friend", subtitle = "Share Lok Lok via SMS or link")
            SettingsRow(icon = Icons.Default.HelpOutline, title = "Help & Support", subtitle = "FAQ, contact us, privacy policy")

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLogoutClick)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log Out",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SettingsCategoryTitle(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = LokLokPrimary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }
}
