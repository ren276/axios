package dev.sandesh.kharcha.feature.ingestion

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.sandesh.kharcha.core.model.TransactionRepository

@HiltWorker
class SmsPurgeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Delete all original SMS payloads for transactions that are confirmed
            val count = transactionRepository.purgeRawSmsForConfirmedTransactions()
            // Log or just return success
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
