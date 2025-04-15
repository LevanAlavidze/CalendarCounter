package com.example.testforcalendarcounter.quiz.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.testforcalendarcounter.databinding.FragmentQuizBinding
import com.example.testforcalendarcounter.quiz.viewmodel.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe changes to the current question index.
        quizViewModel.currentIndex.observe(viewLifecycleOwner, Observer { index ->
            updateQuestion(index)
        })

        // Observe if the quiz has finished.
        quizViewModel.quizFinished.observe(viewLifecycleOwner, Observer { finished ->
            if (finished) {
                showQuizResult()
            }
        })

        binding.btnSubmit.setOnClickListener {
            val selectedId = binding.rgChoices.checkedRadioButtonId
            if (selectedId != -1) {
                val radioButton = binding.rgChoices.findViewById<RadioButton>(selectedId)
                val selectedIndex = binding.rgChoices.indexOfChild(radioButton)
                quizViewModel.submitAnswer(selectedIndex)
                // Clear selection for next question.
                binding.rgChoices.clearCheck()
            }
        }
    }

    private fun updateQuestion(index: Int) {
        val questions = quizViewModel.questions.value
        if (questions != null && questions.isNotEmpty() && index < questions.size) {
            val currentQuestion = questions[index]
            binding.tvQuestion.text = currentQuestion.question

            // Clear previous choices.
            binding.rgChoices.removeAllViews()
            // Populate RadioGroup with choices.
            currentQuestion.choices.forEachIndexed { i, choice ->
                val radioButton = RadioButton(context).apply {
                    text = choice
                    textSize = 18f
                    id = View.generateViewId()
                }
                binding.rgChoices.addView(radioButton)
            }
        }
    }

    private fun showQuizResult() {
        val finalScore = quizViewModel.score.value ?: 0
        val totalQuestions = quizViewModel.questions.value?.size ?: 0

        AlertDialog.Builder(requireContext())
            .setTitle("Quiz Completed!")
            .setMessage("You scored $finalScore out of $totalQuestions.")
            .setPositiveButton("Restart Quiz") { dialog, _ ->
                dialog.dismiss()
                quizViewModel.resetQuiz()
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
                // Optionally, navigate back or to another fragment.
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}