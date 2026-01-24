package com.example.habittrackerapplication.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.habittrackerapplication.HabbitApplication
import com.example.habittrackerapplication.R
import java.util.concurrent.TimeUnit

/**
 * Manager for hydration reminder notifications
 * Uses WorkManager for reliable background notifications
 */
class HydrationNotificationManager(private val context: Context) {
    
    private val notificationManager = NotificationManagerCompat.from(context)
    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Schedule hydration reminders
     * @param intervalMinutes Interval between reminders in minutes
     */
    fun scheduleHydrationReminders(intervalMinutes: Int) {
        // Cancel existing reminders
        cancelHydrationReminders()
        
        // Create periodic work request
        val hydrationWorkRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            intervalMinutes.toLong(), TimeUnit.MINUTES
        )
            .setInitialDelay(intervalMinutes.toLong(), TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .addTag(HYDRATION_WORK_TAG)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            HYDRATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            hydrationWorkRequest
        )
    }
    
    /**
     * Cancel hydration reminders
     */
    fun cancelHydrationReminders() {
        workManager.cancelUniqueWork(HYDRATION_WORK_NAME)
    }
    
    /**
     * Show immediate hydration reminder notification
     */
    fun showHydrationReminder() {
        val notification = NotificationCompat.Builder(context, HabbitApplication.HYDRATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hydration)
            .setContentTitle(context.getString(R.string.hydration_reminder_title))
            .setContentText(context.getString(R.string.hydration_reminder_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setLights(android.graphics.Color.BLUE, 1000, 1000)
            .build()
        
        try {
            notificationManager.notify(HYDRATION_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Handle case where notifications are disabled
            android.util.Log.w("HydrationNotification", "Cannot show notification: ${e.message}")
        }
    }
    
    /**
     * Check if notifications are enabled
     */
    fun areNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationManager.areNotificationsEnabled()
        } else {
            true // Assume enabled for older versions
        }
    }
    
    /**
     * Request notification permission (Android 13+)
     */
    fun requestNotificationPermission(activity: android.app.Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }
    
    companion object {
        private const val HYDRATION_WORK_NAME = "hydration_reminder_work"
        private const val HYDRATION_WORK_TAG = "hydration_reminder"
        private const val HYDRATION_NOTIFICATION_ID = 1001
    }
}

/**
 * Worker class for hydration reminder notifications
 */
class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        val notificationManager = HydrationNotificationManager(applicationContext)
        notificationManager.showHydrationReminder()
        return Result.success()
    }
}
