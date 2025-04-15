package com.example.testforcalendarcounter.repository.quiz




import android.content.Context
import com.example.testforcalendarcounter.data.quize.QuizQuestion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val context: Context // Provided by Hilt as the application context
) : QuizRepository {
    override fun loadQuizQuestions(): List<QuizQuestion> {
        val jsonString = context.assets.open("quiz_questions.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<QuizQuestion>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }
}