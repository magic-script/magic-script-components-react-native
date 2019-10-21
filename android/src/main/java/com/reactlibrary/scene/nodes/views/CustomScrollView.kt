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
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.reactlibrary.utils.Vector2
import com.reactlibrary.utils.onLayoutListener
import kotlinx.android.synthetic.main.scroll_view.view.*

class CustomScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var contentSize = Vector2()
        set(value) {
            field = value
            updateScrollbars()
        }

    var onScrollChangeListener: ((on: Vector2) -> Unit)? = null

    private var isBeingDragged = false
    private var previousTouch = Vector2()

    init {
        // We can be sure nested scrollBars are 
        // initialized only after layout is completed. 
        this.onLayoutListener {
            h_bar.isVertical = false
            h_bar.onScrollChangeListener = { pos: Float ->
                val viewPosition = Vector2(pos, v_bar.thumbPosition)
                onScrollChangeListener?.invoke(viewPosition)
            }
            v_bar.onScrollChangeListener = { pos: Float ->
                val viewPosition = Vector2(h_bar.thumbPosition, pos)
                onScrollChangeListener?.invoke(viewPosition)
            }
            updateScrollbars()
        }
    }

    override fun stopNestedScroll() {
        isBeingDragged = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_MOVE) {
            stopNestedScroll()
            return false
        }

        val touch = Vector2(event.getX(), event.getY())
        if (!isBeingDragged) {
            isBeingDragged = true
            previousTouch = touch
        }

        if (action == MotionEvent.ACTION_MOVE) {
            val movePx = touch - previousTouch
            val viewSize = Vector2(width.toFloat(), height.toFloat())
            val maxTravel = contentSize - viewSize
            val move = movePx / maxTravel

            val thumbPos = Vector2(
                    h_bar.thumbPosition - move.x,
                    v_bar.thumbPosition - move.y)
            h_bar.thumbPosition = thumbPos.x
            v_bar.thumbPosition = thumbPos.y
            onScrollChangeListener?.invoke(getViewPosition())
        }
        previousTouch = touch

        return true
    }

    fun getViewPosition(): Vector2 {
        return Vector2(h_bar.thumbPosition, v_bar.thumbPosition)
    }

    // Update scrollbars when content size has changed.
    private fun updateScrollbars() {
        this.h_bar.thumbSize = width.toFloat() / contentSize.x
        this.v_bar.thumbSize = height.toFloat() / contentSize.y
    }
}
