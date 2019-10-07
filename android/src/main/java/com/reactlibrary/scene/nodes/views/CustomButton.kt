/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactlibrary.scene.nodes.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import kotlin.math.min

class CustomButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // border width = shorter button dimension * borderWidthFactor
    private val borderWidthFactor = 0.07F

    var text = ""
        set(value) {
            field = value
            invalidate()
            requestLayout() // need to measure the view
        }

    var roundnessFactor = 1F // from 0 to 1 (fully rounded)
        set(value) {
            field = value
            invalidate()
        }

    private var textPaddingHorizontal = 0

    private var textPaddingVertical = 0

    // private val maxStrokeSize = Utils.metersToPx(0.005F, context).toFloat()

    private val textPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
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
        val defaultWidth = textBounds.width() + 2 * textPaddingHorizontal
        val defaultHeight = textBounds.height() + 2 * textPaddingVertical

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
        // logMessage("CustomButton onDraw")
        val centerX = width / 2
        val centerY = height / 2

        // draw background
        val strokeSize = borderWidthFactor * min(width, height)
        val radius = (height.toFloat() - strokeSize) / 2 * roundnessFactor

        bgPaint.strokeWidth = strokeSize
        canvas.drawRoundRect(
                strokeSize / 2,
                strokeSize / 2,
                width.toFloat() - strokeSize / 2,
                height.toFloat() - strokeSize / 2,
                radius,
                radius,
                bgPaint
        )

        // draw text
        val textX = centerX - textBounds.exactCenterX()
        val textY = centerY - textBounds.exactCenterY()
        canvas.drawText(text, textX, textY, textPaint)
    }

    fun setTextSize(textSizePx: Float) {
        textPaint.textSize = textSizePx
        invalidate()
        requestLayout() // need to measure the view
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
        invalidate()
    }

    fun setTextPadding(paddingHorizontalPx: Int, paddingVerticalPx: Int) {
        textPaddingHorizontal = paddingHorizontalPx
        textPaddingVertical = paddingVerticalPx
        invalidate()
        requestLayout() // need to measure the view
    }

    fun setTypeface(typeface: Typeface) {
        textPaint.typeface = typeface
    }

}
