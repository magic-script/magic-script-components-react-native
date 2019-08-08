package com.reactlibrary.scene.nodes.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.support.v4.content.ContextCompat.getColor
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.view.View
import com.reactlibrary.R
import com.reactlibrary.utils.Utils
import kotlin.math.min

class CustomButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text = "Button"

    private var roundnessFactor = 1F // from 0 to 1 (fully rounded)

    private val textPadding = Utils.metersToPx(0.01F, context)

    private val maxStrokeSize = Utils.metersToPx(0.005F, context).toFloat()

    private val textPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        typeface = ResourcesCompat.getFont(context, R.font.lomino_app_regular)
        color = getColor(context, android.R.color.white)
        textSize = 12F
    }

    private val bgPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, android.R.color.white)
        // maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
    }

    private val textBounds = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val defaultWidth = textBounds.width() + 2 * textPadding
        val defaultHeight = textBounds.height() + 2 * textPadding

        val width: Int = if (widthMode == MeasureSpec.EXACTLY) { // exact size
            widthSize
        } else { // WRAP_CONTENT
            defaultWidth
        }

        val height: Int = if (heightMode == MeasureSpec.EXACTLY) { // exact size
            heightSize
        } else { // WRAP_CONTENT
            defaultHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2
        val centerY = height / 2

        // draw background
        val radius = height.toFloat() / 2 * roundnessFactor
        val strokeSize = min(height / 14F, maxStrokeSize)
        bgPaint.strokeWidth = strokeSize
        canvas.drawRoundRect(
                0F + strokeSize / 2,
                0F + strokeSize / 2,
                width.toFloat() - strokeSize,
                height.toFloat() - strokeSize,
                radius,
                radius,
                bgPaint
        )

        // draw text
        val textX = centerX - textBounds.exactCenterX()
        val textY = centerY - textBounds.exactCenterY()
        canvas.drawText(text, textX, textY, textPaint)
    }

    fun setText(text: String) {
        this.text = text
        requestLayout() // need to measure the view
    }

    fun setRoundnessFactor(factor: Float) {
        roundnessFactor = factor
        invalidate()
    }

    fun setTextSize(sizePx: Float) {
        textPaint.textSize = sizePx
        requestLayout() // need to measure the view
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
        invalidate()
    }


}
