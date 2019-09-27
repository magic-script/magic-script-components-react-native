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
import android.graphics.RectF
import android.view.MotionEvent
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.reactlibrary.R
import com.reactlibrary.utils.logMessage

class CustomScrollBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundSizeRatio = 0.66F

    private var width = 0F
        get() = this.getWidth().toFloat()

    private var height = 0F
        get() = this.getHeight().toFloat()

    var thumbPosition = 0F
        set(value) {
            field = value.coerceIn(0F, 1F)
            invalidate()
        }

    var thumbSize = 0F
        set(value) {
            field = value.coerceIn(0F, 1F)
            invalidate()
        }

    var isVertical = true

    var touchOffset = 0F

    fun onTouchCallback(event: MotionEvent) {
        val action = event.getActionMasked()

        // Clear touch offset.
        if (action == MotionEvent.ACTION_UP ){
            touchOffset = 0F
        }

        if (action == MotionEvent.ACTION_DOWN ){
        }

        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_MOVE){
            return
        }

        if (isVertical){
            touchVertical(event)
        } else {
            touchHorizontal(event)
        }
    }

    private fun touchVertical(event: MotionEvent) {
        val length = (width * thumbSize).coerceAtLeast(height)
        thumbPosition = (event.getX() - length / 2 ) / (width - length)
    }

    private fun touchHorizontal(event: MotionEvent) {
        val (begin, end) = thumbPositionHorizontal()
        val length = end - begin
        var touchPos = event.getX()
        
        val action = event.getActionMasked()
        val onThumb = inRange(touchPos, begin, end)
        if (action == MotionEvent.ACTION_DOWN && onThumb){
            val middle = begin + length / 2
            touchOffset = -(touchPos - middle)
        }
            
        thumbPosition = (touchPos + touchOffset - length / 2 ) / (width - length)
    }

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

        if (isVertical){
            drawBackgroundVertical(canvas, bgPaint)
        } else {
            drawBackgroundHorizontal(canvas, bgPaint)
        }
    }

    private fun drawBackgroundVertical(canvas: Canvas, paint: Paint){
        val bgWidth = width * backgroundSizeRatio
        val padding = (width - bgWidth) / 2 
        val bbox = RectF(padding, 0F, bgWidth + padding, height)
        val radius = bgWidth / 2
        canvas.drawRoundRect(bbox, radius, radius, paint)
    }

    private fun drawBackgroundHorizontal(canvas: Canvas, paint: Paint){
        val bgHeight = height * backgroundSizeRatio
        val padding = (height - bgHeight) / 2 
        val bbox = RectF(0F, padding, width, bgHeight + padding)
        val radius = bgHeight / 2
        canvas.drawRoundRect(bbox, radius, radius, paint)
    }

    private fun drawThumb(canvas: Canvas){
        val thumbPaint = Paint(ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = getColor(context, R.color.scroll_bar_thumb)
        }

        if (isVertical){
            drawThumbVertical(canvas, thumbPaint)
        } else {
            drawThumbHorizontal(canvas, thumbPaint)
        }
    }

    private fun drawThumbVertical(canvas: Canvas, paint: Paint){
        val (begin, end) = thumbPositionVertical()
        val bbox = RectF(0F, begin, width, end)
        val radius = height / 2
        canvas.drawRoundRect(bbox, radius, radius, paint)
    }

    private fun drawThumbHorizontal(canvas: Canvas, paint: Paint){
        val (begin, end) = thumbPositionHorizontal()
        val bbox = RectF(begin, 0F, end, height)
        val radius = width / 2
        canvas.drawRoundRect(bbox, radius, radius, paint)
    }

    private fun thumbPositionVertical(): Pair<Float, Float>{
        val length = (height * thumbSize).coerceAtLeast(width)
        val offset = (height - length) * thumbPosition
        return Pair(offset, offset + length)
    }

    private fun thumbPositionHorizontal(): Pair<Float, Float>{
        val length = (width * thumbSize).coerceAtLeast(height)
        val offset = (width - length) * thumbPosition
        return Pair(offset, offset + length)
    }

    private fun inRange(pos: Float, begin: Float, end: Float): Boolean{
        // Following line breaks Kotlin syntax highlight 
        // in Visual Studio Code, so it's placed at the 
        // end of file :)
        return (pos >= begin) && (pos <=end) 
    }
}
