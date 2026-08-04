package dev.sandesh.kharcha.feature.ingestion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import dagger.hilt.android.AndroidEntryPoint
import dev.sandesh.kharcha.feature.ingestion.service.IngestionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Receives incoming SMS messages in real-time.
 * Extracts sender + body, passes to IngestionService for parsing and storage.
 *
 * Requires RECEIVE_SMS permission (restricted setting on Android 15+).
 * The onboarding flow walks the user through enabling this.
 */
@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var ingestionService: IngestionService

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") return

        val bundle = intent.extras ?: return
        val pdus = bundle.get("pdus") as? Array<*> ?: return
        val format = bundle.getString("format")

        val messages = pdus.mapNotNull { pdu ->
            @Suppress("DEPRECATION")
            if (format != null) {
                SmsMessage.createFromPdu(pdu as ByteArray, format)
            } else {
                SmsMessage.createFromPdu(pdu as ByteArray)
            }
        }

        if (messages.isEmpty()) return

        // Concatenate multi-part SMS
        val sender = messages.first().originatingAddress ?: return
        val body = messages.joinToString("") { it.messageBody }

        scope.launch {
            ingestionService.processSms(sender = sender, body = body)
        }
    }
}
