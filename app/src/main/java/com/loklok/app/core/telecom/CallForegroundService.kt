package com.loklok.app.core.telecom

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.loklok.app.LokLokApplication
import com.loklok.app.MainActivity
import com.loklok.app.R
import com.loklok.app.feature.calls.CallActivity

class CallForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val callerName = intent?.getStringExtra(EXTRA_CALLER_NAME) ?: "Lok Lok Call"
        val isVideo = intent?.getBooleanExtra(EXTRA_IS_VIDEO, false) ?: false

        val fullScreenIntent = Intent(this, CallActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, LokLokApplication.CHANNEL_CALLS)
            .setContentTitle(callerName)
            .setContentText(if (isVideo) "Ongoing Video Call" else "Ongoing Voice Call")
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val foregroundServiceType = if (isVideo) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            }
            startForeground(NOTIFICATION_ID, notification, foregroundServiceType)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 2001
        const val EXTRA_CALLER_NAME = "extra_caller_name"
        const val EXTRA_IS_VIDEO = "extra_is_video"
    }
}
