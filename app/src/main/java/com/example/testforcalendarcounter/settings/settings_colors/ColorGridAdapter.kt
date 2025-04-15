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
        val view = convertView ?: View(context)

        // 100dp in px
        val sizePx = (100 * context.resources.displayMetrics.density).toInt()
        view.layoutParams = ViewGroup.LayoutParams(sizePx, sizePx)

        val item = items[position]
        try {
            // If it's a valid color hex
            view.setBackgroundColor(Color.parseColor(item))
        } catch (e: Exception) {
            // Otherwise treat it as a drawable name
            val resId = context.resources.getIdentifier(item, "drawable", context.packageName)
            if (resId != 0) {
                val drawable: Drawable? = ContextCompat.getDrawable(context, resId)
                view.background = drawable
            } else {
                // Fallback
                view.setBackgroundColor(Color.LTGRAY)
            }
        }
        return view
    }
}
