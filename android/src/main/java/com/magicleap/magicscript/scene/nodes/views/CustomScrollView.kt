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

import android.animation.ObjectAnimator
import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.magicleap.magicscript.R
import com.magicleap.magicscript.utils.Vector2

class CustomScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener,
    ValueAnimator.AnimatorUpdateListener {

    companion object {
        const val SCROLL_DIRECTION_VERTICAL = "vertical"
        const val SCROLL_DIRECTION_HORIZONTAL = "horizontal"
        const val SCROLL_DIRECTION_UNSPECIFIED = ""

        /**
         * Minimum X and Y position
         */
        const val MIN_POSITION = 0f

        /**
         * Maximum X and Y position
         */
        const val MAX_POSITION = 1f
    }

    var contentSize = Vector2()
        set(value) {
            field = value
            updateScrollbars()
        }

    var onScrollChangeListener: ((position: Vector2) -> Unit)? = null

    var scrollDirection = SCROLL_DIRECTION_UNSPECIFIED

    /**
     * Normalized scroll position. X and Y of a vector is always
     * in the range of [MIN_POSITION] to [MAX_POSITION]
     */
    var position = Vector2()
        private set(value) {
            field = value
            hBar?.thumbPosition = value.x
            vBar?.thumbPosition = value.y
            onScrollChangeListener?.invoke(value)
            invalidate()
        }

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
    private val maximumScrollVelocity: Float

    private var scrollAnimator: ValueAnimator? = null

    init {
        viewTreeObserver.addOnGlobalLayoutListener(this)
        val viewConfiguration = ViewConfiguration.get(context)
        maximumScrollVelocity = viewConfiguration.scaledMaximumFlingVelocity.toFloat()
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

            position = (position + move).coerceIn(MIN_POSITION, MAX_POSITION)
            previousTouch = touch
            return true
        }

        if (action == MotionEvent.ACTION_UP) {
            velocityTracker?.let {
                it.addMovement(event)
                it.computeCurrentVelocity(1000, maximumScrollVelocity)
                startScrollAnimation(it.xVelocity, it.yVelocity)
                it.recycle()
            }
            velocityTracker = null
            isBeingDragged = false
            return true
        }

        isBeingDragged = false
        return false
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val newPos = animation.animatedValue as PointF
        position = Vector2(newPos.x, newPos.y)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scrollAnimator?.apply {
            if (isPaused) {
                resume()
            }
        }
    }

    // Called e.g. when activity was paused. In that case we should pause animation,
    // because otherwise the invalidate() queue will overflow and animation may get stuck.
    // See: https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/widget/ProgressBar.java
    override fun onDetachedFromWindow() {
        scrollAnimator?.apply {
            if (isStarted) {
                pause()
            }
        }
        // This should come after stopping animation, otherwise an invalidate message remains in the
        // queue, which can prevent the entire view hierarchy from being GC'ed during a rotation
        super.onDetachedFromWindow()
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

    private fun startScrollAnimation(speedX: Float, speedY: Float) {
        // cancel previous animation
        scrollAnimator?.cancel()

        val scrollDeltaX = -speedX / maximumScrollVelocity
        val scrollDeltaY = -speedY / maximumScrollVelocity

        val destX = (position.x + scrollDeltaX).coerceIn(MIN_POSITION, MAX_POSITION)
        val destY = (position.y + scrollDeltaY).coerceIn(MIN_POSITION, MAX_POSITION)

        scrollAnimator = ObjectAnimator.ofObject(
            PointFEvaluator(),
            PointF(position.x, position.y),
            PointF(destX, destY)
        ).also {
            it.duration = 200
            it.addUpdateListener(this)
            it.start()
        }
    }

}
