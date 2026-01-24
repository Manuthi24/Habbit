package com.example.habittrackerapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habittrackerapplication.data.DataManager
import com.example.habittrackerapplication.databinding.ItemHabitBinding
import com.example.habittrackerapplication.model.Habit
import com.example.habittrackerapplication.model.DateUtils

/**
 * Adapter for displaying habits in RecyclerView
 */
class HabitsAdapter(
    private val habits: List<Habit>,
    private val onHabitToggle: (Habit, Boolean) -> Unit,
    private val onHabitEdit: (Habit) -> Unit,
    private val onHabitDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {
    
    private lateinit var dataManager: DataManager
    
    /**
     * ViewHolder for habit items
     */
    class HabitViewHolder(private val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            habit: Habit,
            isCompleted: Boolean,
            onToggle: (Boolean) -> Unit,
            onEdit: () -> Unit,
            onDelete: () -> Unit
        ) {
            binding.textHabitName.text = habit.name
            binding.textHabitDescription.text = habit.description
            binding.textHabitFrequency.text = habit.frequency.name.lowercase()
            binding.checkboxCompleted.isChecked = isCompleted
            
            // Set completion status styling
            if (isCompleted) {
                binding.cardHabit.setCardBackgroundColor(binding.root.context.getColor(com.example.habittrackerapplication.R.color.success))
                binding.textHabitName.setTextColor(binding.root.context.getColor(com.example.habittrackerapplication.R.color.white))
                binding.textHabitDescription.setTextColor(binding.root.context.getColor(com.example.habittrackerapplication.R.color.white))
                binding.textHabitFrequency.setTextColor(binding.root.context.getColor(com.example.habittrackerapplication.R.color.white))
            } else {
                binding.cardHabit.setCardBackgroundColor(binding.root.context.getColor(com.example.habittrackerapplication.R.color.card_background))
                binding.textHabitName.setTextColor(binding.root.context.getColor(com.example.habittrackerapplication.R.color.text_primary))
                binding.textHabitDescription.setTextColor(binding.root.context.getColor(com.example.habittrackerapplication.R.color.text_secondary))
                binding.textHabitFrequency.setTextColor(binding.root.context.getColor(com.example.habittrackerapplication.R.color.text_secondary))
            }
            
            // Set click listeners
            binding.checkboxCompleted.setOnCheckedChangeListener { _, isChecked ->
                onToggle(isChecked)
            }
            
            binding.buttonEdit.setOnClickListener {
                onEdit()
            }
            
            binding.buttonDelete.setOnClickListener {
                onDelete()
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        val today = DateUtils.getCurrentDateString()
        val isCompleted = dataManager.isHabitCompleted(habit.id, today)
        
        holder.bind(
            habit = habit,
            isCompleted = isCompleted,
            onToggle = { isChecked -> onHabitToggle(habit, isChecked) },
            onEdit = { onHabitEdit(habit) },
            onDelete = { onHabitDelete(habit) }
        )
    }
    
    override fun getItemCount(): Int = habits.size
    
    /**
     * Set data manager for checking completion status
     */
    fun setDataManager(dataManager: DataManager) {
        this.dataManager = dataManager
    }
}
