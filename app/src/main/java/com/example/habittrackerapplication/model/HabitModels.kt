package com.example.habittrackerapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Data class representing a habit
 * @param id Unique identifier for the habit
 * @param name Name of the habit
 * @param description Optional description
 * @param frequency How often the habit should be performed (Daily/Weekly)
 * @param createdAt When the habit was created
 * @param isActive Whether the habit is currently active
 */
@Parcelize
data class Habit(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) : Parcelable

/**
 * Enum for habit frequency
 */
enum class HabitFrequency {
    DAILY, WEEKLY
}

/**
 * Data class representing a habit completion record
 * @param habitId ID of the habit that was completed
 * @param date Date when the habit was completed
 * @param completedAt Timestamp when completion was recorded
 */
@Parcelize
data class HabitCompletion(
    val habitId: String,
    val date: String, // Format: yyyy-MM-dd
    val completedAt: Long = System.currentTimeMillis()
) : Parcelable

/**
 * Data class representing a mood entry
 * @param id Unique identifier for the mood entry
 * @param emoji Emoji representing the mood
 * @param note Optional note about the mood
 * @param timestamp When the mood was recorded
 */
@Parcelize
data class MoodEntry(
    val id: String = "",
    val emoji: String = "",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable

/**
 * Data class representing hydration data
 * @param date Date for the hydration record
 * @param glassesConsumed Number of glasses consumed
 * @param dailyGoal Target number of glasses per day
 * @param lastUpdated When the record was last updated
 */
@Parcelize
data class HydrationRecord(
    val date: String, // Format: yyyy-MM-dd
    val glassesConsumed: Int = 0,
    val dailyGoal: Int = 8,
    val lastUpdated: Long = System.currentTimeMillis()
) : Parcelable

/**
 * Data class representing app settings
 * @param hydrationRemindersEnabled Whether hydration reminders are enabled
 * @param reminderIntervalMinutes Interval between hydration reminders in minutes
 * @param dailyWaterGoal Daily water intake goal in glasses
 * @param theme Theme preference
 */
@Parcelize
data class AppSettings(
    val hydrationRemindersEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 60,
    val dailyWaterGoal: Int = 8,
    val theme: String = "light"
) : Parcelable

/**
 * Utility object for date formatting
 */
object DateUtils {
    /**
     * Get current date in yyyy-MM-dd format
     */
    fun getCurrentDateString(): String {
        val date = Date()
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Format timestamp to readable date string
     */
    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Format timestamp to readable time string
     */
    fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return formatter.format(date)
    }
}
