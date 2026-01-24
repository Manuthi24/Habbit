package com.example.habittrackerapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * Application class for Habbit app
 * Initializes notification channels and app-wide configurations
 */
class HabbitApplication : Application() {
    
    companion object {
        const val HYDRATION_CHANNEL_ID = "hydration_reminder_channel"
        const val HYDRATION_CHANNEL_NAME = "Hydration Reminders"
        const val HYDRATION_CHANNEL_DESCRIPTION = "Reminders to drink water"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    /**
     * Creates notification channels for Android O and above
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val hydrationChannel = NotificationChannel(
                HYDRATION_CHANNEL_ID,
                HYDRATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = HYDRATION_CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(hydrationChannel)
        }
    }
}
