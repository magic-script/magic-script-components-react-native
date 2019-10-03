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
            this.h_bar.thumbSize = viewWidth / contentWidth
        }

    var contentHeight = 0F
        set(value) {
            field = value
            this.v_bar.thumbSize = viewHeight / contentHeight
        }

    var viewWidth = 0F
        set(value) {
            field = value
            this.h_bar.thumbSize = viewWidth / contentWidth
        }

    var viewHeight = 0F
        set(value) {
            field = value
            this.v_bar.thumbSize = viewHeight / contentHeight
        }

    var offsetX = 0F
        set(value) {
            val maxOffset = (contentWidth - viewWidth).coerceAtLeast(0F)
            field = value.coerceIn(0F, maxOffset)
            this.h_bar.thumbPosition = if ( maxOffset > 0F) {
                offsetX / maxOffset
            } else {
                0F
            }
        }

    var offsetY = 0F
        set(value) {
            val maxOffset = (contentHeight - viewHeight).coerceAtLeast(0F)
            field = value.coerceIn(0F, maxOffset)
            this.v_bar.thumbPosition = if ( maxOffset > 0F) {
                offsetY / maxOffset
            } else {
                0F
            }
        }

    var previousTouchX = 0F
    var previousTouchY = 0F

    // var onScrollChangeListener: ((on: Float) -> Unit)? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_MOVE) {
            return false
        }

        val x = event.getX()
        val y = event.getY()

        if (action == MotionEvent.ACTION_MOVE) {
            offsetX = offsetX + previousTouchX - x
            offsetY = offsetY + previousTouchY - y
        }

        previousTouchX = x
        previousTouchY = y

        // onScrollChangeListener?.invoke(thumbPosition)
        return true
    }

    // private fun updateBarThumbSize(bar: CustomScrollBar, viewSize: Float, childSize: Float){
    //     bar.thumbSize = 
    // }

    // override fun setLayoutParams(params: ViewGroup.LayoutParams ){
    //     super.setLayoutParams(params)
    //     // this.h_bar.isVertical = false
    //     this.h_bar.thumbSize = width() / contentWidth
    //     this.v_bar.thumbSize = width() / contentHeight
    // }

    // private fun height(): Float {
    //     return this.layoutParams.height.toFloat()
    // }

    // private fun width(): Float {
    //     return this.layoutParams?.width?.toFloat() ?: 0F
    // }
}
