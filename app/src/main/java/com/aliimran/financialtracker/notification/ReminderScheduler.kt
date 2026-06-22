package com.aliimran.financialtracker.notification

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Schedules / cancels the daily reminder using WorkManager OneTimeWorkRequest.
 *
 * WorkManager tetap jalan walau app di-close / force close, karena
 * dikelola oleh Google Play Services yang di-whitelist oleh semua
 * vendor HP (Infinix, Xiaomi, Oppo, dll).
 */
object ReminderScheduler {

    private const val TAG = "ReminderScheduler"
    private const val WORK_NAME = "daily_reminder_work"
    const val PREFS_NAME = "financial_tracker_prefs"
    const val KEY_REMINDER_ENABLED = "daily_reminder_enabled"

    /** Schedule notifikasi harian. Untuk testing: delay 10 detik saja. */
    fun schedule(context: Context) {
        // TEST MODE: delay 10 detik agar notifikasi muncul cepat
        val delayMs = 10_000L
        Log.d(TAG, "Scheduling daily reminder — delay = ${delayMs / 1000}s")

        val workRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )

        Log.d(TAG, "WorkManager enqueued successfully")
    }

    /** Cancel the daily reminder work. */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        Log.d(TAG, "Daily reminder cancelled")
    }

    /**
     * Returns milliseconds from now until the next 20:00.
     * (Used in production — currently bypassed for testing)
     */
    private fun calculateDelayMs(): Long {
        val now = System.currentTimeMillis()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return target.timeInMillis - now
    }
}
