package dev.sandesh.kharcha.feature.budget.bills

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.sandesh.kharcha.core.model.BillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class BillReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val billRepository: BillRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val bills = billRepository.getAllBills()
            
            val currentTime = System.currentTimeMillis()
            // 3 days before reminder
            val threeDaysMs = 3L * 24 * 60 * 60 * 1000
            
            for (bill in bills) {
                if (!bill.isPaid && bill.dueDate != null) {
                    val timeUntilDue = bill.dueDate - currentTime
                    if (timeUntilDue in 0..threeDaysMs) {
                        showNotification(bill.name, bill.amount)
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun showNotification(name: String, amount: Double?) {
        val channelId = "bill_reminders"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Bill Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming bills"
            }
            val notificationManager: NotificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val amountStr = if (amount != null) "₹$amount" else "Varies"
        val builder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Upcoming Bill: $name")
            .setContentText("Amount due soon: $amountStr")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(appContext)) {
            // Need permission on Android 13+, assuming it's granted
            try {
                notify(name.hashCode(), builder.build())
            } catch (e: SecurityException) {
                // Ignore if permission not granted
            }
        }
    }
}
