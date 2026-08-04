package dev.sandesh.kharcha.feature.ingestion

import android.content.Context
import android.provider.Telephony
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.sandesh.kharcha.feature.ingestion.service.IngestionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One-time historical SMS importer.
 * Reads from Telephony.Sms content provider for messages in the last N months.
 *
 * Requires READ_SMS permission (same restricted-settings unlock as RECEIVE_SMS).
 *
 * Called once on first launch from onboarding, or manually triggered from Settings.
 * Progress is reported via a callback for UI display.
 */
@Singleton
class SmsBulkImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ingestionService: IngestionService
) {

    data class ImportProgress(
        val processed: Int,
        val total: Int,
        val skipped: Int,
        val imported: Int
    )

    /**
     * Import SMS messages from the last [monthsBack] months.
     * @param monthsBack Number of months of history to import (default 6)
     * @param onProgress Progress callback invoked for each processed batch
     */
    suspend fun importHistory(
        monthsBack: Int = 6,
        onProgress: (ImportProgress) -> Unit = {}
    ): ImportProgress = withContext(Dispatchers.IO) {
        val cutoffMs = System.currentTimeMillis() - (monthsBack * 30L * 24 * 60 * 60 * 1000)

        val projection = arrayOf(
            Telephony.Sms.ADDRESS,     // Sender/sender ID
            Telephony.Sms.BODY,        // SMS body
            Telephony.Sms.DATE,        // Timestamp
            Telephony.Sms.TYPE,        // Inbox/sent/etc
        )

        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            "${Telephony.Sms.DATE} > ?",
            arrayOf(cutoffMs.toString()),
            "${Telephony.Sms.DATE} DESC"
        )

        val total = cursor?.count ?: 0
        var processed = 0
        var imported = 0
        var skipped = 0

        cursor?.use { c ->
            val addrCol = c.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyCol = c.getColumnIndex(Telephony.Sms.BODY)

            while (c.moveToNext()) {
                val sender = c.getString(addrCol) ?: continue
                val body = c.getString(bodyCol) ?: continue

                val wasImported = ingestionService.processSms(
                    sender = sender,
                    body = body,
                    isHistorical = true
                )

                if (wasImported) imported++ else skipped++
                processed++

                // Report progress every 50 messages to avoid overwhelming the UI
                if (processed % 50 == 0) {
                    onProgress(ImportProgress(processed, total, skipped, imported))
                }
            }
        }

        val finalProgress = ImportProgress(processed, total, skipped, imported)
        onProgress(finalProgress)
        finalProgress
    }
}
