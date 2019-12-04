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

package com.magicleap.magicscript.scene.nodes.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.magicleap.magicscript.R
import com.magicleap.magicscript.utils.Vector2

class CustomScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener {

    companion object {
        const val SCROLL_DIRECTION_VERTICAL = "vertical"
        const val SCROLL_DIRECTION_HORIZONTAL = "horizontal"
        const val SCROLL_DIRECTION_UNSPECIFIED = ""
    }

    var contentSize = Vector2()
        set(value) {
            field = value
            updateScrollbars()
        }

    var onScrollChangeListener: ((position: Vector2) -> Unit)? = null

    var scrollDirection = SCROLL_DIRECTION_UNSPECIFIED

    /**
     * Normalized scroll position (0 - 1)
     */
    var position = Vector2()
        private set

    var hBar: CustomScrollBar? = null
        private set(value) {
            field = value
            updateScrollbars()
        }

    var vBar: CustomScrollBar? = null
        private set(value) {
            field = value
            updateScrollbars()
        }

    private var isBeingDragged = false
    private var previousTouch = Vector2()
    private var velocityTracker: VelocityTracker? = null

    init {
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun stopNestedScroll() {
        isBeingDragged = false
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        this.vBar = findViewById(R.id.bar_vertical)
        this.hBar = findViewById(R.id.bar_horizontal)
    }

    override fun onGlobalLayout() {
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        updateScrollbars()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }

        val action = event.actionMasked
        val touch = Vector2(event.x, event.y)
        if (!isBeingDragged) {
            isBeingDragged = true
            previousTouch = touch
        }

        if (action == MotionEvent.ACTION_DOWN) {
            velocityTracker?.addMovement(event)
            return true
        }

        if (action == MotionEvent.ACTION_MOVE) {
            velocityTracker?.addMovement(event)
            val movePx = previousTouch - touch
            val viewSize = Vector2(width.toFloat(), height.toFloat())
            val maxTravel = contentSize - viewSize
            val move = movePx / maxTravel

            when (scrollDirection) {
                SCROLL_DIRECTION_VERTICAL -> move.x = 0F
                SCROLL_DIRECTION_HORIZONTAL -> move.y = 0F
            }

            position = (position + move).coerceIn(0F, 1F)
            hBar?.thumbPosition = position.x
            vBar?.thumbPosition = position.y
            onScrollChangeListener?.invoke(position)
            previousTouch = touch
            return true
        }

        if (action == MotionEvent.ACTION_UP) {
            velocityTracker?.addMovement(event)
            // TODO get velocity and move scroll
            velocityTracker?.recycle()
            velocityTracker = null
            isBeingDragged = false
            return true
        }

        isBeingDragged = false
        return false
    }

    private fun updateScrollbars() {

        // set default thumb length if not specified
        hBar?.let {
            if (it.useAutoThumbSize && contentSize.x > 0) {
                hBar?.thumbSize = width.toFloat() / contentSize.x
            }
        }

        vBar?.let {
            if (it.useAutoThumbSize && contentSize.y > 0) {
                vBar?.thumbSize = height.toFloat() / contentSize.y
            }
        }
    }
}
