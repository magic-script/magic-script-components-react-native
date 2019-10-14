/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.reactlibrary.scene.nodes.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.reactlibrary.R

class CustomSlider @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var onScrollChangeListener: ((on: Float) -> Unit)? = null

    var min = 0F
    var max = 1F

    var value = 0F
        set(value) {
            field = value
            invalidate()
        }

    private val backgroundHeightRatio = 0.5F // relative to thumb (view) height

    private val thumbSize
        get() = height.toFloat()

    private val bgHeight
        get() = height * backgroundHeightRatio

    private val bgPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.slider_inactive_part)
    }

    private val thumbPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.slider_active_part)
    }

    // reusing instance in onDraw() method for better performance
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawThumb(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_MOVE) {
            return false
        }
        if (width < thumbSize) {
            return false
        }

        // touch position normalized to touchable area
        val touchPos = event.x.coerceIn(thumbSize / 2, width.toFloat() - thumbSize / 2)
        val offset = (touchPos - thumbSize / 2) / (width - thumbSize) // [0;1]
        value = min + offset * (max - min)
        onScrollChangeListener?.invoke(value)

        return true
    }

    private fun drawBackground(canvas: Canvas) {
        val thumbPos = getThumbCenterX()

        // left side background
        rect.left = thumbSize / 2
        rect.right = thumbPos
        rect.top = (thumbSize - bgHeight) / 2
        rect.bottom = rect.top + bgHeight

        val radius = bgHeight / 2
        canvas.drawRoundRect(rect, radius, radius, thumbPaint)

        // right side background
        rect.left = rect.right
        rect.right = width - thumbSize / 2
        canvas.drawRoundRect(rect, radius, radius, bgPaint)
    }

    private fun drawThumb(canvas: Canvas) {
        val radius = thumbSize / 2
        canvas.drawCircle(getThumbCenterX(), thumbSize / 2, radius, thumbPaint)
    }

    private fun getThumbCenterX(): Float {
        val normalizedValue = if (max - min > 0) (value - min) / (max - min) else 0F // [0;1]
        return thumbSize / 2 + normalizedValue * (width - thumbSize)
    }
}