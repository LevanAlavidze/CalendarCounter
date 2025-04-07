package com.example.testforcalendarcounter.data.quote

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

data class Quote(val text: String, val author: String)

val motivationalQuotes = listOf(
    Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
    Quote("Whether you think you can or you think you can’t, you’re right.", "Henry Ford"),
    Quote("The only limit to our realization of tomorrow is our doubts of today.", "Franklin D. Roosevelt"),
    Quote("It does not matter how slowly you go as long as you do not stop.", "Confucius"),
    Quote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"),
    Quote("I have not failed. I've just found 10,000 ways that won't work.", "Thomas Edison"),
    Quote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
    Quote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
    Quote("The best way to predict the future is to create it.", "Peter Drucker"),
    Quote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson")
)

private var currentQuoteDialog: AlertDialog? = null

fun Context.showMotivationalQuoteDialog(quote: Quote) {
    val fullText = "${quote.text}\n\n- ${quote.author}"
    val spannable = SpannableString(fullText)
    val authorStart = fullText.indexOf("-")
    if (authorStart != -1) {
        spannable.setSpan(
            ForegroundColorSpan(Color.GRAY),
            authorStart,
            fullText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    if (currentQuoteDialog?.isShowing == true) {
        currentQuoteDialog?.setMessage(spannable)
    } else {
        currentQuoteDialog = AlertDialog.Builder(this)
            .setMessage(spannable)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
                currentQuoteDialog = null
            }
            .setNeutralButton("Share") { dialog, _ ->
                shareQuote(fullText)
            }
            .setNegativeButton("Disable") { dialog, _ ->
                disableShakeNotifications()
                dialog.dismiss()
                currentQuoteDialog = null
            }
            .setCancelable(false)
            .show()
    }
}

private fun Context.shareQuote(text: String) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    startActivity(Intent.createChooser(shareIntent, "Share via"))
}

private fun disableShakeNotifications() {
    // Here you can update your settings (e.g., SharedPreferences) to disable shake notifications.
}

fun Context.showRandomMotivationalQuote() {
    val randomQuote = motivationalQuotes.random()
    showMotivationalQuoteDialog(randomQuote)
}
