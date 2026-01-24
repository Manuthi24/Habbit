package com.example.habittrackerapplication.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.data.DataManager
import com.example.habittrackerapplication.databinding.FragmentHabitsBinding
import com.example.habittrackerapplication.databinding.DialogAddHabitBinding
import com.example.habittrackerapplication.model.Habit
import com.example.habittrackerapplication.model.HabitCompletion
import com.example.habittrackerapplication.model.HabitFrequency
import com.example.habittrackerapplication.model.DateUtils
import com.example.habittrackerapplication.adapters.HabitsAdapter
import java.util.UUID

/**
 * Fragment for managing daily habits
 * Allows users to add, edit, delete habits and mark them as completed
 */
class HabitsFragment : Fragment() {
    
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private lateinit var habitsAdapter: HabitsAdapter
    private val habits = mutableListOf<Habit>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupRecyclerView()
        setupClickListeners()
        loadHabits()
        updateProgress()
    }
    
    /**
     * Setup RecyclerView for habits list
     */
    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(
            habits = habits,
            onHabitToggle = { habit, isCompleted ->
                toggleHabitCompletion(habit, isCompleted)
            },
            onHabitEdit = { habit ->
                showEditHabitDialog(habit)
            },
            onHabitDelete = { habit ->
                showDeleteHabitDialog(habit)
            }
        )
        
        // Set data manager for the adapter
        habitsAdapter.setDataManager(dataManager)
        
        binding.recyclerViewHabits.apply {
            // Use GridLayoutManager for tablets, LinearLayoutManager for phones
            val isTablet = resources.configuration.smallestScreenWidthDp >= 600
            layoutManager = if (isTablet) {
                androidx.recyclerview.widget.GridLayoutManager(context, 2)
            } else {
                LinearLayoutManager(context)
            }
            adapter = habitsAdapter
        }
    }
    
    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        binding.fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    /**
     * Load habits from data manager
     */
    private fun loadHabits() {
        habits.clear()
        habits.addAll(dataManager.getHabits().filter { it.isActive })
        habitsAdapter.notifyDataSetChanged()
        
        // Show empty state if no habits
        if (habits.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewHabits.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewHabits.visibility = View.VISIBLE
        }
    }
    
    /**
     * Update progress display
     */
    private fun updateProgress() {
        val progress = dataManager.getTodayProgress()
        val completedHabits = dataManager.getTodayCompletions().size
        val totalHabits = habits.size
        
        binding.progressText.text = getString(R.string.progress_today)
        binding.progressBar.progress = (progress * 100).toInt()
        binding.progressPercentage.text = "${completedHabits}/${totalHabits}"
    }
    
    /**
     * Show dialog to add a new habit
     */
    private fun showAddHabitDialog(habit: Habit? = null) {
        val dialogBinding = DialogAddHabitBinding.inflate(layoutInflater)
        
        // Pre-fill fields if editing
        habit?.let {
            dialogBinding.editTextHabitName.setText(it.name)
            dialogBinding.editTextHabitDescription.setText(it.description)
            dialogBinding.radioGroupFrequency.check(
                if (it.frequency == HabitFrequency.DAILY) R.id.radioDaily else R.id.radioWeekly
            )
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (habit == null) getString(R.string.add_habit) else getString(R.string.edit_habit))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val name = dialogBinding.editTextHabitName.text.toString().trim()
                val description = dialogBinding.editTextHabitDescription.text.toString().trim()
                val frequency = if (dialogBinding.radioDaily.isChecked) HabitFrequency.DAILY else HabitFrequency.WEEKLY
                
                if (name.isNotEmpty()) {
                    if (habit == null) {
                        addHabit(name, description, frequency)
                    } else {
                        updateHabit(habit.copy(name = name, description = description, frequency = frequency))
                    }
                } else {
                    Toast.makeText(context, "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        
        dialog.show()
    }
    
    /**
     * Show dialog to edit a habit
     */
    private fun showEditHabitDialog(habit: Habit) {
        showAddHabitDialog(habit)
    }
    
    /**
     * Show dialog to confirm habit deletion
     */
    private fun showDeleteHabitDialog(habit: Habit) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_habit))
            .setMessage("Are you sure you want to delete \"${habit.name}\"?")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteHabit(habit)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    /**
     * Add a new habit
     */
    private fun addHabit(name: String, description: String, frequency: HabitFrequency) {
        val newHabit = Habit(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            frequency = frequency
        )
        
        dataManager.addHabit(newHabit)
        loadHabits()
        updateProgress()
        Toast.makeText(context, "Habit added successfully!", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Update an existing habit
     */
    private fun updateHabit(habit: Habit) {
        dataManager.updateHabit(habit)
        loadHabits()
        Toast.makeText(context, "Habit updated successfully!", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Delete a habit
     */
    private fun deleteHabit(habit: Habit) {
        dataManager.deleteHabit(habit.id)
        loadHabits()
        updateProgress()
        Toast.makeText(context, "Habit deleted successfully!", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Toggle habit completion status
     */
    private fun toggleHabitCompletion(habit: Habit, isCompleted: Boolean) {
        val today = DateUtils.getCurrentDateString()
        
        if (isCompleted) {
            val completion = HabitCompletion(
                habitId = habit.id,
                date = today
            )
            dataManager.addHabitCompletion(completion)
        } else {
            dataManager.removeHabitCompletion(habit.id, today)
        }
        
        updateProgress()

        // Also update the homescreen widget to reflect latest progress
        try {
            val appWidgetManager = android.appwidget.AppWidgetManager.getInstance(requireContext())
            val widgetProvider = android.content.ComponentName(requireContext(), com.example.habittrackerapplication.widget.HabbitWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetProvider)
            for (appWidgetId in appWidgetIds) {
                com.example.habittrackerapplication.widget.HabbitWidgetProvider.updateAppWidget(requireContext(), appWidgetManager, appWidgetId)
            }
        } catch (_: Exception) {
            // Best-effort widget refresh; ignore errors
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
