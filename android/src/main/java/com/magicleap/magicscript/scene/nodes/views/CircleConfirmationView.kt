/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes.views

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.magicleap.magicscript.R

class CircleConfirmationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val START_ANGLE = 270F // degrees
        private const val STROKE_SIZE_TO_HEIGHT_RATIO = 0.03F
    }

    /**
     * Progress (from 0 to 1)
     */
    var value: Float = 0F
        set(value) {
            field = value.coerceIn(0F, 1F)
            invalidate()
        }

    private val circleBounds = RectF()

    private val backgroundPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, R.color.circle_confirmation_background)
    }

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, R.color.circle_confirmation_color)
    }

    private val gradientColors = context.resources.getStringArray(R.array.circe_confirmation_colors)
        .map { Color.parseColor(it) }
        .toIntArray()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val strokeWidth = STROKE_SIZE_TO_HEIGHT_RATIO * h
        circleBounds.left = strokeWidth / 2
        circleBounds.top = strokeWidth / 2
        circleBounds.right = w - strokeWidth / 2
        circleBounds.bottom = h - strokeWidth / 2

        backgroundPaint.strokeWidth = strokeWidth
        paint.strokeWidth = strokeWidth

        val gradientMatrix = Matrix()
        val cX = w / 2f
        val cY = h / 2f
        gradientMatrix.postRotate(-90f, cX, cY)

        val gradient = SweepGradient(w / 2f, h / 2f, gradientColors, null)
        gradient.setLocalMatrix(gradientMatrix)
        paint.shader = gradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw background
        canvas.drawArc(circleBounds, 0F, 360F, false, backgroundPaint)

        // draw progress
        val sweepAngle = value * 360F
        canvas.drawArc(circleBounds, START_ANGLE, sweepAngle, false, paint)
    }

}
