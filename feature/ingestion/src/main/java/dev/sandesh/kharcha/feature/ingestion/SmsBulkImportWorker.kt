package dev.sandesh.kharcha.feature.ingestion

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.sandesh.kharcha.core.model.TransactionRepository
import dev.sandesh.kharcha.feature.parser.rules.ParserEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SmsBulkImportWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val parserEngine: ParserEngine,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Only fetch SMS from the last 6 months
            val sixMonthsAgo = System.currentTimeMillis() - (180L * 24 * 60 * 60 * 1000)
            
            val cursor = appContext.contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
                "${Telephony.Sms.DATE} >= ?",
                arrayOf(sixMonthsAgo.toString()),
                "${Telephony.Sms.DATE} DESC"
            )

            var processed = 0
            var imported = 0

            cursor?.use {
                val addressIndex = it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val bodyIndex = it.getColumnIndexOrThrow(Telephony.Sms.BODY)
                val dateIndex = it.getColumnIndexOrThrow(Telephony.Sms.DATE)

                while (it.moveToNext()) {
                    val address = it.getString(addressIndex)
                    val body = it.getString(bodyIndex)
                    val date = it.getLong(dateIndex)

                    processed++
                    
                    val parsed = parserEngine.parse(address, body)
                    val txn = parsed.transaction
                    if (txn != null) {
                        // Avoid duplicates
                        val duplicate = transactionRepository.checkDuplicate(
                            amount = txn.amount,
                            accountLast4 = "",
                            timestampFrom = date - (24 * 60 * 60 * 1000), // 24h window
                            timestampTo = date + (24 * 60 * 60 * 1000)
                        )
                        if (duplicate == null) {
                            transactionRepository.insertTransaction(txn)
                            imported++
                        }
                    }
                }
            }

            // We can return the counts using Result.success(workDataOf(...)) if needed
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
