package com.example.habittrackerapplication.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.habittrackerapplication.data.DataManager

/**
 * Broadcast receiver for boot completed
 * Restarts hydration reminders after device reboot
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val dataManager = DataManager(context)
            val settings = dataManager.getAppSettings()
            
            if (settings.hydrationRemindersEnabled) {
                val notificationManager = HydrationNotificationManager(context)
                notificationManager.scheduleHydrationReminders(settings.reminderIntervalMinutes)
            }
        }
    }
}
