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
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.reactlibrary.R

class CircleConfirmationView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val START_ANGLE = 270F // degrees
        private const val STROKE_SIZE_TO_HEIGHT_RATIO = 0.04F
    }

    /**
     * Progress (from 0 to 1)
     */
    var value: Float = 0F
        set(value) {
            field = value.coerceIn(0F, 1F)
            invalidate()
        }

    private val circleRect = RectF()

    private val backgroundPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, R.color.circle_confirmation_background)
    }

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, R.color.circle_confirmation_color)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val strokeWidth = STROKE_SIZE_TO_HEIGHT_RATIO * height
        backgroundPaint.strokeWidth = strokeWidth
        paint.strokeWidth = strokeWidth

        circleRect.left = strokeWidth / 2
        circleRect.top = strokeWidth / 2
        circleRect.right = width.toFloat() - strokeWidth / 2
        circleRect.bottom = height.toFloat() - strokeWidth / 2

        // draw background
        canvas.drawArc(circleRect, 0F, 360F, false, backgroundPaint)

        // draw progress
        val sweepAngle = value * 360F
        canvas.drawArc(circleRect, START_ANGLE, sweepAngle, false, paint)
    }

}
