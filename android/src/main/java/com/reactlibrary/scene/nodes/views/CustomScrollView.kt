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
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat.getColor
import com.reactlibrary.R
import com.reactlibrary.utils.logMessage
import kotlinx.android.synthetic.main.scroll_view.view.*

class CustomScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var contentWidth = 0F
        set(value) {
            field = value
            this.h_bar.thumbSize = width() / value
        }

    var contentHeight = 0F

    // fun initScrollBars(){
    //     this.h_bar.isVertical = false
    // }

    // override fun onDraw(canvas: Canvas) {
    //     super.onDraw(canvas)
    //     logMessage("view")
    // }

    // override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int){
    //     logMessage(p1.toString() + " " + p2.toString() + " " + p3.toString() + " " + p4.toString())
    // }

    // private val backgroundSizeRatio = 0.66F

    // var isVertical = true

    // private var touchOffset = 0F

    // var onScrollChangeListener: ((on: Float) -> Unit)? = null

    // override fun onTouchEvent(event: MotionEvent): Boolean {
    //     val action = event.actionMasked
    //     if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_MOVE) {
    //         return false
    //     }

    //     val (begin, end) = thumbBounds()
    //     val thumbLength = end - begin
    //     val touchPos = getTouchPos(event)

    //     if (action == MotionEvent.ACTION_DOWN) {
    //         touchOffset = 0F
    //         val onThumb = inRange(touchPos, begin, end)
    //         if (onThumb) {
    //             val middle = begin + thumbLength / 2
    //             touchOffset = touchPos - middle
    //         }
    //     }

    //     val thumbTravel = length() - thumbLength
    //     thumbPosition = if (thumbTravel > 0F) {
    //         (touchPos - touchOffset - thumbLength / 2) / thumbTravel
    //     } else {
    //         0F
    //     }
    //     onScrollChangeListener?.invoke(thumbPosition)
    //     return true
    // }

    // private fun updateBarThumbSize(bar: CustomScrollBar, viewSize: Float, childSize: Float){
    //     bar.thumbSize = 
    // }

    override fun setLayoutParams(params: ViewGroup.LayoutParams ){
        super.setLayoutParams(params)
        // this.h_bar.isVertical = false
        this.h_bar.thumbSize = width() / contentWidth
        this.v_bar.thumbSize = width() / contentHeight
    }

    private fun height(): Float {
        return this.layoutParams.height.toFloat()
    }

    private fun width(): Float {
        return this.layoutParams?.width?.toFloat() ?: 0F
    }
}
