package com.loklok.app.feature.calls

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.loklok.app.core.designsystem.theme.LokLokTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallActivity : ComponentActivity() {

    private val callViewModel: CallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wakeAndUnlockDevice()

        setContent {
            LokLokTheme(darkTheme = true) {
                ActiveCallScreen(
                    viewModel = callViewModel,
                    onCallEnded = { finish() }
                )
            }
        }
    }

    private fun wakeAndUnlockDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
}
