package com.example.habittrackerapplication.data

import android.content.Context
import android.content.SharedPreferences
import com.example.habittrackerapplication.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DataManager(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "habbit_preferences"
        private const val KEY_HABITS = "habits"
        private const val KEY_HABIT_COMPLETIONS = "habit_completions"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
        private const val KEY_HYDRATION_RECORDS = "hydration_records"
        private const val KEY_APP_SETTINGS = "app_settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }
    
    // Habits Management
    fun saveHabits(habits: List<Habit>) {
        val habitsJson = gson.toJson(habits)
        sharedPreferences.edit().putString(KEY_HABITS, habitsJson).apply()
    }
    
    fun getHabits(): List<Habit> {
        val habitsJson = sharedPreferences.getString(KEY_HABITS, null)
        return if (habitsJson != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(habitsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }
    
    fun updateHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            saveHabits(habits)
        }
    }
    
    fun deleteHabit(habitId: String) {
        val habits = getHabits().toMutableList()
        habits.removeAll { it.id == habitId }
        saveHabits(habits)
        
        // Also remove all completions for this habit
        val completions = getHabitCompletions().toMutableList()
        completions.removeAll { it.habitId == habitId }
        saveHabitCompletions(completions)
    }
    
    // Habit Completions Management
    fun saveHabitCompletions(completions: List<HabitCompletion>) {
        val completionsJson = gson.toJson(completions)
        sharedPreferences.edit().putString(KEY_HABIT_COMPLETIONS, completionsJson).apply()
    }
    
    fun getHabitCompletions(): List<HabitCompletion> {
        val completionsJson = sharedPreferences.getString(KEY_HABIT_COMPLETIONS, null)
        return if (completionsJson != null) {
            val type = object : TypeToken<List<HabitCompletion>>() {}.type
            gson.fromJson(completionsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun addHabitCompletion(completion: HabitCompletion) {
        val completions = getHabitCompletions().toMutableList()
        // Remove existing completion for the same habit and date
        completions.removeAll { it.habitId == completion.habitId && it.date == completion.date }
        completions.add(completion)
        saveHabitCompletions(completions)
    }
    
    fun removeHabitCompletion(habitId: String, date: String) {
        val completions = getHabitCompletions().toMutableList()
        completions.removeAll { it.habitId == habitId && it.date == date }
        saveHabitCompletions(completions)
    }
    
    fun isHabitCompleted(habitId: String, date: String): Boolean {
        return getHabitCompletions().any { it.habitId == habitId && it.date == date }
    }
    
    fun getTodayCompletions(): List<HabitCompletion> {
        val today = DateUtils.getCurrentDateString()
        return getHabitCompletions().filter { it.date == today }
    }
    
    // Mood Entries Management
    fun saveMoodEntries(entries: List<MoodEntry>) {
        val entriesJson = gson.toJson(entries)
        sharedPreferences.edit().putString(KEY_MOOD_ENTRIES, entriesJson).apply()
    }
    
    fun getMoodEntries(): List<MoodEntry> {
        val entriesJson = sharedPreferences.getString(KEY_MOOD_ENTRIES, null)
        return if (entriesJson != null) {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(entriesJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun addMoodEntry(entry: MoodEntry) {
        val entries = getMoodEntries().toMutableList()
        entries.add(entry)
        saveMoodEntries(entries)
    }
    
    fun updateMoodEntry(entry: MoodEntry) {
        val entries = getMoodEntries().toMutableList()
        val index = entries.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            entries[index] = entry
            saveMoodEntries(entries)
        }
    }
    
    fun deleteMoodEntry(entryId: String) {
        val entries = getMoodEntries().toMutableList()
        entries.removeAll { it.id == entryId }
        saveMoodEntries(entries)
    }
    
    // Hydration Records Management
    fun saveHydrationRecords(records: List<HydrationRecord>) {
        val recordsJson = gson.toJson(records)
        sharedPreferences.edit().putString(KEY_HYDRATION_RECORDS, recordsJson).apply()
    }
    
    fun getHydrationRecords(): List<HydrationRecord> {
        val recordsJson = sharedPreferences.getString(KEY_HYDRATION_RECORDS, null)
        return if (recordsJson != null) {
            val type = object : TypeToken<List<HydrationRecord>>() {}.type
            gson.fromJson(recordsJson, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun getTodayHydrationRecord(): HydrationRecord {
        val today = DateUtils.getCurrentDateString()
        val records = getHydrationRecords()
        return records.find { it.date == today } ?: HydrationRecord(date = today)
    }
    
    fun updateHydrationRecord(record: HydrationRecord) {
        val records = getHydrationRecords().toMutableList()
        val index = records.indexOfFirst { it.date == record.date }
        if (index != -1) {
            records[index] = record
        } else {
            records.add(record)
        }
        saveHydrationRecords(records)
    }
    
    fun addGlassOfWater() {
        val todayRecord = getTodayHydrationRecord()
        val updatedRecord = todayRecord.copy(
            glassesConsumed = todayRecord.glassesConsumed + 1,
            lastUpdated = System.currentTimeMillis()
        )
        updateHydrationRecord(updatedRecord)
    }
    
    // App Settings Management
    fun saveAppSettings(settings: AppSettings) {
        val settingsJson = gson.toJson(settings)
        sharedPreferences.edit().putString(KEY_APP_SETTINGS, settingsJson).apply()
    }
    
    fun getAppSettings(): AppSettings {
        val settingsJson = sharedPreferences.getString(KEY_APP_SETTINGS, null)
        return if (settingsJson != null) {
            gson.fromJson(settingsJson, AppSettings::class.java) ?: AppSettings()
        } else {
            AppSettings()
        }
    }
    
    // First Launch Management
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    fun setFirstLaunchCompleted() {
        sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }
    
    // Utility Methods
    fun getTodayProgress(): Float {
        val habits = getHabits().filter { it.isActive }
        if (habits.isEmpty()) return 0f
        
        val todayCompletions = getTodayCompletions()
        val completedHabits = habits.count { habit ->
            todayCompletions.any { completion -> completion.habitId == habit.id }
        }
        
        return completedHabits.toFloat() / habits.size
    }
    
    fun getWeeklyMoodTrend(): List<MoodEntry> {
        val entries = getMoodEntries()
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        return entries.filter { it.timestamp >= sevenDaysAgo }
            .sortedBy { it.timestamp }
    }
}
