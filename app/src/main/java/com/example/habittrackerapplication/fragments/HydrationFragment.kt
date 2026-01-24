package com.example.habittrackerapplication.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.data.DataManager
import com.example.habittrackerapplication.databinding.FragmentHydrationBinding
import com.example.habittrackerapplication.model.AppSettings
import com.example.habittrackerapplication.model.HydrationRecord
import com.example.habittrackerapplication.model.DateUtils
import com.example.habittrackerapplication.notification.HydrationNotificationManager

/**
 * Fragment for hydration tracking and reminder settings
 * Allows users to track water intake and configure reminders
 */
class HydrationFragment : Fragment() {
    
    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private lateinit var notificationManager: HydrationNotificationManager
    private var currentRecord: HydrationRecord? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHydrationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        notificationManager = HydrationNotificationManager(requireContext())
        
        setupClickListeners()
        loadHydrationData()
        updateDisplay()
    }
    
    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        binding.buttonAddGlass.setOnClickListener {
            addGlassOfWater()
        }
        
        binding.buttonSettings.setOnClickListener {
            showSettingsDialog()
        }
        
        /*binding.buttonTestNotification.setOnClickListener {
            // Test hydration notification
            notificationManager.showHydrationReminder()
            Toast.makeText(context, "Test notification sent!", Toast.LENGTH_SHORT).show()
        }*/
        
        binding.buttonNotificationSettings.setOnClickListener {
            openNotificationSettings()
        }
    }
    
    /**
     * Load hydration data for today
     */
    private fun loadHydrationData() {
        currentRecord = dataManager.getTodayHydrationRecord()
    }
    
    /**
     * Update the display with current hydration data
     */
    private fun updateDisplay() {
        currentRecord?.let { record ->
            binding.textGlassesConsumed.text = record.glassesConsumed.toString()
            binding.textDailyGoal.text = record.dailyGoal.toString()
            
            // Update progress bar
            val progress = (record.glassesConsumed.toFloat() / record.dailyGoal * 100).toInt()
            binding.progressBarHydration.progress = progress
            
            // Update progress text
            binding.textProgress.text = "${record.glassesConsumed}/${record.dailyGoal} glasses"
            
            // Update water drop animation based on progress
            updateWaterDropAnimation(progress)
        }
    }
    
    /**
     * Update water drop animation based on progress
     */
    private fun updateWaterDropAnimation(progress: Int) {
        when {
            progress >= 100 -> {
                binding.textWaterDrop.text = "💧💧💧💧💧💧💧💧"
                binding.textMotivation.text = "Great job! You've reached your goal! 🎉"
            }
            progress >= 75 -> {
                binding.textWaterDrop.text = "💧💧💧💧💧💧💧"
                binding.textMotivation.text = "Almost there! Keep it up! 💪"
            }
            progress >= 50 -> {
                binding.textWaterDrop.text = "💧💧💧💧"
                binding.textMotivation.text = "Halfway there! You're doing great! 😊"
            }
            progress >= 25 -> {
                binding.textWaterDrop.text = "💧💧"
                binding.textMotivation.text = "Good start! Keep drinking water! 💧"
            }
            else -> {
                binding.textWaterDrop.text = "💧"
                binding.textMotivation.text = "Let's start your hydration journey! 🌟"
            }
        }
    }
    
    /**
     * Add a glass of water
     */
    private fun addGlassOfWater() {
        dataManager.addGlassOfWater()
        loadHydrationData()
        updateDisplay()
        // Refresh homescreen widget hydration progress
        try {
            val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(requireContext())
            val widgetProvider = android.content.ComponentName(requireContext(), com.example.habittrackerapplication.widget.HabbitWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetProvider)
            for (appWidgetId in appWidgetIds) {
                com.example.habittrackerapplication.widget.HabbitWidgetProvider.updateAppWidget(requireContext(), appWidgetManager, appWidgetId)
            }
        } catch (_: Exception) {}
        
        // Show celebration if goal is reached
        currentRecord?.let { record ->
            if (record.glassesConsumed == record.dailyGoal) {
                Toast.makeText(context, "🎉 Congratulations! You've reached your daily goal!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "💧 Glass of water added!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Show settings dialog
     */
    private fun showSettingsDialog() {
        val settings = dataManager.getAppSettings()
        
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_hydration_settings, null)
        
        val switchReminders = dialogView.findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switchReminders)
        val seekBarInterval = dialogView.findViewById<SeekBar>(R.id.seekBarInterval)
        val textInterval = dialogView.findViewById<android.widget.TextView>(R.id.textInterval)
        val seekBarGoal = dialogView.findViewById<SeekBar>(R.id.seekBarGoal)
        val textGoal = dialogView.findViewById<android.widget.TextView>(R.id.textGoal)
        
        // Set current values
        switchReminders.isChecked = settings.hydrationRemindersEnabled
        seekBarInterval.progress = settings.reminderIntervalMinutes / 15 - 1 // 15-240 minutes in 15-min intervals
        textInterval.text = "${settings.reminderIntervalMinutes} minutes"
        seekBarGoal.progress = settings.dailyWaterGoal - 1 // 1-12 glasses
        textGoal.text = "${settings.dailyWaterGoal} glasses"
        
        // Setup listeners
        seekBarInterval.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val minutes = (progress + 1) * 15
                textInterval.text = "$minutes minutes"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        seekBarGoal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val glasses = progress + 1
                textGoal.text = "$glasses glasses"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.reminder_settings))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val newSettings = AppSettings(
                    hydrationRemindersEnabled = switchReminders.isChecked,
                    reminderIntervalMinutes = (seekBarInterval.progress + 1) * 15,
                    dailyWaterGoal = seekBarGoal.progress + 1,
                    theme = settings.theme
                )
                
                dataManager.saveAppSettings(newSettings)
                
                // Update current record with new goal
                currentRecord?.let { record ->
                    val updatedRecord = record.copy(dailyGoal = newSettings.dailyWaterGoal)
                    dataManager.updateHydrationRecord(updatedRecord)
                    loadHydrationData()
                    updateDisplay()
                }
                
                // Restart notifications with new settings
                if (newSettings.hydrationRemindersEnabled) {
                    // Check if notifications are enabled
                    if (notificationManager.areNotificationsEnabled()) {
                        notificationManager.scheduleHydrationReminders(newSettings.reminderIntervalMinutes)
                        // Show an immediate reminder to confirm setup
                        notificationManager.showHydrationReminder()
                        Toast.makeText(context, "Hydration reminders enabled!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Show permission request dialog
                        showNotificationPermissionDialog()
                    }
                } else {
                    notificationManager.cancelHydrationReminders()
                    Toast.makeText(context, "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
                }
                
                Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()

                // Refresh homescreen widget to reflect any goal/label changes
                try {
                    val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(requireContext())
                    val widgetProvider = android.content.ComponentName(requireContext(), com.example.habittrackerapplication.widget.HabbitWidgetProvider::class.java)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetProvider)
                    for (appWidgetId in appWidgetIds) {
                        com.example.habittrackerapplication.widget.HabbitWidgetProvider.updateAppWidget(requireContext(), appWidgetManager, appWidgetId)
                    }
                } catch (_: Exception) {}
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    /**
     * Open notification settings
     */
    private fun openNotificationSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }
    
    /**
     * Show notification permission dialog
     */
    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Notification Permission Required")
            .setMessage("To receive hydration reminders, please enable notifications for this app in your device settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openNotificationSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Disable reminders if user cancels
                val settings = dataManager.getAppSettings()
                val updatedSettings = settings.copy(hydrationRemindersEnabled = false)
                dataManager.saveAppSettings(updatedSettings)
                Toast.makeText(context, "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
