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
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Shader
import androidx.core.content.ContextCompat.getColor
import android.util.AttributeSet
import android.view.View
import com.reactlibrary.R

class CustomProgressBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var value = 0F
        set(value) {
            field = value
            invalidate()
        }

    var min = 0F
        set(value) {
            field = value
            invalidate()
        }

    var max = 1F
        set(value) {
            field = value
            invalidate()
        }

    var beginColor: Int = R.color.progress_bar_foreground
        set(value) {
            field = value
            invalidate()
        }

    var endColor: Int = R.color.progress_bar_foreground
        set(value) {
            field = value
            invalidate()
        }

    private val bgPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = getColor(context, R.color.progress_bar_background)
    }

    private val progressPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val roundnessFactor = 1F // 1 fully rounded corners

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw background
        val radius = height.toFloat() / 2 * roundnessFactor
        canvas.drawRoundRect(
                0F,
                0F,
                width.toFloat(),
                height.toFloat(),
                radius,
                radius,
                bgPaint
        )

        // draw progress
        if (value > max) {
            value = max
        }
        val progress = if (max - min > 0) (value - min) / (max - min) else 0F
        val progressWidth = progress * width.toFloat()

        progressPaint.shader = LinearGradient(
                0F,
                0F,
                progressWidth,
                0F,
                beginColor,
                endColor,
                Shader.TileMode.CLAMP
        )

        canvas.drawRoundRect(
                0F,
                0F,
                progressWidth,
                height.toFloat(),
                radius,
                radius,
                progressPaint
        )

    }

}
