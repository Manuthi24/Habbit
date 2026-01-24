package com.example.habittrackerapplication.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.adapters.MoodEntriesAdapter
import com.example.habittrackerapplication.data.DataManager
import com.example.habittrackerapplication.databinding.DialogAddMoodBinding
import com.example.habittrackerapplication.databinding.FragmentMoodBinding
import com.example.habittrackerapplication.model.MoodEntry
import com.example.habittrackerapplication.model.DateUtils
import java.util.UUID

/**
 * Fragment for mood journal functionality
 * Allows users to log their mood with emojis and notes
 */
class MoodFragment : Fragment() {
    
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dataManager: DataManager
    private lateinit var moodAdapter: MoodEntriesAdapter
    private val moodEntries = mutableListOf<MoodEntry>()
    
    // Available mood emojis
    private val moodEmojis = listOf(
        "😊", "😄", "😍", "🥰", "😎", // Happy moods
        "😐", "😑", "😶", "🤔", "😕", // Neutral moods
        "😢", "😭", "😔", "😞", "😟", // Sad moods
        "😠", "😡", "🤬", "😤", "😖"  // Angry moods
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dataManager = DataManager(requireContext())
        setupRecyclerView()
        setupClickListeners()
        loadMoodEntries()
    }
    
    /**
     * Setup RecyclerView for mood entries
     */
    private fun setupRecyclerView() {
        moodAdapter = MoodEntriesAdapter(
            moodEntries = moodEntries,
            onMoodEdit = { moodEntry ->
                showEditMoodDialog(moodEntry)
            },
            onMoodDelete = { moodEntry ->
                showDeleteMoodDialog(moodEntry)
            },
            onMoodShare = { moodEntry ->
                shareMoodEntry(moodEntry)
            }
        )
        
        binding.recyclerViewMoods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodAdapter
        }
    }
    
    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        binding.fabAddMood.setOnClickListener {
            showAddMoodDialog()
        }
        
        binding.buttonViewChart.setOnClickListener {
            // Navigate to mood chart fragment using Navigation Component
            findNavController().navigate(R.id.moodChartFragment)
        }
    }
    
    /**
     * Load mood entries from data manager
     */
    private fun loadMoodEntries() {
        moodEntries.clear()
        moodEntries.addAll(dataManager.getMoodEntries().sortedByDescending { it.timestamp })
        moodAdapter.notifyDataSetChanged()
        
        // Show empty state if no mood entries
        if (moodEntries.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewMoods.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewMoods.visibility = View.VISIBLE
        }
    }
    
    /**
     * Show dialog to add a new mood entry
     */
    private fun showAddMoodDialog(moodEntry: MoodEntry? = null) {
        val dialogBinding = DialogAddMoodBinding.inflate(layoutInflater)
        
        // Setup emoji grid first to ensure children exist before preselecting
        setupEmojiGrid(dialogBinding)

        // Pre-fill fields if editing (after grid is built)
        moodEntry?.let {
            dialogBinding.editTextNote.setText(it.note)
            val emojiIndex = moodEmojis.indexOf(it.emoji)
            if (emojiIndex >= 0 && emojiIndex < dialogBinding.gridEmojis.childCount) {
                dialogBinding.gridEmojis.getChildAt(emojiIndex)?.isSelected = true
            }
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (moodEntry == null) getString(R.string.add_mood) else "Edit Mood")
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                var selectedEmoji = getSelectedEmoji(dialogBinding)
                val note = dialogBinding.editTextNote.text.toString().trim()
                
                // If editing and no new emoji selected, keep previous emoji
                if (moodEntry != null && selectedEmoji.isEmpty()) {
                    selectedEmoji = moodEntry.emoji
                }

                if (selectedEmoji.isNotEmpty()) {
                    if (moodEntry == null) {
                        addMoodEntry(selectedEmoji, note)
                    } else {
                        updateMoodEntry(moodEntry.copy(emoji = selectedEmoji, note = note))
                    }
                } else {
                    Toast.makeText(context, "Please select an emoji", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        
        dialog.show()
    }
    
    /**
     * Setup emoji selection grid
     */
    private fun setupEmojiGrid(dialogBinding: DialogAddMoodBinding) {
        // Clear existing views
        dialogBinding.gridEmojis.removeAllViews()
        
        moodEmojis.forEach { emoji ->
            val emojiView = LayoutInflater.from(context)
                .inflate(R.layout.item_emoji, dialogBinding.gridEmojis, false)
            
            val emojiText = emojiView.findViewById<android.widget.TextView>(R.id.textEmoji)
            emojiText.text = emoji
            
            emojiView.setOnClickListener {
                // Deselect all other emojis
                for (i in 0 until dialogBinding.gridEmojis.childCount) {
                    dialogBinding.gridEmojis.getChildAt(i).isSelected = false
                }
                // Select clicked emoji
                emojiView.isSelected = true
            }
            
            dialogBinding.gridEmojis.addView(emojiView)
        }
    }
    
    /**
     * Get selected emoji from the grid
     */
    private fun getSelectedEmoji(dialogBinding: DialogAddMoodBinding): String {
        for (i in 0 until dialogBinding.gridEmojis.childCount) {
            val child = dialogBinding.gridEmojis.getChildAt(i)
            if (child.isSelected) {
                val emojiText = child.findViewById<android.widget.TextView>(R.id.textEmoji)
                return emojiText.text.toString()
            }
        }
        return ""
    }
    
    /**
     * Show dialog to edit a mood entry
     */
    private fun showEditMoodDialog(moodEntry: MoodEntry) {
        showAddMoodDialog(moodEntry)
    }
    
    /**
     * Show dialog to confirm mood entry deletion
     */
    private fun showDeleteMoodDialog(moodEntry: MoodEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this mood entry?")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteMoodEntry(moodEntry)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    /**
     * Add a new mood entry
     */
    private fun addMoodEntry(emoji: String, note: String) {
        val newMoodEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            emoji = emoji,
            note = note
        )
        
        dataManager.addMoodEntry(newMoodEntry)
        loadMoodEntries()
        Toast.makeText(context, "Mood logged successfully!", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Update an existing mood entry
     */
    private fun updateMoodEntry(moodEntry: MoodEntry) {
        dataManager.updateMoodEntry(moodEntry)
        loadMoodEntries()
        Toast.makeText(context, "Mood updated successfully!", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Delete a mood entry
     */
    private fun deleteMoodEntry(moodEntry: MoodEntry) {
        dataManager.deleteMoodEntry(moodEntry.id)
        loadMoodEntries()
        Toast.makeText(context, "Mood entry deleted successfully!", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Share mood entry
     */
    private fun shareMoodEntry(moodEntry: MoodEntry) {
        val shareText = "I'm feeling ${moodEntry.emoji} today! ${moodEntry.note}"
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share your mood"))
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
