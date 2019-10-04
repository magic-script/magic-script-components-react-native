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
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.scroll_view.view.*
import com.reactlibrary.utils.*

class CustomScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    var contentSize = PointF()
        set(value) {
            field = value
            this.h_bar.thumbSize = viewWidth / field.x
            this.v_bar.thumbSize = viewHeight / field.y
        }

    // remove??
    var viewWidth = 0F
        set(value) {
            field = value
            this.h_bar.thumbSize = viewWidth / contentSize.x
        }

    var viewHeight = 0F
        set(value) {
            field = value
            this.v_bar.thumbSize = viewHeight / contentSize.y
        }

    var viewPosition = PointF()
        set(value) {
            field = value.coerceIn(0F, 1F)
        }

    private var previousTouch = PointF()

    var onScrollChangeListener: ((on: PointF) -> Unit)? = null

    init {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // logMessage("onLayout")
                h_bar.onScrollChangeListener = { pos: Float -> Unit
                    // logMessage("h_bar.onScrollChangeListener")
                    viewPosition = PointF(pos, viewPosition.y)
                    onScrollChangeListener?.invoke(viewPosition)
                }
                v_bar.onScrollChangeListener = { pos: Float -> Unit
                    // logMessage("h_bar.onScrollChangeListener")
                    viewPosition = PointF(viewPosition.x, pos)
                    onScrollChangeListener?.invoke(viewPosition)
                }
                viewTreeObserver.removeOnGlobalLayoutListener(this);
            }
        })
    }

    fun onGlobalLayout() {
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_MOVE) {
            return false
        }

        val touch = PointF(event.getX(), event.getY())
        if (action == MotionEvent.ACTION_MOVE) {
            val movePx = touch - previousTouch
            val move = movePx / contentSize
            viewPosition = viewPosition + move
        }
        previousTouch = touch

        this.h_bar.thumbPosition = viewPosition.x
        this.v_bar.thumbPosition = viewPosition.y
        onScrollChangeListener?.invoke(viewPosition)
        return true
    }
}
