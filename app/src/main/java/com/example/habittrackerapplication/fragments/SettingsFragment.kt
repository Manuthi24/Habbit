package com.example.habittrackerapplication.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.habittrackerapplication.MainActivity
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.data.DataManager
import com.example.habittrackerapplication.databinding.FragmentSettingsBinding

/**
 * Fragment for app settings and preferences
 */
class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupClickListeners()
        loadSettings()
    }
    
    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        binding.cardNotifications.setOnClickListener {
            showNotificationSettings()
        }
        
        binding.cardTheme.setOnClickListener {
            showThemeDialog()
        }
        
        binding.cardData.setOnClickListener {
            showDataManagementDialog()
        }
        
        binding.cardAbout.setOnClickListener {
            showAboutDialog()
        }
    }
    
    /**
     * Load current settings
     */
    private fun loadSettings() {
        val settings = dataManager.getAppSettings()
        
        // Update notification settings display
        binding.textNotificationStatus.text = if (settings.hydrationRemindersEnabled) {
            "Enabled (${settings.reminderIntervalMinutes} min intervals)"
        } else {
            "Disabled"
        }
        
        // Update theme display
        binding.textThemeStatus.text = settings.theme.replaceFirstChar { it.uppercase() }
    }
    
    /**
     * Show notification settings dialog
     */
    private fun showNotificationSettings() {
        val settings = dataManager.getAppSettings()
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.notifications))
            .setMessage("Hydration reminders are currently ${if (settings.hydrationRemindersEnabled) "enabled" else "disabled"}")
            .setPositiveButton("Configure") { _, _ ->
                // Navigate to hydration fragment for settings
                Toast.makeText(context, "Go to Hydration tab to configure reminders", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton(getString(R.string.ok), null)
            .show()
    }
    
    /**
     * Show theme selection dialog
     */
    private fun showThemeDialog() {
        val themes = arrayOf("Light", "Dark", "System")
        val currentTheme = dataManager.getAppSettings().theme
        
        val selectedIndex = when (currentTheme) {
            "light" -> 0
            "dark" -> 1
            "system" -> 2
            else -> 0
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.theme))
            .setSingleChoiceItems(themes, selectedIndex) { dialog, which ->
                val newTheme = when (which) {
                    0 -> "light"
                    1 -> "dark"
                    2 -> "system"
                    else -> "light"
                }
                
                val currentSettings = dataManager.getAppSettings()
                val updatedSettings = currentSettings.copy(theme = newTheme)
                dataManager.saveAppSettings(updatedSettings)
                
                binding.textThemeStatus.text = themes[which]
                dialog.dismiss()
                
                Toast.makeText(context, "Theme changed to ${themes[which]}", Toast.LENGTH_SHORT).show()
                
                // Restart activity to apply theme changes
                (requireActivity() as MainActivity).restartActivity()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    /**
     * Show data management dialog
     */
    private fun showDataManagementDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Data Management")
            .setMessage("Manage your app data and export/import settings")
            .setPositiveButton("Export Data") { _, _ ->
                exportData()
            }
            .setNeutralButton("Clear All Data") { _, _ ->
                showClearDataConfirmation()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    /**
     * Export app data
     */
    private fun exportData() {
        val habits = dataManager.getHabits()
        val moodEntries = dataManager.getMoodEntries()
        val hydrationRecords = dataManager.getHydrationRecords()
        val settings = dataManager.getAppSettings()
        
        val exportText = buildString {
            appendLine("=== Habbit App Data Export ===")
            appendLine("Export Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")
            appendLine()
            
            appendLine("=== Habits ===")
            habits.forEach { habit ->
                appendLine("- ${habit.name}: ${habit.description} (${habit.frequency})")
            }
            appendLine()
            
            appendLine("=== Mood Entries ===")
            moodEntries.forEach { mood ->
                appendLine("- ${mood.emoji} ${mood.note} (${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(mood.timestamp))})")
            }
            appendLine()
            
            appendLine("=== Hydration Records ===")
            hydrationRecords.forEach { record ->
                appendLine("- ${record.date}: ${record.glassesConsumed}/${record.dailyGoal} glasses")
            }
            appendLine()
            
            appendLine("=== Settings ===")
            appendLine("- Hydration Reminders: ${settings.hydrationRemindersEnabled}")
            appendLine("- Reminder Interval: ${settings.reminderIntervalMinutes} minutes")
            appendLine("- Daily Water Goal: ${settings.dailyWaterGoal} glasses")
            appendLine("- Theme: ${settings.theme}")
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, exportText)
            type = "text/plain"
        }
        
        startActivity(Intent.createChooser(shareIntent, "Export Habbit Data"))
    }
    
    /**
     * Show confirmation dialog for clearing all data
     */
    private fun showClearDataConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("Are you sure you want to clear all your data? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                clearAllData()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    /**
     * Clear all app data
     */
    private fun clearAllData() {
        // Clear all data from SharedPreferences
        val prefs = requireContext().getSharedPreferences("habbit_preferences", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        
        Toast.makeText(context, "All data cleared successfully", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Show about dialog
     */
    private fun showAboutDialog() {
        val aboutText = """
            Habbit v1.0
            
            Your personal wellness companion for building healthy habits and tracking your mood.
            
            Features:
            • Daily habit tracking
            • Mood journal with emojis
            • Hydration reminders
            • Progress visualization
            
            Built with ❤️ for your wellness journey.
        """.trimIndent()
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.about))
            .setMessage(aboutText)
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
