package com.example.testforcalendarcounter.breath.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.testforcalendarcounter.databinding.FragmentBreathingTimerBinding

class BreathingTimerFragment : Fragment() {

    private var _binding: FragmentBreathingTimerBinding? = null
    private val binding get() = _binding!!

    private var breathingAnimatorSet: AnimatorSet? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreathingTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startBreathingAnimation()
    }

    /**
     * Starts a breathing cycle that repeats indefinitely.
     * The cycle consists of:
     *   - Inhale: Bubble expands (4 seconds)
     *   - Hold: Bubble remains full size (2 seconds)
     *   - Exhale: Bubble contracts (4 seconds)
     *   - Hold: Bubble remains contracted (2 seconds)
     */
    private fun startBreathingAnimation() {
        // If the binding is null, do not proceed.
        if (_binding == null) return

        val inhaleDuration = 6000L
        val holdAfterInhaleDuration = 3000L
        val exhaleDuration = 6000L
        val holdAfterExhaleDuration = 3000L

        // Inhale: Expand the bubble from scale 0.5 to 1.0.
        val inhaleX = ObjectAnimator.ofFloat(binding.bubbleView, "scaleX", 0.5f, 1f).setDuration(inhaleDuration)
        val inhaleY = ObjectAnimator.ofFloat(binding.bubbleView, "scaleY", 0.5f, 1f).setDuration(inhaleDuration)
        inhaleX.addUpdateListener { _binding?.tvInstruction?.text = "Inhale with nose" }
        val inhaleSet = AnimatorSet().apply {
            playTogether(inhaleX, inhaleY)
        }

        // Hold after inhale: Delay (no change in scale).
        val holdAfterInhale = ObjectAnimator.ofFloat(binding.bubbleView, "scaleX", 1f, 1f).setDuration(holdAfterInhaleDuration)
        holdAfterInhale.addUpdateListener { _binding?.tvInstruction?.text = "Hold" }

        // Exhale: Contract the bubble from scale 1.0 to 0.5.
        val exhaleX = ObjectAnimator.ofFloat(binding.bubbleView, "scaleX", 1f, 0.5f).setDuration(exhaleDuration)
        val exhaleY = ObjectAnimator.ofFloat(binding.bubbleView, "scaleY", 1f, 0.5f).setDuration(exhaleDuration)
        exhaleX.addUpdateListener { _binding?.tvInstruction?.text = "Exhale with mouth" }
        val exhaleSet = AnimatorSet().apply {
            playTogether(exhaleX, exhaleY)
        }

        // Hold after exhale: Delay (no change in scale).
        val holdAfterExhale = ObjectAnimator.ofFloat(binding.bubbleView, "scaleX", 0.5f, 0.5f).setDuration(holdAfterExhaleDuration)
        holdAfterExhale.addUpdateListener { _binding?.tvInstruction?.text = "Hold" }

        // Combine all animations sequentially.
        breathingAnimatorSet = AnimatorSet().apply {
            playSequentially(inhaleSet, holdAfterInhale, exhaleSet, holdAfterExhale)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Only restart if the binding is still valid.
                    if (_binding != null) {
                        startBreathingAnimation()
                    }
                }
            })
        }
        breathingAnimatorSet?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        breathingAnimatorSet?.cancel()
        _binding = null
    }
}
