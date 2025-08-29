package com.example.testforcalendarcounter.main.fragment

import android.animation.Animator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testforcalendarcounter.adapter.LastTenAdapter
import com.example.testforcalendarcounter.databinding.FragmentSmokeBinding
import com.example.testforcalendarcounter.enums.MoodLevel
import com.example.testforcalendarcounter.main.viewmodel.SmokeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SmokeFragment : Fragment() {

    private var _binding: FragmentSmokeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SmokeViewModel by viewModels()
    private lateinit var lastTenAdapter: LastTenAdapter
    private lateinit var dateChangeReceiver: BroadcastReceiver

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

        // RecyclerView setup
        lastTenAdapter = LastTenAdapter { entry -> viewModel.deleteCigarette(entry) }
        binding.lastTenRecyclerView.apply {
            adapter = lastTenAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        viewModel.lifetimeNotSmoked.observe(viewLifecycleOwner) { total ->
            binding.tvNotSmokedValue.text = total.toString()
        }


        // Lottie (add cigarette)
        binding.cigaretteAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                binding.cigaretteAnimationView.progress = 0f
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        binding.cigaretteAnimationView.setOnClickListener {
            binding.cigaretteAnimationView.playAnimation()
            viewModel.addCigarette()
        }

        // Heart meter: trim to fill-only frames (0–60) and drive progress
        binding.heartMeterView.addLottieOnCompositionLoadedListener { _ ->
            binding.heartMeterView.setMinAndMaxFrame(0, 300)
        }

        // whenever percent changes:
        viewModel.dailySavingsPercent.observe(viewLifecycleOwner) { usedPercent ->
            // remaining fill (1 = full heart at 0% used, 0 = empty at 100% used)
            val remainingRatio = (100 - usedPercent) / 100f
            binding.heartMeterView.progress = remainingRatio.coerceIn(0f, 1f)
        }

        /*binding.arcIndicator.setProgress(0)*/

        viewModel.dailySavings.observe(viewLifecycleOwner) { saved ->
            binding.tvArcSavings.text = "You've Saved: %.2f".format(saved)
        }

     /*   viewModel.dailySavingsPercent.observe(viewLifecycleOwner) { percent ->
            binding.arcIndicator.setProgress(percent)
            binding.arcIndicator.post {
                val (dx, dy) = binding.arcIndicator.calculateIndicatorPosition(
                    binding.ivArcSmiley.width,
                    binding.ivArcSmiley.height
                )
                binding.ivArcSmiley.x = binding.arcIndicator.x + dx
                binding.ivArcSmiley.y = binding.arcIndicator.y +dy
            }
        }

        viewModel.moodLevel.observe(viewLifecycleOwner) { mood ->
            val resId = when (mood) {
                MoodLevel.VERY_GOOD -> com.example.testforcalendarcounter.R.drawable.smile_very_good
                MoodLevel.GOOD      -> com.example.testforcalendarcounter.R.drawable.smile_good
                MoodLevel.FINE      -> com.example.testforcalendarcounter.R.drawable.smile_fine
                MoodLevel.BAD       -> com.example.testforcalendarcounter.R.drawable.smile_bad
                MoodLevel.VERY_BAD  -> com.example.testforcalendarcounter.R.drawable.smile_very_bad
            }
            binding.ivArcSmiley.setImageResource(resId)
        }*/


        // Observe counts
        viewModel.dayCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.dailyCountTextView.text = "Daily: $count"
        }
        viewModel.weekCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.weeklyCountTextView.text = "Weekly: $count"
        }
        viewModel.monthCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.monthlyCountTextView.text = "Monthly: $count"
        }

        // Observe costs (currency + cost flows)
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



        // Last ten cigarettes list
        viewModel.lastTenCigarettes.observe(viewLifecycleOwner) { entries ->
            lastTenAdapter.setData(entries)
        }

        // Timer
        viewModel.timer.observe(viewLifecycleOwner) { time ->
            binding.tvTimer.text = time
        }
    }

    override fun onStart() {
        super.onStart()
        dateChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_DATE_CHANGED) {
                    viewLifecycleOwner.lifecycleScope.launch{viewModel
                        viewModel.refreshCounts()
                        viewModel.refreshLifetimeNotSmoked()
                    }
                }
            }
        }
        requireContext().registerReceiver(
            dateChangeReceiver,
            IntentFilter(Intent.ACTION_DATE_CHANGED)
        )
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(dateChangeReceiver)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.refreshCounts()
            viewModel.loadTimerState()
            viewModel.fetchCurrency()
            viewModel.refreshLifetimeNotSmoked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}