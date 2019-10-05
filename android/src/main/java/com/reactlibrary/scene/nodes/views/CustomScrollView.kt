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

import com.google.ar.sceneform.Node
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
            update()
        }

    var onScrollChangeListener: ((on: PointF) -> Unit)? = null

    private var previousTouch = PointF()

    init {
        // We can be sure nested scrollBars are 
        // initialized only after layout is completed. 
        this.onPreDrawListener{
                h_bar.onScrollChangeListener = { pos: Float ->
                    val viewPosition = PointF(pos, v_bar.thumbPosition)
                    onScrollChangeListener?.invoke(viewPosition)
                }
                v_bar.onScrollChangeListener = { pos: Float ->
                    val viewPosition = PointF(h_bar.thumbPosition, pos)
                    onScrollChangeListener?.invoke(viewPosition)
                }
                update()
            }
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
            h_bar.thumbPosition += move.x
            v_bar.thumbPosition += move.y
        }
        previousTouch = touch

        onScrollChangeListener?.invoke(viewPosition())
        return true
    }

    fun viewPosition(): PointF {
        return PointF(h_bar.thumbPosition, v_bar.thumbPosition)
    }

    private fun update() {
        this.h_bar.thumbSize = width.toFloat() / contentSize.x
        this.v_bar.thumbSize = height.toFloat() / contentSize.y
    }
}
