package dev.sandesh.kharcha.security

import android.content.Context
import android.view.WindowManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class AppLockState { LOCKED, UNLOCKED, NOT_ENROLLED }

/**
 * Manages biometric/PIN app lock on cold start and resume from background.
 *
 * Per SECURITY.md §4: "Biometric or PIN lock required on cold start AND on resume from background
 * (use BiometricPrompt API, fall back to device PIN)"
 */
@Singleton
class AppLockManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _lockState = MutableStateFlow(AppLockState.LOCKED)
    val lockState: StateFlow<AppLockState> = _lockState.asStateFlow()

    private var lastBackgroundTime: Long = 0L
    private val backgroundLockTimeoutMs = 30_000L // 30 seconds in background triggers re-lock

    /**
     * Call from MainActivity.onCreate — shows biometric prompt if needed.
     */
    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit = {}
    ) {
        val biometricManager = BiometricManager.from(context)

        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt(activity, onSuccess, onFailure)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // No biometrics enrolled — allow access but warn in Settings
                _lockState.value = AppLockState.NOT_ENROLLED
                onSuccess()
            }
            else -> {
                // Hardware unavailable — allow access
                _lockState.value = AppLockState.UNLOCKED
                onSuccess()
            }
        }
    }

    /**
     * Called from MainActivity.onResume — re-locks if app was backgrounded long enough.
     */
    fun checkLockOnResume(activity: FragmentActivity) {
        val timeSinceBackground = System.currentTimeMillis() - lastBackgroundTime
        if (lastBackgroundTime > 0 && timeSinceBackground > backgroundLockTimeoutMs) {
            _lockState.value = AppLockState.LOCKED
            authenticate(activity, onSuccess = {
                _lockState.value = AppLockState.UNLOCKED
            })
        }
    }

    fun onAppBackgrounded() {
        lastBackgroundTime = System.currentTimeMillis()
    }

    fun onAppUnlocked() {
        _lockState.value = AppLockState.UNLOCKED
        lastBackgroundTime = 0L
    }

    private fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                _lockState.value = AppLockState.UNLOCKED
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                // User can retry — don't dismiss prompt on single failure
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    // User cancelled — lock the app
                    onFailure("Authentication cancelled")
                } else {
                    onFailure(errString.toString())
                }
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Kharcha")
            .setSubtitle("Verify your identity to access your financial data")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        prompt.authenticate(promptInfo)
    }

    /**
     * Apply FLAG_SECURE to a window — blocks screenshots and screen recordings.
     * Per SECURITY.md §4: applied on sensitive screens (transactions, accounts).
     */
    fun applyFlagSecure(activity: FragmentActivity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    /**
     * Clear FLAG_SECURE when on non-sensitive screens.
     */
    fun clearFlagSecure(activity: FragmentActivity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}
