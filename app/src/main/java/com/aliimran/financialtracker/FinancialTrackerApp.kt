package com.aliimran.financialtracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.aliimran.financialtracker.notification.ReminderNotificationReceiver
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class — entry point for Dagger Hilt's component hierarchy.
 * Must be declared in AndroidManifest.xml:
 *   android:name=".FinancialTrackerApp"
 */
@HiltAndroidApp
class FinancialTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createReminderNotificationChannel()
    }

    private fun createReminderNotificationChannel() {
        val channel = NotificationChannel(
            ReminderNotificationReceiver.CHANNEL_ID,
            "Pengingat Harian",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Pengingat harian untuk mencatat pengeluaran"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
