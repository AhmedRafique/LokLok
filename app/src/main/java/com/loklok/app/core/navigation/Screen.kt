package com.loklok.app.core.navigation

sealed class Screen(val route: String) {
    // Auth Flow
    data object Welcome : Screen("welcome")
    data object PhoneAuth : Screen("phone_auth")
    data object OtpVerify : Screen("otp_verify/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "otp_verify/$phoneNumber"
    }
    data object ProfileSetup : Screen("profile_setup")

    // Main App Container & Tabs
    data object MainContainer : Screen("main_container")
    data object ChatsList : Screen("tab_chats")
    data object CallsHistory : Screen("tab_calls")
    data object StoriesFeed : Screen("tab_stories")
    data object LokLokPay : Screen("tab_pay")
    data object Settings : Screen("tab_settings")

    // Detail & Action Screens
    data object ChatDetail : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }
    data object ActiveCall : Screen("active_call/{callId}") {
        fun createRoute(callId: String) = "active_call/$callId"
    }
    data object NewChatOrCall : Screen("new_chat_or_call")
    data object TransferMoney : Screen("transfer_money")
}
