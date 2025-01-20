package com.example.testforcalendarcounter.main.fragment

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.testforcalendarcounter.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testforcalendarcounter.adapter.LastTenAdapter
import com.example.testforcalendarcounter.databinding.FragmentSmokeBinding
import com.example.testforcalendarcounter.enums.MoodLevel
import com.example.testforcalendarcounter.main.viewmodel.SmokeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmokeFragment : Fragment() {

    private var _binding: FragmentSmokeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SmokeViewModel by viewModels()
    private lateinit var lastTenAdapter: LastTenAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmokeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Recycler
        lastTenAdapter = LastTenAdapter { entry -> viewModel.deleteCigarette(entry) }
        binding.lastTenRecyclerView.apply {
            adapter = lastTenAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Configure Lottie animation (add listener to reset progress at end)
        binding.cigaretteAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) { /* no-op */ }
            override fun onAnimationEnd(animation: Animator) {
                // Reset animation at the end so it doesn't repeat
                binding.cigaretteAnimationView.progress = 0f
            }
            override fun onAnimationCancel(animation: Animator) { /* no-op */ }
            override fun onAnimationRepeat(animation: Animator) { /* no-op */ }
        })


        // Add click listener to play animation + add a cigarette
        binding.cigaretteAnimationView.setOnClickListener {
            binding.cigaretteAnimationView.playAnimation()
            viewModel.addCigarette()
        }

        viewModel.moodLevel.observe(viewLifecycleOwner){mood ->
            displayMood(mood)
            val segment = moodToProgress(mood)
            binding.moodProgressBar.progress = segment
        }

        // Observe ViewModel data
        // 1) Counts
        viewModel.dayCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.dailyCountTextView.text = "Daily: $count"
        }
        viewModel.weekCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.weeklyCountTextView.text = "Weekly: $count"
        }
        viewModel.monthCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.monthlyCountTextView.text = "Monthly: $count"
        }

        // 2) Costs (wrapped in currency.observe to pick up changes)
        viewModel.currency.observe(viewLifecycleOwner) { currency ->
            viewModel.dailyCost.observe(viewLifecycleOwner) { (count, cost) ->
                binding.dailyCostTextView.text = "Cost: %.2f $currency".format(cost)
            }
            viewModel.weeklyCost.observe(viewLifecycleOwner) { (count, cost) ->
                binding.weeklyCostTextView.text = "Cost: %.2f $currency".format(cost)
            }
            viewModel.monthlyCost.observe(viewLifecycleOwner) { (count, cost) ->
                binding.monthlyCostTextView.text = "Cost: %.2f $currency".format(cost)
            }
        }

        viewModel.dailySavings.observe(viewLifecycleOwner) {moneySaved ->
            binding.tvSavings.text = "You've saved: %.2f".format(moneySaved)
        }

        // 3) Last ten cigarettes
        viewModel.lastTenCigarettes.observe(viewLifecycleOwner) { entries ->
            lastTenAdapter.setData(entries)
        }

        // 4) Timer
        viewModel.timer.observe(viewLifecycleOwner) { time ->
            binding.tvTimer.text = time
        }
    }

    private fun displayMood(mood: MoodLevel) {
        when (mood) {
            MoodLevel.VERY_GOOD -> {
                binding.ivSmiley.setImageResource(R.drawable.smile_very_good)
                binding.tvSmileyLabel.text = "Very Good"
            }
            MoodLevel.GOOD -> {
                binding.ivSmiley.setImageResource(R.drawable.smile_good)
                binding.tvSmileyLabel.text = "Good"
            }
            MoodLevel.FINE -> {
                binding.ivSmiley.setImageResource(R.drawable.smile_fine)
                binding.tvSmileyLabel.text = "Fine"
            }
            MoodLevel.BAD -> {
                binding.ivSmiley.setImageResource(R.drawable.smile_bad)
                binding.tvSmileyLabel.text = "Bad"
            }
            MoodLevel.VERY_BAD -> {
                binding.ivSmiley.setImageResource(R.drawable.smile_very_bad)
                binding.tvSmileyLabel.text = "Very Bad"
            }
        }
    }

    private fun moodToProgress(mood: MoodLevel): Int {
        return when (mood) {
            MoodLevel.VERY_GOOD -> 0
            MoodLevel.GOOD -> 1
            MoodLevel.FINE -> 2
            MoodLevel.BAD -> 3
            MoodLevel.VERY_BAD -> 4
        }
    }




    override fun onResume() {
        super.onResume()
        // Update data as needed each time we resume
        viewModel.refreshCounts()
        viewModel.loadTimerState()
        viewModel.fetchCurrency()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
