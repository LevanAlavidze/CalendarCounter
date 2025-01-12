package com.example.testforcalendarcounter.main.fragment

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testforcalendarcounter.main.viewmodel.SmokeViewModel
import com.example.testforcalendarcounter.adapter.LastTenAdapter
import com.example.testforcalendarcounter.databinding.FragmentSmokeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmokeFragment : Fragment() {

    private var _binding: FragmentSmokeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SmokeViewModel by viewModels()
    private lateinit var lastTenAdapter: LastTenAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

        // Add Cigarette
        // Add Lottie Animation Listener
        binding.cigaretteAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Optional: do something when animation starts
            }

            override fun onAnimationEnd(animation: Animator) {
                // Reset animation at the end
                binding.cigaretteAnimationView.progress = 0f
            }

            override fun onAnimationCancel(animation: Animator) {
                // Optional: handle cancel
            }

            override fun onAnimationRepeat(animation: Animator) {
                // Optional: handle repeat
            }
        })

        // Add click listener
        binding.cigaretteAnimationView.setOnClickListener {
            // Play the Lottie animation
            binding.cigaretteAnimationView.playAnimation()

            // Add cigarette to the database via ViewModel
            viewModel.addCigarette()
        }

        // Observers
        viewModel.dayCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.dailyCountTextView.text = "Daily: $count"
        }

        // Observe costs
        viewModel.currency.observe(viewLifecycleOwner) { currency ->
            viewModel.dailyCost.observe(viewLifecycleOwner) { (_, cost) ->
                binding.dailyCostTextView.text = "Cost: %.2f $currency".format(cost)
            }
            viewModel.weeklyCost.observe(viewLifecycleOwner) { (_, cost) ->
                binding.weeklyCostTextView.text = "Cost: %.2f $currency".format(cost)
            }
            viewModel.monthlyCost.observe(viewLifecycleOwner) { (_, cost) ->
                binding.monthlyCostTextView.text = "Cost: %.2f $currency".format(cost)
            }
        }

        viewModel.weekCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.weeklyCountTextView.text = "Weekly: $count"
        }

        viewModel.monthCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.monthlyCountTextView.text = "Monthly: $count"
        }

        viewModel.lastTenCigarettes.observe(viewLifecycleOwner) { entries ->
            lastTenAdapter.setData(entries)
        }

        viewModel.timer.observe(viewLifecycleOwner) { time ->
            binding.tvTimer.text = time
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCounts()
        viewModel.loadTimerState()
        viewModel.calculateDailyCost()
        viewModel.calculateWeeklyCost()
        viewModel.calculateMonthlyCost()
        viewModel.fetchCurrency()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
