package dev.sandesh.kharcha

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.sandesh.kharcha.core.designsystem.theme.KharchaTheme
import dev.sandesh.kharcha.security.AppLockManager
import dev.sandesh.kharcha.navigation.KharchaNavHost
import javax.inject.Inject

/**
 * Single Activity — hosts the entire Compose UI.
 * Uses FragmentActivity (required for BiometricPrompt).
 * FLAG_SECURE is applied per-screen in sensitive composables.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var appLockManager: AppLockManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KharchaTheme {
                KharchaNavHost(
                    appLockManager = appLockManager
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Trigger biometric lock check on resume from background
        appLockManager.checkLockOnResume(this)
    }
}
