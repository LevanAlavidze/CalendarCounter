package com.example.testforcalendarcounter.tapGame.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.testforcalendarcounter.databinding.FragmentTapGameBinding
import kotlin.math.max
import kotlin.random.Random

class TapGameFragment : Fragment() {

    private var _binding: FragmentTapGameBinding? = null
    private val binding get() = _binding!!

    private var score = 0
    private var comboCount = 0
    private var comboMultiplier = 1
    private lateinit var countDownTimer: CountDownTimer
    private val gameDuration = 30000L // 30 seconds

    // Base animation duration in ms. It decreases as the score increases.
    private var animationDuration = 150L
    private val minAnimationDuration = 80L

    // Control distractor visibility duration.
    private var distractorVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTapGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateScore()
        startGameTimer()

        // Handle cigarette tap: update combo & score, animate, then maybe show distractor.
        binding.ivCigarette.setOnClickListener {
            comboCount++
            // Increase multiplier every 5 taps.
            comboMultiplier = 1 + (comboCount / 5)
            score += comboMultiplier
            updateScore()

            // Progressive difficulty: reduce animation duration, not below the minimum.
            animationDuration = max(minAnimationDuration, animationDuration - 2)

            animateAndMoveCigarette()
            maybeShowDistractor()
        }

        // Handle distractor tap: penalize and reset combo.
        binding.ivDistractor.setOnClickListener {
            score = max(0, score - 2)
            updateScore()
            resetCombo()
            binding.ivDistractor.visibility = View.GONE
            distractorVisible = false
        }

        // Position the cigarette (and optionally the distractor) once the layout is ready.
        binding.root.post {
            moveCigaretteToRandomPosition()
            maybeShowDistractor()
        }
    }

    /**
     * Updates the score TextView including combo multiplier info.
     */
    private fun updateScore() {
        binding.tvScore.text = "Score: $score (Combo x$comboMultiplier)"
    }

    /**
     * Animate a "pop" (scale and 360° rotation) then reposition the cigarette.
     */
    private fun animateAndMoveCigarette() {
        binding.ivCigarette.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .rotationBy(360f)
            .setDuration(animationDuration)
            .withEndAction {
                binding.ivCigarette.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(animationDuration)
                    .withEndAction {
                        moveCigaretteToRandomPosition()
                        maybeShowDistractor()
                    }
                    .start()
            }
            .start()
    }

    /**
     * Reposition the cigarette ImageView to a random location within safe bounds.
     * The safe area is below the bottom of the timer (tvTimer) plus a margin.
     */
    private fun moveCigaretteToRandomPosition() {
        val safeMargin = 16 // pixels
        val containerWidth = binding.root.width
        val containerHeight = binding.root.height
        val imageWidth = binding.ivCigarette.width
        val imageHeight = binding.ivCigarette.height

        if (containerWidth == 0 || containerHeight == 0 || imageWidth == 0 || imageHeight == 0) return

        // Top bound is just below the tvTimer.
        val topBound = binding.tvTimer.bottom + safeMargin
        val leftBound = safeMargin
        val rightBound = containerWidth - imageWidth - safeMargin
        val bottomBound = containerHeight - imageHeight - safeMargin

        if (rightBound <= leftBound || bottomBound <= topBound) return

        val randomX = Random.nextInt(leftBound, rightBound + 1)
        val randomY = Random.nextInt(topBound, bottomBound + 1)

        binding.ivCigarette.x = randomX.toFloat()
        binding.ivCigarette.y = randomY.toFloat()
    }

    /**
     * With 30% chance, show the distractor at a random position (using the same safe bounds as the cigarette).
     * Once shown, it stays visible for at least 1 second before hiding.
     */
    private fun maybeShowDistractor() {
        if (!distractorVisible && Random.nextFloat() < 0.3f) {
            moveDistractorToRandomPosition()
            binding.ivDistractor.visibility = View.VISIBLE
            distractorVisible = true

            // Fade-in animation for the distractor.
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.duration = 300
            binding.ivDistractor.startAnimation(fadeIn)

            // Keep the distractor visible for at least 1 second.
            binding.root.postDelayed({
                binding.ivDistractor.visibility = View.GONE
                distractorVisible = false
            }, 1000)
        }
    }

    /**
     * Reposition the distractor ImageView to a random location within safe bounds,
     * similar to moveCigaretteToRandomPosition().
     */
    private fun moveDistractorToRandomPosition() {
        val safeMargin = 16 // pixels
        val containerWidth = binding.root.width
        val containerHeight = binding.root.height
        val imageWidth = binding.ivDistractor.width
        val imageHeight = binding.ivDistractor.height

        if (containerWidth == 0 || containerHeight == 0 || imageWidth == 0 || imageHeight == 0) return

        val topBound = binding.tvTimer.bottom + safeMargin
        val leftBound = safeMargin
        val rightBound = containerWidth - imageWidth - safeMargin
        val bottomBound = containerHeight - imageHeight - safeMargin

        if (rightBound <= leftBound || bottomBound <= topBound) return

        val randomX = Random.nextInt(leftBound, rightBound + 1)
        val randomY = Random.nextInt(topBound, bottomBound + 1)

        binding.ivDistractor.x = randomX.toFloat()
        binding.ivDistractor.y = randomY.toFloat()
    }

    /**
     * Starts a 30-second countdown timer. On every tick, updates the timer display.
     * When finished, shows an AlertDialog with the final score and options to try again or exit.
     */
    private fun startGameTimer() {
        countDownTimer = object : CountDownTimer(gameDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = "Time: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                binding.tvTimer.text = "Time: 0"
                showGameOverDialog()
            }
        }
        countDownTimer.start()
    }

    /**
     * Display an AlertDialog at the end of the game showing the final score and asking if the user wants to try again.
     */
    private fun showGameOverDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Time's Up!")
            .setMessage("Your score is $score.\nDo you want to try again?")
            .setPositiveButton("Try Again") { dialog, _ ->
                dialog.dismiss()
                resetGame()
            }
            .setNegativeButton("Exit") { dialog, _ ->
                dialog.dismiss()
                requireActivity().onBackPressed()
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Reset the game state and restart the timer.
     */
    private fun resetGame() {
        score = 0
        resetCombo()
        updateScore()
        animationDuration = 150L
        startGameTimer()
        moveCigaretteToRandomPosition()
        binding.ivDistractor.visibility = View.GONE
        distractorVisible = false
    }

    /**
     * Reset combo count and multiplier.
     */
    private fun resetCombo() {
        comboCount = 0
        comboMultiplier = 1
        updateScore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
        _binding = null
    }
}
