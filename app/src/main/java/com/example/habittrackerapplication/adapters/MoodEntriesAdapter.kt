package com.example.habittrackerapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.databinding.ItemMoodEntryBinding
import com.example.habittrackerapplication.model.MoodEntry
import com.example.habittrackerapplication.model.DateUtils

/**
 * Adapter for displaying mood entries in RecyclerView
 */
class MoodEntriesAdapter(
    private val moodEntries: List<MoodEntry>,
    private val onMoodEdit: (MoodEntry) -> Unit,
    private val onMoodDelete: (MoodEntry) -> Unit,
    private val onMoodShare: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodEntriesAdapter.MoodEntryViewHolder>() {
    
    /**
     * ViewHolder for mood entry items
     */
    class MoodEntryViewHolder(private val binding: ItemMoodEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            moodEntry: MoodEntry,
            onEdit: () -> Unit,
            onDelete: () -> Unit,
            onShare: () -> Unit
        ) {
            binding.textEmoji.text = moodEntry.emoji
            binding.textNote.text = moodEntry.note
            binding.textDate.text = DateUtils.formatTimestamp(moodEntry.timestamp)
            binding.textTime.text = DateUtils.formatTime(moodEntry.timestamp)
            
            // Set click listeners
            binding.buttonEdit.setOnClickListener { onEdit() }
            binding.buttonDelete.setOnClickListener { onDelete() }
            binding.buttonShare.setOnClickListener { onShare() }
            
            // Set mood-specific background color
            val moodColor = when {
                moodEntry.emoji.contains("😊") || moodEntry.emoji.contains("😄") || 
                moodEntry.emoji.contains("😍") || moodEntry.emoji.contains("🥰") -> 
                    binding.root.context.getColor(R.color.mood_happy)
                moodEntry.emoji.contains("😐") || moodEntry.emoji.contains("😑") || 
                moodEntry.emoji.contains("😶") || moodEntry.emoji.contains("🤔") -> 
                    binding.root.context.getColor(R.color.mood_neutral)
                moodEntry.emoji.contains("😢") || moodEntry.emoji.contains("😭") || 
                moodEntry.emoji.contains("😔") || moodEntry.emoji.contains("😞") -> 
                    binding.root.context.getColor(R.color.mood_sad)
                moodEntry.emoji.contains("😠") || moodEntry.emoji.contains("😡") || 
                moodEntry.emoji.contains("🤬") || moodEntry.emoji.contains("😤") -> 
                    binding.root.context.getColor(R.color.mood_angry)
                else -> binding.root.context.getColor(R.color.mood_good)
            }
            
            binding.cardMood.setCardBackgroundColor(moodColor)
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodEntryViewHolder {
        val binding = ItemMoodEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodEntryViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: MoodEntryViewHolder, position: Int) {
        val moodEntry = moodEntries[position]
        
        holder.bind(
            moodEntry = moodEntry,
            onEdit = { onMoodEdit(moodEntry) },
            onDelete = { onMoodDelete(moodEntry) },
            onShare = { onMoodShare(moodEntry) }
        )
    }
    
    override fun getItemCount(): Int = moodEntries.size
}
