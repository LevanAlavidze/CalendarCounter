package com.example.testforcalendarcounter.tapGame.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.testforcalendarcounter.databinding.FragmentTapGameBinding
import kotlin.random.Random

class TapGameFragment : Fragment() {

    private var _binding: FragmentTapGameBinding? = null
    private val binding get() = _binding!!

    private var score = 0
    private lateinit var countDownTimer: CountDownTimer
    private val gameDuration = 30000L // 30 seconds

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTapGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateScore()
        startGameTimer()

        // When the cigarette image is tapped, update score and animate.
        binding.ivCigarette.setOnClickListener {
            score++
            updateScore()
            animateAndMoveCigarette()
        }

        // Once layout is ready, position the cigarette randomly.
        binding.root.post {
            moveCigaretteToRandomPosition()
        }
    }

    /**
     * Update the score TextView.
     */
    private fun updateScore() {
        binding.tvScore.text = "Score: $score"
    }

    /**
     * Animate a "pop" with scale and rotation, then reposition the cigarette.
     */
    private fun animateAndMoveCigarette() {
        binding.ivCigarette.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .rotationBy(360f)
            .setDuration(150)
            .withEndAction {
                binding.ivCigarette.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .withEndAction {
                        moveCigaretteToRandomPosition()
                    }
                    .start()
            }
            .start()
    }

    /**
     * Reposition the cigarette ImageView to a random location within the parent,
     * ensuring it stays fully visible by using a safe margin.
     */
    private fun moveCigaretteToRandomPosition() {
        val safeMargin = 16 // pixels
        val containerWidth = binding.root.width
        val containerHeight = binding.root.height
        val imageWidth = binding.ivCigarette.width
        val imageHeight = binding.ivCigarette.height

        if (containerWidth == 0 || containerHeight == 0 || imageWidth == 0 || imageHeight == 0) return

        // Define safe bounds
        val leftBound = safeMargin
        val topBound = safeMargin
        val rightBound = containerWidth - imageWidth - safeMargin
        val bottomBound = containerHeight - imageHeight - safeMargin

        if (rightBound <= leftBound || bottomBound <= topBound) return

        val randomX = Random.nextInt(leftBound, rightBound + 1)
        val randomY = Random.nextInt(topBound, bottomBound + 1)

        // Set the absolute x and y position so the image stays fully inside.
        binding.ivCigarette.x = randomX.toFloat()
        binding.ivCigarette.y = randomY.toFloat()
    }

    /**
     * Starts a countdown timer that updates every second. When the time is up,
     * the game resets (score resets and timer restarts).
     */
    private fun startGameTimer() {
        countDownTimer = object : CountDownTimer(gameDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = "Time: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                binding.tvTimer.text = "Time: 0"
                // Game over: reset score and restart timer (or you can show a dialog here)
                score = 0
                updateScore()
                startGameTimer()
            }
        }
        countDownTimer.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
        _binding = null
    }
}
