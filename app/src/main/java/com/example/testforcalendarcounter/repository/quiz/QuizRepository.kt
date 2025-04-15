package com.example.testforcalendarcounter.repository.quiz

import com.example.testforcalendarcounter.data.quize.QuizQuestion

interface QuizRepository {
    fun loadQuizQuestions(): List<QuizQuestion>
}