package com.example.habittrackerapplication

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.habittrackerapplication.data.DataManager
import com.example.habittrackerapplication.databinding.ActivityMainBinding
import com.example.habittrackerapplication.notification.HydrationNotificationManager
import com.example.habittrackerapplication.onboarding.OnboardingActivity
import com.example.habittrackerapplication.sensors.StepCounterService
import com.example.habittrackerapplication.widget.HabbitWidgetProvider

/**
 * Main activity that hosts the navigation and fragments
 * Checks if it's the first launch and shows onboarding if needed
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var dataManager: DataManager
    private lateinit var stepCounterService: StepCounterService
    private lateinit var notificationManager: HydrationNotificationManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize data manager first
        dataManager = DataManager(this)
        
        // Apply theme before setting content view
        applyTheme()
        
        enableEdgeToEdge()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        stepCounterService = StepCounterService(this)
        notificationManager = HydrationNotificationManager(this)
        
        // Always start with onboarding unless returning from theme change or just completed onboarding
        val fromThemeChange = intent.getBooleanExtra("from_theme_change", false)
        val fromOnboarding = intent.getBooleanExtra("from_onboarding", false)
        if (!fromThemeChange && !fromOnboarding) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        
        // Setup main app after onboarding completion or theme change
        setupNavigation()
        setupServices()
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    /**
     * Setup bottom navigation with NavController
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation!!.setupWithNavController(navController)
    }
    
    /**
     * Setup background services
     */
    private fun setupServices() {
        // Start step counting
        stepCounterService.startStepCounting()
        
        // Setup hydration reminders
        val settings = dataManager.getAppSettings()
        if (settings.hydrationRemindersEnabled) {
            notificationManager.scheduleHydrationReminders(settings.reminderIntervalMinutes)
        }
        
        // Update widget
        updateWidget()
    }
    
    /**
     * Update home screen widget
     */
    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val widgetProvider = ComponentName(this, HabbitWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetProvider)
        
        for (appWidgetId in appWidgetIds) {
            HabbitWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Update widget when returning to app
        updateWidget()
    }
    
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Handle theme changes without recreating the activity
        val currentTheme = dataManager.getAppSettings().theme
        val systemTheme = when (newConfig.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> "dark"
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> "light"
            else -> "system"
        }
        
        if (currentTheme == "system" && systemTheme != currentTheme) {
            // System theme changed, restart to apply new theme
            restartActivity()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop step counting when activity is destroyed
        stepCounterService.stopStepCounting()
    }
    
    /**
     * Apply theme based on user preference
     */
    private fun applyTheme() {
        val settings = dataManager.getAppSettings()
        when (settings.theme) {
            "dark" -> setTheme(R.style.Theme_HabitTrackerApplication_Dark)
            "light" -> setTheme(R.style.Theme_HabitTrackerApplication_Light)
            "system" -> setTheme(R.style.Theme_HabitTrackerApplication)
            else -> setTheme(R.style.Theme_HabitTrackerApplication)
        }
    }
    
    /**
     * Restart activity to apply theme changes
     */
    fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("from_theme_change", true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}