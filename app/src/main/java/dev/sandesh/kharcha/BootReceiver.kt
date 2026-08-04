package dev.sandesh.kharcha

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * Reschedules WorkManager tasks after device reboot.
 * WorkManager handles most of this automatically in newer versions,
 * but explicit registration is still good practice.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // WorkManager will automatically reschedule persisted work
            // No explicit action needed in WorkManager 2.8+
        }
    }
}
