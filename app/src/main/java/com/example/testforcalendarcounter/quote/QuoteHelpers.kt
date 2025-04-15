package com.example.testforcalendarcounter.quote

import androidx.fragment.app.FragmentActivity
import com.example.testforcalendarcounter.data.quote.motivationalQuotes

fun FragmentActivity.showRandomMotivationalQuoteDialog() {
    val randomQuote = motivationalQuotes.random()
    val tag = "motivational_quote"
    // Check if a MotivationalQuoteDialogFragment is already shown.
    val existingDialog = supportFragmentManager.findFragmentByTag(tag)
    if (existingDialog is MotivationalQuoteDialogFragment) {
        existingDialog.updateQuote(randomQuote)
    } else {
        MotivationalQuoteDialogFragment.newInstance(randomQuote)
            .show(supportFragmentManager, tag)
    }
}
