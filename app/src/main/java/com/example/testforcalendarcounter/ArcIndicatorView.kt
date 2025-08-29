package com.example.testforcalendarcounter

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ArcIndicatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {
    // configurable via XML
    private var strokeWidthPx = 8f
    private var trackColor = Color.LTGRAY
    private var indicatorColor = Color.GREEN
    private var startAngle = 150f    // where the arc begins
    private var sweepAngle = 240f    // how big the total arc is

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var progressPercent = 0  // 0–100

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.ArcIndicatorView, 0, 0)
            strokeWidthPx = a.getDimension(R.styleable.ArcIndicatorView_strokeWidth, strokeWidthPx)
            trackColor = a.getColor(R.styleable.ArcIndicatorView_trackColor, trackColor)
            indicatorColor = a.getColor(R.styleable.ArcIndicatorView_indicatorColor, indicatorColor)
            startAngle = a.getFloat(R.styleable.ArcIndicatorView_startAngle, startAngle)
            sweepAngle = a.getFloat(R.styleable.ArcIndicatorView_sweepAngle, sweepAngle)
            a.recycle()
        }
        trackPaint.strokeWidth = strokeWidthPx
        trackPaint.color = trackColor
        indicatorPaint.strokeWidth = strokeWidthPx
        indicatorPaint.color = indicatorColor
    }

    private val arcRect = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Figure out the raw measured sizes
        val width  = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        // Pick the smaller to make a perfect square
        val size = min(width, height)
        // Create a new MeasureSpec that’s exactly that size
        val newSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        // Call super with the square spec for both dimensions
        super.onMeasure(newSpec, newSpec)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val pad = strokeWidthPx / 2
        arcRect.set(pad, pad, w - pad, h - pad)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 1) draw the background track
        canvas.drawArc(arcRect, startAngle, sweepAngle, false, trackPaint)
        // 2) draw the indicator arc
        val angle = sweepAngle * progressPercent / 100f
        canvas.drawArc(arcRect, startAngle, angle, false, indicatorPaint)
    }

    /** Call this to update the progress (0–100) and redraw. */
    fun setProgress(percent: Int) {
        progressPercent = percent.coerceIn(0, 100)
        invalidate()
    }

    /**
     * Returns the (x,y) of the current end-point of the arc,
     * in this view’s coordinate space, so you can place your ImageView.
     */
    fun calculateIndicatorPosition(iconWidth: Int, iconHeight: Int): Pair<Float,Float> {
        val angleDeg = startAngle + sweepAngle * progressPercent / 100f
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val cx = width / 2f
        val cy = height / 2f
        val radius = (min(width, height) - strokeWidthPx) / 2f
        val x = cx + radius * cos(angleRad) - iconWidth / 2f
        val y = cy + radius * sin(angleRad) - iconHeight / 2f
        return x.toFloat() to y.toFloat()
    }
}