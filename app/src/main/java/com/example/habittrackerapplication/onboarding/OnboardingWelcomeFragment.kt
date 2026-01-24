package com.example.habittrackerapplication.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.databinding.FragmentOnboardingWelcomeBinding

/**
 * First onboarding screen - Welcome screen
 */
class OnboardingWelcomeFragment : Fragment() {
    
    private var _binding: FragmentOnboardingWelcomeBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.titleText.text = getString(R.string.onboarding_welcome_title)
        binding.descriptionText.text = getString(R.string.onboarding_welcome_description)
        
        // Add some animation or styling here if needed
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
