package com.example.habittrackerapplication.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.data.DataManager
import com.example.habittrackerapplication.databinding.FragmentMoodChartBinding
import com.example.habittrackerapplication.model.DateUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for displaying mood trend chart
 * Shows mood entries over the past week
 */
class MoodChartFragment : Fragment() {
    
    private var _binding: FragmentMoodChartBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodChartBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupChart()
    }
    
    /**
     * Setup mood trend chart
     */
    private fun setupChart() {
        val moodEntries = dataManager.getWeeklyMoodTrend()
        
        if (moodEntries.isEmpty()) {
            binding.chartMood.visibility = View.GONE
            binding.textNoData.visibility = View.VISIBLE
            return
        }
        
        // Convert mood entries to chart entries
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()
        
        // Group mood entries by day
        val moodByDay = moodEntries.groupBy { 
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp))
        }
        
        // Create entries for the last 7 days
        val calendar = Calendar.getInstance()
        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            
            val dayMoods = moodByDay[dateString] ?: emptyList()
            val averageMood = if (dayMoods.isNotEmpty()) {
                dayMoods.map { moodToNumericValue(it.emoji) }.average().toFloat()
            } else {
                0f
            }

            entries.add(Entry((6 - i).toFloat(), averageMood))
            labels.add(SimpleDateFormat("MMM dd", Locale.getDefault()).format(calendar.time))
        }
        
        // Create dataset
        val dataSet = LineDataSet(entries, "Mood Trend").apply {
            color = requireContext().getColor(R.color.chart_line)
            lineWidth = 3f
            setDrawCircles(false) // hide dots
            setDrawValues(false)
            setDrawFilled(true)
            fillColor = requireContext().getColor(R.color.chart_fill)
            fillAlpha = 60
        }
        
        val lineData = LineData(dataSet)
        binding.chartMood.data = lineData
        
        // Configure chart
        binding.chartMood.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            
            // Configure X-axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (value.toInt() < labels.size) {
                            labels[value.toInt()]
                        } else ""
                    }
                }
                textSize = 12f
                textColor = requireContext().getColor(R.color.chart_axis)
            }
            
            // Configure Y-axis
            axisLeft.apply {
                setDrawGridLines(true)
                setDrawAxisLine(false)
                gridColor = requireContext().getColor(R.color.chart_grid)
                textColor = requireContext().getColor(R.color.chart_axis)
                textSize = 12f
                axisMinimum = 0f
                axisMaximum = 5f
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value.toInt()) {
                            0 -> "😢"
                            1 -> "😔"
                            2 -> "😐"
                            3 -> "😊"
                            4 -> "😄"
                            5 -> "😍"
                            else -> ""
                        }
                    }
                }
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = false
        }
        
        binding.chartMood.invalidate()
    }
    
    /**
     * Convert mood emoji to numeric value for charting
     */
    private fun moodToNumericValue(emoji: String): Int {
        return when {
            emoji.contains("😍") || emoji.contains("🥰") -> 5
            emoji.contains("😄") || emoji.contains("😊") -> 4
            emoji.contains("😐") || emoji.contains("😑") -> 3
            emoji.contains("😔") || emoji.contains("😞") -> 2
            emoji.contains("😢") || emoji.contains("😭") -> 1
            emoji.contains("😠") || emoji.contains("😡") -> 0
            else -> 3 // Default neutral
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
