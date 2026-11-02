package com.loklok.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.loklok.app.core.designsystem.theme.LokLokTheme
import com.loklok.app.core.navigation.LokLokNavGraph
import com.loklok.app.core.network.LokLokWebSocketClient
import com.loklok.app.core.security.SecureStorage
import com.loklok.app.domain.repository.ChatRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var secureStorage: SecureStorage

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var webSocketClient: LokLokWebSocketClient

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* Permissions granted */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAppPermissions()

        if (secureStorage.isLoggedIn()) {
            webSocketClient.connect()
        }

        setContent {
            LokLokTheme {
                val navController = rememberNavController()
                LokLokNavGraph(
                    navController = navController,
                    secureStorage = secureStorage,
                    chatRepository = chatRepository
                )
            }
        }
    }

    private fun requestAppPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val ungranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isNotEmpty()) {
            permissionLauncher.launch(ungranted.toTypedArray())
        }
    }

    override fun onDestroy() {
        webSocketClient.disconnect()
        super.onDestroy()
    }
}
