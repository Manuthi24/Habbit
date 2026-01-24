package com.example.habittrackerapplication.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.databinding.FragmentOnboardingGetStartedBinding

/**
 * Third onboarding screen - Get started screen
 */
class OnboardingGetStartedFragment : Fragment() {
    
    private var _binding: FragmentOnboardingGetStartedBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingGetStartedBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.titleText.text = getString(R.string.onboarding_get_started_title)
        binding.descriptionText.text = getString(R.string.onboarding_get_started_description)
        
        // Add motivational content
        binding.motivationText.text = "Start your wellness journey today!"
        binding.tipText.text = "💡 Tip: Start with 2-3 simple habits and build from there"

        // Button action handled by activity-level Next/Get Started button
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
