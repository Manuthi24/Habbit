package com.example.habittrackerapplication.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver for hydration notifications
 */
class HydrationNotificationReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = HydrationNotificationManager(context)
        notificationManager.showHydrationReminder()
    }
}
