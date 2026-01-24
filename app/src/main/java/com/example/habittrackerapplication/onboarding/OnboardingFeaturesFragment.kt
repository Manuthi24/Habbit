package com.example.habittrackerapplication.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.databinding.FragmentOnboardingFeaturesBinding

/**
 * Second onboarding screen - Features overview
 */
class OnboardingFeaturesFragment : Fragment() {
    
    private var _binding: FragmentOnboardingFeaturesBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingFeaturesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.titleText.text = getString(R.string.onboarding_features_title)
        binding.descriptionText.text = getString(R.string.onboarding_features_description)
        
        // Set feature icons and descriptions
        binding.feature1Icon.text = "📝"
        binding.feature1Title.text = "Track Habits"
        binding.feature1Description.text = "Monitor your daily wellness routines"
        
        binding.feature2Icon.text = "😊"
        binding.feature2Title.text = "Mood Journal"
        binding.feature2Description.text = "Log your emotions with emojis"
        
        binding.feature3Icon.text = "💧"
        binding.feature3Title.text = "Stay Hydrated"
        binding.feature3Description.text = "Get reminders to drink water"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
