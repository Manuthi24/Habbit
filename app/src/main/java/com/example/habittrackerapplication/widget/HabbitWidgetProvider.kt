package com.example.habittrackerapplication.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.habittrackerapplication.MainActivity
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.data.DataManager
import com.example.habittrackerapplication.model.DateUtils

/**
 * App Widget Provider for Habbit
 * Shows today's habit completion progress and water intake
 */
class HabbitWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all widget instances
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onEnabled(context: Context) {
        // Called when the first widget is created
    }
    
    override fun onDisabled(context: Context) {
        // Called when the last widget is removed
    }
    
    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val dataManager = DataManager(context)
            
            // Get today's data
            val habits = dataManager.getHabits().filter { it.isActive }
            val completedHabits = dataManager.getTodayCompletions().size
            val totalHabits = habits.size
            val progress = if (totalHabits > 0) (completedHabits.toFloat() / totalHabits * 100).toInt() else 0
            
            val hydrationRecord = dataManager.getTodayHydrationRecord()
            val waterProgress = (hydrationRecord.glassesConsumed.toFloat() / hydrationRecord.dailyGoal * 100).toInt()
            
            // Create RemoteViews
            val views = RemoteViews(context.packageName, R.layout.widget_habbit)
            
            // Set click intent to open the app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetContainer, pendingIntent)
            
            // Update habit progress
            views.setTextViewText(R.id.textWidgetTitle, context.getString(R.string.widget_title))
            views.setTextViewText(R.id.textHabitsProgress, "$completedHabits/$totalHabits")
            views.setTextViewText(R.id.textHabitsLabel, context.getString(R.string.widget_habits_completed))
            views.setProgressBar(R.id.progressBarHabits, 100, progress, false)
            
            // Update water progress
            views.setTextViewText(R.id.textWaterProgress, "${hydrationRecord.glassesConsumed}/${hydrationRecord.dailyGoal}")
            views.setTextViewText(R.id.textWaterLabel, context.getString(R.string.widget_water_glasses))
            views.setProgressBar(R.id.progressBarWater, 100, waterProgress, false)
            
            // Update date
            views.setTextViewText(R.id.textWidgetDate, DateUtils.formatTimestamp(System.currentTimeMillis()))
            
            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
