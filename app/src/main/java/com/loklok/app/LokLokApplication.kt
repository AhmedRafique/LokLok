package com.loklok.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LokLokApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // VoIP Calls Channel (Maximum Priority + Custom Ringtone & Full Screen Intent)
            val callChannel = NotificationChannel(
                CHANNEL_CALLS,
                "Lok Lok Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Incoming voice and video calls notifications"
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .build()
                )
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 1000, 1000, 1000)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }

            // Chats & Messaging Channel
            val chatChannel = NotificationChannel(
                CHANNEL_MESSAGES,
                "Lok Lok Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Direct and group chat notifications"
                enableVibration(true)
            }

            // System & Wallet Channel
            val systemChannel = NotificationChannel(
                CHANNEL_SYSTEM,
                "System & Transactions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Account updates and Lok Lok Pay notifications"
            }

            notificationManager.createNotificationChannels(
                listOf(callChannel, chatChannel, systemChannel)
            )
        }
    }

    companion object {
        const val CHANNEL_CALLS = "loklok_calls_channel"
        const val CHANNEL_MESSAGES = "loklok_messages_channel"
        const val CHANNEL_SYSTEM = "loklok_system_channel"
    }
}
