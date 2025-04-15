package com.example.testforcalendarcounter.data.quize

data class QuizQuestion(
    val question: String,
    val choices: List<String>,
    val correctAnswer: Int
)
