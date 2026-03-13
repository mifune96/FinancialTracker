package com.aliimran.financialtracker.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Re-schedules the daily reminder alarm after a device reboot,
 * because [android.app.AlarmManager] alarms do not survive reboots.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val prefs = context.getSharedPreferences(
            ReminderScheduler.PREFS_NAME,
            Context.MODE_PRIVATE,
        )
        val isEnabled = prefs.getBoolean(ReminderScheduler.KEY_REMINDER_ENABLED, false)

        if (isEnabled) {
            ReminderScheduler.schedule(context)
        }
    }
}
