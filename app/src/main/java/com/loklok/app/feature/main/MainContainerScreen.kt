package com.loklok.app.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loklok.app.core.designsystem.components.LokLokTopBar
import com.loklok.app.core.designsystem.theme.LokLokPrimary
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.feature.calls.CallsHistoryScreen
import com.loklok.app.feature.calls.CallsHistoryViewModel
import com.loklok.app.feature.chats.ConversationsListScreen
import com.loklok.app.feature.chats.ConversationsViewModel
import com.loklok.app.feature.settings.SettingsScreen
import com.loklok.app.feature.stories.StoriesScreen
import com.loklok.app.feature.stories.StoriesViewModel
import com.loklok.app.feature.wallet.LokLokPayScreen
import com.loklok.app.feature.wallet.WalletViewModel

enum class NavigationTab(val label: String, val icon: ImageVector) {
    CHATS("Chats", Icons.Default.Chat),
    CALLS("Calls", Icons.Default.Call),
    STORIES("Stories", Icons.Default.DonutLarge),
    PAY("Pay", Icons.Default.AccountBalanceWallet),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun MainContainerScreen(
    conversationsViewModel: ConversationsViewModel,
    callsViewModel: CallsHistoryViewModel,
    storiesViewModel: StoriesViewModel,
    walletViewModel: WalletViewModel,
    onNavigateToChat: (String) -> Unit,
    onStartCall: (String, String, CallMediaType) -> Unit,
    onNewChatOrCall: () -> Unit,
    onSendMoney: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.CHATS) }

    Scaffold(
        topBar = {
            LokLokTopBar(
                title = when (selectedTab) {
                    NavigationTab.CHATS -> "Lok Lok"
                    NavigationTab.CALLS -> "Calls"
                    NavigationTab.STORIES -> "Stories"
                    NavigationTab.PAY -> "Lok Lok Pay"
                    NavigationTab.SETTINGS -> "Settings"
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            if (tab == NavigationTab.CHATS) {
                                BadgedBox(badge = {
                                    Badge(containerColor = LokLokPrimary) { Text("4") }
                                }) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LokLokPrimary,
                            selectedTextColor = LokLokPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                NavigationTab.CHATS -> ConversationsListScreen(
                    viewModel = conversationsViewModel,
                    onConversationClick = onNavigateToChat,
                    onNewChatClick = onNewChatOrCall
                )
                NavigationTab.CALLS -> CallsHistoryScreen(
                    viewModel = callsViewModel,
                    onStartCall = onStartCall,
                    onNewCallClick = onNewChatOrCall
                )
                NavigationTab.STORIES -> StoriesScreen(
                    viewModel = storiesViewModel,
                    onStoryClick = { /* View story */ }
                )
                NavigationTab.PAY -> LokLokPayScreen(
                    viewModel = walletViewModel,
                    onSendMoneyClick = onSendMoney,
                    onScanQrClick = { /* Open camera QR */ }
                )
                NavigationTab.SETTINGS -> SettingsScreen(
                    onLogoutClick = onLogout
                )
            }
        }
    }
}
