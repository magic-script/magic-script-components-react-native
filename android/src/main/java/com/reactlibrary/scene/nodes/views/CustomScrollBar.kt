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
import android.util.AttributeSet
import android.view.MotionEvent
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

    private var touchOffset = 0F

    var onScrollChangeListener: ((on: Float) -> Unit)? = null
        set(value){
            field = value
            logMessage("onScrollChangeListener set")
        }

    init {
        val orientationStr = attrs?.getAttributeValue(null, "orientation")
        logMessage(orientationStr + " ")
        if (orientationStr == "horizontal"){
            isVertical = false
        }
        logMessage("init")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_MOVE) {
            return false
        }

        val (begin, end) = thumbBounds()
        val thumbLength = end - begin
        val touchPos = getTouchPos(event)

        if (action == MotionEvent.ACTION_DOWN) {
            touchOffset = 0F
            val onThumb = inRange(touchPos, begin, end)
            if (onThumb) {
                val middle = begin + thumbLength / 2
                touchOffset = touchPos - middle
            }
        }

        val thumbTravel = length() - thumbLength
        thumbPosition = if (thumbTravel > 0F) {
            (touchPos - touchOffset - thumbLength / 2) / thumbTravel
        } else {
            0F
        }
        logMessage(" should call " + (onScrollChangeListener == null).toString())
        onScrollChangeListener?.invoke(thumbPosition)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawThumb(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        val bgPaint = Paint(ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = getColor(context, R.color.scroll_bar_background)
        }

        val bgWidth = width() * backgroundSizeRatio
        val padding = (width() - bgWidth) / 2
        val bbox = makeBbox(0F, length(), padding, bgWidth + padding)
        val radius = bgWidth / 2
        canvas.drawRoundRect(bbox, radius, radius, bgPaint)
    }

    private fun drawThumb(canvas: Canvas) {
        val thumbPaint = Paint(ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = getColor(context, R.color.scroll_bar_thumb)
        }

        val (begin, end) = thumbBounds()
        val bbox = makeBbox(begin, end, 0F, width())
        val radius = width() / 2
        canvas.drawRoundRect(bbox, radius, radius, thumbPaint)
    }

    private fun thumbBounds(): Pair<Float, Float> {
        val thumbLength = (length() * thumbSize).coerceAtLeast(width())
        val offset = (length() - thumbLength) * thumbPosition
        return Pair(offset, offset + thumbLength)
    }

    private fun length(): Float {
        return if (isVertical) {
            this.height.toFloat()
        } else {
            this.width.toFloat()
        }
    }

    private fun width(): Float {
        return if (isVertical) {
            this.width.toFloat()
        } else {
            this.height.toFloat()
        }
    }

    private fun getTouchPos(event: MotionEvent): Float {
        return if (isVertical) {
            event.getY()
        } else {
            event.getX()
        }
    }

    private fun makeBbox(lBegin: Float, lEnd: Float, wBegin: Float, wEnd: Float): RectF {
        return if (isVertical) {
            RectF(wBegin, lBegin, wEnd, lEnd)
        } else {
            RectF(lBegin, wBegin, lEnd, wEnd)
        }
    }

    private fun inRange(pos: Float, begin: Float, end: Float): Boolean {
        // Following line breaks Kotlin syntax highlight 
        // in Visual Studio Code, so it's placed at the 
        // end of file :)
        return (pos >= begin) && (pos <= end)
    }
}
