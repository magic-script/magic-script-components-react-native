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
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.reactlibrary.R

class CustomSpinner @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val START_ANGLE = 270F
        private const val STROKE_SIZE_TO_HEIGHT_RATIO = 0.035F
    }

    /**
     * Progress (from 0 to 1)
     */
    var value: Float = 0F
        set(value) {
            field = value
            invalidate()
        }

    private val spinnerRect = RectF()

    init {
        // disabling hardware acceleration to make blur effect working
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, R.color.color_spinner)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val innerStrokeWidth = STROKE_SIZE_TO_HEIGHT_RATIO * height
        val innerBlurRadius = innerStrokeWidth / 2
        val outStrokeWidth = innerStrokeWidth / 2
        val outBlurRadius = innerStrokeWidth + innerBlurRadius

        // drawing inner circle
        paint.strokeWidth = innerStrokeWidth
        paint.maskFilter = BlurMaskFilter(innerBlurRadius, BlurMaskFilter.Blur.NORMAL)
        paint.shader = RadialGradient(
                width / 2F,
                height / 2F,
                height / 2F,
                intArrayOf(paint.color, paint.color, Color.TRANSPARENT),
                floatArrayOf(0F, 0.85F, 1F),
                Shader.TileMode.MIRROR
        )

        spinnerRect.left =
                innerStrokeWidth / 2 + innerBlurRadius + outStrokeWidth + outBlurRadius
        spinnerRect.top =
                innerStrokeWidth / 2 + innerBlurRadius + outStrokeWidth + outBlurRadius
        spinnerRect.right =
                width.toFloat() - innerStrokeWidth / 2 - innerBlurRadius - outStrokeWidth - outBlurRadius
        spinnerRect.bottom =
                height.toFloat() - innerStrokeWidth / 2 - innerBlurRadius - outStrokeWidth - outBlurRadius

        canvas.drawArc(spinnerRect, START_ANGLE, value * 360F, false, paint)

        // drawing out circle (with stronger blur effect)
        paint.strokeWidth = outStrokeWidth
        paint.maskFilter = BlurMaskFilter(outBlurRadius, BlurMaskFilter.Blur.NORMAL)

        spinnerRect.left = spinnerRect.left - outStrokeWidth / 2 - outBlurRadius
        spinnerRect.top = spinnerRect.top - outStrokeWidth / 2 - outBlurRadius
        spinnerRect.right = spinnerRect.right + outStrokeWidth / 2 + outBlurRadius
        spinnerRect.bottom = spinnerRect.bottom + outStrokeWidth / 2 + outBlurRadius

        canvas.drawArc(spinnerRect, START_ANGLE, value * 360F, false, paint)
    }

}
