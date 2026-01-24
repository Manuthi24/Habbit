package com.example.habittrackerapplication.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.habittrackerapplication.MainActivity
import com.example.habittrackerapplication.R
import com.example.habittrackerapplication.data.DataManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Onboarding activity that introduces users to the Habbit app
 * Uses ViewPager2 to display multiple onboarding screens
 */
class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var nextButton: MaterialButton
    private lateinit var skipButton: MaterialButton
    private lateinit var dataManager: DataManager
    
    private val onboardingFragments = listOf(
        OnboardingWelcomeFragment(),
        OnboardingFeaturesFragment(),
        OnboardingGetStartedFragment()
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        dataManager = DataManager(this)
        
        initViews()
        setupViewPager()
        setupButtons()
    }
    
    /**
     * Initialize all views
     */
    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        nextButton = findViewById(R.id.nextButton)
        skipButton = findViewById(R.id.skipButton)
    }
    
    /**
     * Setup ViewPager2 with onboarding fragments
     */
    private fun setupViewPager() {
        val adapter = OnboardingPagerAdapter(this, onboardingFragments)
        viewPager.adapter = adapter
        
        // Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { _, _ ->
            // Tab configuration is handled by the fragments
        }.attach()
        
        // Update button text based on current page
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonText(position)
            }
        })
    }
    
    /**
     * Setup button click listeners
     */
    private fun setupButtons() {
        nextButton.setOnClickListener {
            if (viewPager.currentItem < onboardingFragments.size - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                completeOnboarding()
            }
        }
        
        skipButton.setOnClickListener {
            completeOnboarding()
        }
    }
    
    /**
     * Update button text based on current page
     */
    private fun updateButtonText(position: Int) {
        when (position) {
            onboardingFragments.size - 1 -> {
                nextButton.text = getString(R.string.get_started)
                skipButton.visibility = android.view.View.GONE
                // Center the bottom button on last page
                val params = (nextButton.layoutParams as android.widget.LinearLayout.LayoutParams)
                params.width = 0
                params.weight = 2f
                nextButton.layoutParams = params
            }
            else -> {
                nextButton.text = getString(R.string.next)
                skipButton.visibility = android.view.View.VISIBLE
                // Restore split weights for two buttons
                val params = (nextButton.layoutParams as android.widget.LinearLayout.LayoutParams)
                params.width = 0
                params.weight = 1f
                nextButton.layoutParams = params
            }
        }
    }
    
    /**
     * Complete onboarding and navigate to main activity
     */
    private fun completeOnboarding() {
        // Mark first launch completed so onboarding won't show again
        dataManager.setFirstLaunchCompleted()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("from_onboarding", true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
