package com.example.habittrackerapplication.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.habittrackerapplication.data.DataManager
import com.example.habittrackerapplication.model.Habit
import com.example.habittrackerapplication.model.HabitCompletion
import com.example.habittrackerapplication.model.DateUtils

/**
 * Step counter service using accelerometer sensor
 * Detects steps and automatically adds them as a habit completion
 */
class StepCounterService(private val context: Context) : SensorEventListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val dataManager = DataManager(context)
    
    private var lastStepTime = 0L
    private var stepCount = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var stepThreshold = 15f // Threshold for step detection
    
    companion object {
        private const val TAG = "StepCounterService"
        private const val STEP_INTERVAL = 300L // Minimum time between steps (ms)
        private const val SHAKE_THRESHOLD = 800f // Threshold for shake detection
    }
    
    /**
     * Start step counting
     */
    fun startStepCounting() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Step counting started")
        } ?: Log.w(TAG, "Accelerometer not available")
    }
    
    /**
     * Stop step counting
     */
    fun stopStepCounting() {
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Step counting stopped")
    }
    
    /**
     * Detect shake gesture for quick mood entry
     */
    fun detectShakeForMood() {
        accelerometer?.let {
            sensorManager.registerListener(object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let { sensorEvent ->
                        val x = sensorEvent.values[0]
                        val y = sensorEvent.values[1]
                        val z = sensorEvent.values[2]
                        
                        val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                        
                        if (acceleration > SHAKE_THRESHOLD) {
                            // Shake detected - could trigger quick mood entry
                            Log.d(TAG, "Shake detected for mood entry")
                            sensorManager.unregisterListener(this)
                        }
                    }
                }
                
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]
            
            val currentTime = System.currentTimeMillis()
            
            // Calculate acceleration difference
            val deltaX = Math.abs(x - lastX)
            val deltaY = Math.abs(y - lastY)
            val deltaZ = Math.abs(z - lastZ)
            
            val acceleration = deltaX + deltaY + deltaZ
            
            // Detect step
            if (acceleration > stepThreshold && 
                currentTime - lastStepTime > STEP_INTERVAL) {
                
                stepCount++
                lastStepTime = currentTime
                
                Log.d(TAG, "Step detected: $stepCount")
                
                // Auto-complete step counting habit if it exists
                autoCompleteStepHabit()
            }
            
            lastX = x
            lastY = y
            lastZ = z
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
    
    /**
     * Auto-complete step counting habit
     */
    private fun autoCompleteStepHabit() {
        val habits = dataManager.getHabits()
        val stepHabit = habits.find { 
            it.name.lowercase().contains("step") || 
            it.name.lowercase().contains("walk") ||
            it.name.lowercase().contains("exercise")
        }
        
        stepHabit?.let { habit ->
            val today = DateUtils.getCurrentDateString()
            val isAlreadyCompleted = dataManager.isHabitCompleted(habit.id, today)
            
            if (!isAlreadyCompleted && stepCount >= 100) { // Complete after 100 steps
                val completion = HabitCompletion(
                    habitId = habit.id,
                    date = today
                )
                dataManager.addHabitCompletion(completion)
                Log.d(TAG, "Auto-completed step habit: ${habit.name}")
            }
        }
    }
    
    /**
     * Get current step count
     */
    fun getStepCount(): Int = stepCount
    
    /**
     * Reset step count
     */
    fun resetStepCount() {
        stepCount = 0
    }
}
