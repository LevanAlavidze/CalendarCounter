package com.example.testforcalendarcounter.quiz.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testforcalendarcounter.data.quize.QuizQuestion
import com.example.testforcalendarcounter.repository.quiz.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _questions = MutableLiveData<List<QuizQuestion>>()
    val questions: LiveData<List<QuizQuestion>> get() = _questions

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> get() = _currentIndex

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

    private val _quizFinished = MutableLiveData(false)
    val quizFinished: LiveData<Boolean> get() = _quizFinished

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        _questions.value = quizRepository.loadQuizQuestions()
    }

    /**
     * Submit an answer: if correct, increase the score.
     * Move to the next question or mark the quiz as finished.
     */
    fun submitAnswer(selectedIndex: Int) {
        val list = _questions.value ?: return
        val currentIndexValue = _currentIndex.value ?: 0
        val currentQ = list[currentIndexValue]
        if (selectedIndex == currentQ.correctAnswer) {
            _score.value = (_score.value ?: 0) + 1
        }
        if (currentIndexValue < list.size - 1) {
            _currentIndex.value = currentIndexValue + 1
        } else {
            _quizFinished.value = true
        }
    }

    fun resetQuiz() {
        _currentIndex.value = 0
        _score.value = 0
        _quizFinished.value = false
    }
}