package com.loklok.app.core.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.loklok.app.core.security.SecureStorage
import com.loklok.app.domain.model.CallMediaType
import com.loklok.app.domain.repository.ChatRepository
import com.loklok.app.feature.auth.OtpVerifyScreen
import com.loklok.app.feature.auth.PhoneAuthScreen
import com.loklok.app.feature.auth.PhoneAuthViewModel
import com.loklok.app.feature.auth.ProfileSetupScreen
import com.loklok.app.feature.auth.WelcomeScreen
import com.loklok.app.feature.calls.CallActivity
import com.loklok.app.feature.calls.CallsHistoryViewModel
import com.loklok.app.feature.chats.ChatDetailScreen
import com.loklok.app.feature.chats.ChatDetailViewModel
import com.loklok.app.feature.chats.ConversationsViewModel
import com.loklok.app.feature.contacts.ContactsViewModel
import com.loklok.app.feature.contacts.NewChatOrCallScreen
import com.loklok.app.feature.main.MainContainerScreen
import com.loklok.app.feature.stories.StoriesViewModel
import com.loklok.app.feature.wallet.WalletViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LokLokNavGraph(
    navController: NavHostController,
    secureStorage: SecureStorage,
    chatRepository: ChatRepository
) {
    val context = LocalContext.current
    val startDestination = if (secureStorage.isLoggedIn()) {
        Screen.MainContainer.route
    } else {
        Screen.Welcome.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Welcome Screen
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStartedClick = { navController.navigate(Screen.PhoneAuth.route) }
            )
        }

        // Phone Auth Screen
        composable(Screen.PhoneAuth.route) {
            val authViewModel: PhoneAuthViewModel = hiltViewModel()
            PhoneAuthScreen(
                viewModel = authViewModel,
                onNavigateToOtp = { phone ->
                    navController.navigate(Screen.OtpVerify.createRoute(phone))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // OTP Verify Screen
        composable(
            route = Screen.OtpVerify.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            val authViewModel: PhoneAuthViewModel = hiltViewModel()
            OtpVerifyScreen(
                phoneNumber = phone,
                viewModel = authViewModel,
                onVerificationSuccess = {
                    navController.navigate(Screen.ProfileSetup.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Profile Setup Screen
        composable(Screen.ProfileSetup.route) {
            val authViewModel: PhoneAuthViewModel = hiltViewModel()
            ProfileSetupScreen(
                viewModel = authViewModel,
                onProfileCompleted = {
                    navController.navigate(Screen.MainContainer.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        // Main App Container (Chats, Calls, Stories, Pay, Settings)
        composable(Screen.MainContainer.route) {
            val conversationsViewModel: ConversationsViewModel = hiltViewModel()
            val callsViewModel: CallsHistoryViewModel = hiltViewModel()
            val storiesViewModel: StoriesViewModel = hiltViewModel()
            val walletViewModel: WalletViewModel = hiltViewModel()

            MainContainerScreen(
                conversationsViewModel = conversationsViewModel,
                callsViewModel = callsViewModel,
                storiesViewModel = storiesViewModel,
                walletViewModel = walletViewModel,
                onNavigateToChat = { convId ->
                    navController.navigate(Screen.ChatDetail.createRoute(convId))
                },
                onStartCall = { targetUserId, targetUserName, mediaType ->
                    callsViewModel.startCall(targetUserId, targetUserName, mediaType)
                    val callIntent = Intent(context, CallActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(callIntent)
                },
                onNewChatOrCall = {
                    navController.navigate(Screen.NewChatOrCall.route)
                },
                onSendMoney = {
                    // Quick P2P money send modal/action
                },
                onLogout = {
                    secureStorage.clearSession()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Chat Detail Screen
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) {
            val chatViewModel: ChatDetailViewModel = hiltViewModel()
            val callsViewModel: CallsHistoryViewModel = hiltViewModel()

            ChatDetailScreen(
                viewModel = chatViewModel,
                onVoiceCallClick = { targetUserId, targetUserName ->
                    callsViewModel.startCall(targetUserId, targetUserName, CallMediaType.VOICE)
                    val callIntent = Intent(context, CallActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(callIntent)
                },
                onVideoCallClick = { targetUserId, targetUserName ->
                    callsViewModel.startCall(targetUserId, targetUserName, CallMediaType.VIDEO)
                    val callIntent = Intent(context, CallActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(callIntent)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Select Contact / New Chat or Call Screen
        composable(Screen.NewChatOrCall.route) {
            val contactsViewModel: ContactsViewModel = hiltViewModel()
            val callsViewModel: CallsHistoryViewModel = hiltViewModel()

            NewChatOrCallScreen(
                viewModel = contactsViewModel,
                onContactSelectForChat = { contact ->
                    CoroutineScope(Dispatchers.Main).launch {
                        val conv = chatRepository.createOrGetDirectConversation(
                            contact.registeredUserId ?: contact.phoneNumber,
                            contact.name
                        )
                        navController.navigate(Screen.ChatDetail.createRoute(conv.id)) {
                            popUpTo(Screen.NewChatOrCall.route) { inclusive = true }
                        }
                    }
                },
                onStartCall = { targetUserId, targetUserName, mediaType ->
                    callsViewModel.startCall(targetUserId, targetUserName, mediaType)
                    val callIntent = Intent(context, CallActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(callIntent)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
