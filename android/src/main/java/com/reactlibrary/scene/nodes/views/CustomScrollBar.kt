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
import androidx.core.content.ContextCompat.getColor
import android.util.AttributeSet
import android.view.View
import com.reactlibrary.R

class CustomScrollBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // init {
    //     this.setOnScrollChangeListener(::scrollCallback)
    // }

    var thumbPosition = 0F
        set(value) {
            field = value
            invalidate()
        }

    var thumbSize = 0F
        set(value) {
            field = value
            invalidate()
        }

    // fun scrollCallback(v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
    //     throw Exception("Hi There!")
    //     // thumbPosition = 1F//width.toFloat() / scrollX.toFloat() 
    //     // super.refreshImage()
    // }

    // fun clickCallback(v: View) {
    //     throw Exception("Hi There!")
    //     thumbPosition += 0.1F
    //     // super.refreshImage()
    // }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawThumb(canvas)
    }

    private fun drawBackground(canvas: Canvas){

        val bgPaint = Paint(ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = getColor(context, R.color.scroll_bar_background)
        }

        val radius = height.toFloat() / 2
        canvas.drawRoundRect(
                0F,
                0F,
                width.toFloat(),
                height.toFloat(),
                radius,
                radius,
                bgPaint
        )
    }

    private fun drawThumb(canvas: Canvas){

        val thumbPaint = Paint(ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = getColor(context, R.color.scroll_bar_thumb)
        }

        val width = this.width.toFloat()
        val height = this.height.toFloat()

        thumbSize = thumbSize.coerceIn(0F,1F)
        thumbPosition = thumbPosition.coerceIn(0F,1F)

        val radius = height / 2
        val length = (width * thumbSize).coerceAtLeast(height)
        val offset = (width - length) * (1F - thumbPosition)

        canvas.drawRoundRect(
                offset,
                0F,
                offset + length,
                height,
                radius,
                radius,
                thumbPaint
        )
    }
}
