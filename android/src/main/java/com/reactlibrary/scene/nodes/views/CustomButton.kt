package com.reactlibrary.scene.nodes.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.support.v4.content.ContextCompat.getColor
import android.util.AttributeSet
import android.view.View
import com.reactlibrary.utils.Utils
import kotlin.math.min

// TODO wrap_content (default size support based on text size)
// https://stackoverflow.com/questions/12266899/onmeasure-custom-view-explanation
class CustomButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text = "Button"

    private var roundnessFactor = 1F // from 0 to 1 (fully rounded)

    private val textBounds = Rect()

    private val maxStrokeSize = Utils.metersToPx(0.005F, context).toFloat()

    private val textPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = getColor(context, android.R.color.white)
        textSize = 12F
    }

    private val bgPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, android.R.color.white)
        // maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2
        val centerY = height / 2

        // draw background
        val radius = height.toFloat() / 2 * roundnessFactor
        val strokeSize = min(height / 14F, maxStrokeSize)
        bgPaint.strokeWidth = strokeSize
        canvas.drawRoundRect(0F + strokeSize / 2, 0F + strokeSize / 2, width.toFloat() - strokeSize, height.toFloat() - strokeSize, radius, radius, bgPaint)

        // draw text
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val textX = centerX - textBounds.exactCenterX()
        val textY = centerY - textBounds.exactCenterY()
        canvas.drawText(text, textX, textY, textPaint)
    }

    fun setText(text: String) {
        this.text = text
        invalidate()
    }

    fun setRoundnessFactor(factor: Float) {
        roundnessFactor = factor
        invalidate()
    }

    fun setTextSize(sizePx: Float) {
        textPaint.textSize = sizePx
        invalidate()
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
        invalidate()
    }


}
