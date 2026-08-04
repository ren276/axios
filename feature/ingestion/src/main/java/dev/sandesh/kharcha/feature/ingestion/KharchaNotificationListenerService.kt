package dev.sandesh.kharcha.feature.ingestion

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import dev.sandesh.kharcha.feature.ingestion.service.IngestionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Secondary ingestion path: catches UPI payment notifications from GPay, PhonePe, BHIM, etc.
 * Used when bank SMS is delayed or blocked.
 *
 * Requires "Notification Access" special permission in Settings → Apps → Special app access.
 * The onboarding flow walks the user through this separately from SMS permissions.
 *
 * Security: Only notification body/title text is extracted. No sensitive metadata is logged.
 */
@AndroidEntryPoint
class KharchaNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var ingestionService: IngestionService

    private val scope = CoroutineScope(Dispatchers.IO)

    // UPI app package names we care about
    private val upiAppPackages = setOf(
        "com.google.android.apps.nbu.paisa.user",  // GPay
        "com.phonepe.app",                          // PhonePe
        "net.one97.paytm",                          // Paytm
        "in.org.npci.upiapp",                       // BHIM
        "com.amazon.mShop.android.shopping",        // Amazon Pay
        "com.whatsapp",                             // WhatsApp Pay
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        // Only process notifications from known UPI apps
        if (sbn.packageName !in upiAppPackages) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        val title = extras.getString("android.title") ?: ""
        val text = extras.getString("android.text") ?: ""
        val bigText = extras.getCharSequence("android.bigText")?.toString() ?: ""

        val body = listOf(title, bigText.ifEmpty { text })
            .filter { it.isNotBlank() }
            .joinToString(" | ")

        if (body.isBlank()) return

        // Use package name as pseudo-sender for routing
        val pseudoSender = sbn.packageName

        scope.launch {
            ingestionService.processNotification(
                packageName = pseudoSender,
                notificationBody = body
            )
        }
    }
}
