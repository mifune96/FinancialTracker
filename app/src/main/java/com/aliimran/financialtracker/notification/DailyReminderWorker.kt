package com.aliimran.financialtracker.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aliimran.financialtracker.MainActivity
import com.aliimran.financialtracker.R

/**
 * WorkManager [Worker] that shows the daily reminder notification.
 *
 * WorkManager is far more reliable than [android.app.AlarmManager] on
 * OEM-customised devices (Infinix, Xiaomi, Oppo, etc.) because the
 * Google Play Services job scheduler is allow-listed by those vendors.
 */
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters,
) : Worker(context, params) {

    companion object {
        private const val TAG = "DailyReminderWorker"
        const val CHANNEL_ID = "daily_reminder"
        private const val NOTIFICATION_ID = 2001
    }

    override fun doWork(): Result {
        Log.d(TAG, "doWork() — showing daily reminder notification")
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val tapIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Pengingat Harian")
            .setContentText("Jangan lupa mencatat pengeluaran hari ini! 💰")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
        Log.d(TAG, "Notification posted successfully")

        // Reschedule for the next day
        ReminderScheduler.schedule(applicationContext)
        Log.d(TAG, "Rescheduled for next day")
    }
}
