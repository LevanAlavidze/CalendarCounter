package com.example.testforcalendarcounter.quote

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.testforcalendarcounter.data.quote.Quote

class MotivationalQuoteDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_QUOTE_TEXT = "quote_text"
        private const val ARG_QUOTE_AUTHOR = "quote_author"

        fun newInstance(quote: Quote): MotivationalQuoteDialogFragment {
            val fragment = MotivationalQuoteDialogFragment()
            val args = Bundle().apply {
                putString(ARG_QUOTE_TEXT, quote.text)
                putString(ARG_QUOTE_AUTHOR, quote.author)
            }
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * Call this method to update the currently visible quote.
     */
    fun updateQuote(quote: Quote) {
        val text = quote.text
        val author = quote.author
        val fullText = "$text\n\n- $author"
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
        (dialog as? AlertDialog)?.setMessage(spannable)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val quoteText = requireArguments().getString(ARG_QUOTE_TEXT) ?: ""
        val quoteAuthor = requireArguments().getString(ARG_QUOTE_AUTHOR) ?: ""
        val fullText = "$quoteText\n\n- $quoteAuthor"
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

        return AlertDialog.Builder(requireContext())
            .setMessage(spannable)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("Share") { _, _ -> shareQuote(fullText) }
            .setNegativeButton("Disable") { dialog, _ ->
                disableShakeNotifications()
                dialog.dismiss()
            }
            .create().apply {
                // Allow dismiss by tapping outside.
                setCanceledOnTouchOutside(true)
            }
    }

    private fun shareQuote(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun disableShakeNotifications() {
        // Update your SharedPreferences or settings to disable shake notifications.
    }
}
