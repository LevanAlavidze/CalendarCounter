package com.example.testforcalendarcounter.settings.settings_colors

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat

class ColorGridAdapter(
    private val context: Context,
    private val items: List<String>
) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: View(context).apply {
            val sizeDp = 80
            val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
            layoutParams = ViewGroup.LayoutParams(sizePx, sizePx)
            background = ContextCompat.getDrawable(context, com.example.testforcalendarcounter.R.drawable.color_preview_background)
        }

        /*val sizeDp = 80
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt()
        view.layoutParams = ViewGroup.LayoutParams(sizePx, sizePx)*/

        val item = items[position]
        try {
            // Attempt to treat the item as a color hex code.
            view.setBackgroundColor(Color.parseColor(item))
        } catch (e: Exception) {
            // Otherwise, treat it as a drawable resource name.
            val resId = context.resources.getIdentifier(item, "drawable", context.packageName)
            if (resId != 0) {
                val drawable: Drawable? = ContextCompat.getDrawable(context, resId)
                view.background = drawable
            } else {
                view.setBackgroundColor(Color.LTGRAY)
            }
        }
        return view
    }
}
