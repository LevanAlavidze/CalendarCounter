package com.example.testforcalendarcounter.settings.settings_colors

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

class ExpandedGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Make GridView expand fully within a ScrollView
        val expandedSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandedSpec)
        layoutParams.height = measuredHeight
    }
}
