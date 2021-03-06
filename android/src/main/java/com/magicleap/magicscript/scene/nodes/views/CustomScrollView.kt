/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.magicleap.magicscript.scene.nodes.props.ScrollBarVisibility
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.isCloseTo
import kotlin.math.abs
import kotlin.math.sign

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

        private const val SCROLL_ANIM_DURATION = 200L
        private const val PIXELS_PER_SECOND_UNIT = 1000
    }

    /**
     * Content size in pixels
     */
    var contentSize = Vector2()
        set(value) {
            field = value
            updateScrollbars()
        }

    var onUserScrollListener: ((position: Vector2) -> Unit)? = null

    var scrollDirection = SCROLL_DIRECTION_UNSPECIFIED

    var scrollingEnabled = true

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

    var scrollBarsVisibility: String = ScrollBarVisibility.AUTO
        set(value) {
            field = value
            val maxTravel = getMaxPossibleTravel()
            vBar?.let {
                adjustScrollBarVisibility(it, maxTravel.y)
            }
            hBar?.let {
                adjustScrollBarVisibility(it, maxTravel.x)
            }
        }

    /**
     * Normalized scroll position. X and Y of a vector is always
     * in the range of [MIN_POSITION] to [MAX_POSITION]
     */
    var scrollValue = Vector2()
        private set

    fun updateScrollValue(value: Vector2) {
        updateScrollValue(value, notifyListener = false)
    }

    private var isBeingDragged = false
    private var previousTouch = Vector2()
    private var lastMove = Vector2()
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

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        if (child is CustomScrollBar) {
            if (child.isVertical) {
                this.vBar = child
            } else {
                this.hBar = child
            }
        }
    }

    override fun onGlobalLayout() {
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        updateScrollbars()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!scrollingEnabled) {
            return false
        }

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
            val maxTravel = getMaxPossibleTravel()

            val moveX = if (maxTravel.x != 0f) movePx.x / maxTravel.x else 0f
            val moveY = if (maxTravel.y != 0f) movePx.y / maxTravel.y else 0f

            lastMove = Vector2(moveX, moveY)

            when (scrollDirection) {
                SCROLL_DIRECTION_VERTICAL -> lastMove.x = 0F
                SCROLL_DIRECTION_HORIZONTAL -> lastMove.y = 0F
            }

            updateScrollValue(scrollValue + lastMove, notifyListener = true)
            previousTouch = touch
            return true
        }

        if (action == MotionEvent.ACTION_UP) {
            velocityTracker?.let {
                it.addMovement(event)
                it.computeCurrentVelocity(PIXELS_PER_SECOND_UNIT, maximumScrollVelocity)
                // must use abs, because velocity may be change sign even in same direction
                val speedX = abs(it.xVelocity)
                val speedY = abs(it.yVelocity)
                val direction = lastMove
                startScrollAnimation(direction, speedX, speedY)
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
        updateScrollValue(Vector2(newPos.x, newPos.y), notifyListener = true)
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

    private fun updateScrollValue(value: Vector2, notifyListener: Boolean) {
        val normalizedPos = value.coerceIn(MIN_POSITION, MAX_POSITION)
        scrollValue = normalizedPos
        hBar?.thumbPosition = normalizedPos.x
        vBar?.thumbPosition = normalizedPos.y
        invalidate()

        if (notifyListener) {
            onUserScrollListener?.invoke(scrollValue)
        }
    }

    private fun updateScrollbars() {
        val maxTravel = getMaxPossibleTravel()

        // set default thumb length if not specified
        hBar?.let {
            if (it.useAutoThumbSize && contentSize.x > 0) {
                it.thumbSize = width.toFloat() / contentSize.x
                adjustScrollBarVisibility(it, maxTravel.x)
            }
        }

        vBar?.let {
            if (it.useAutoThumbSize && contentSize.y > 0) {
                it.thumbSize = height.toFloat() / contentSize.y
                adjustScrollBarVisibility(it, maxTravel.y)
            }
        }
    }

    private fun adjustScrollBarVisibility(bar: CustomScrollBar, maxTravel: Float) {
        when (scrollBarsVisibility) {
            ScrollBarVisibility.AUTO -> {
                if (maxTravel.isCloseTo(0f, epsilon = 1e-5f)) {
                    bar.visibility = View.INVISIBLE
                } else {
                    bar.visibility = View.VISIBLE
                }
            }
            ScrollBarVisibility.ALWAYS -> bar.visibility = View.VISIBLE
            ScrollBarVisibility.OFF -> bar.visibility = View.INVISIBLE
        }
    }

    private fun getMaxPossibleTravel(): Vector2 {
        val viewSize = Vector2(width.toFloat(), height.toFloat())
        return (contentSize - viewSize).coerceAtLeast(0f)
    }

    private fun startScrollAnimation(direction: Vector2, speedX: Float, speedY: Float) {
        // cancel previous animation
        scrollAnimator?.cancel()

        val scrollDeltaX = sign(direction.x) * speedX / maximumScrollVelocity
        val scrollDeltaY = sign(direction.y) * speedY / maximumScrollVelocity

        val destX = (scrollValue.x + scrollDeltaX).coerceIn(MIN_POSITION, MAX_POSITION)
        val destY = (scrollValue.y + scrollDeltaY).coerceIn(MIN_POSITION, MAX_POSITION)

        scrollAnimator = ObjectAnimator.ofObject(
            PointFEvaluator(),
            PointF(scrollValue.x, scrollValue.y),
            PointF(destX, destY)
        ).also {
            it.duration = SCROLL_ANIM_DURATION
            it.addUpdateListener(this)
            it.interpolator = DecelerateInterpolator()
            it.start()
        }
    }

}
