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

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat.getColor
import com.reactlibrary.R

class CustomSpinner @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val START_ANGLE = 270F // degrees
        private const val INDETERMINATE_SWEEP_ANGLE = 45F // degrees
        private const val INDETERMNATE_ROTATION_TIME = 1000L
        private const val STROKE_SIZE_TO_HEIGHT_RATIO = 0.035F
    }

    enum class Type {
        DETERMINATE,
        INDETERMINATE
    }

    /**
     * Spinner type
     * - use [Type.DETERMINATE] and set progress [value] manually
     * - use [Type.INDETERMINATE] to set indefinite rotation
     */
    var type: Type = Type.INDETERMINATE
        set(value) {
            field = value
            if (value == Type.INDETERMINATE) {
                if (!animator.isStarted) {
                    animator.start()
                }
            } else if (animator.isStarted) {
                animator.pause()
            }
            invalidate()
        }

    /**
     * Progress (from 0 to 1)
     */
    var value: Float = 0F
        set(value) {
            field = value.coerceIn(0F, 1F)
            if (type == Type.DETERMINATE) {
                invalidate()
            }
        }

    private val spinnerRect = RectF()
    private val animator: ValueAnimator

    private var animationAngle = START_ANGLE // for indeterminate mode
    private var attached = false

    private var innerBlurMaskFilter = BlurMaskFilter(1f, BlurMaskFilter.Blur.NORMAL)
    private var outerBlurMaskFilter = BlurMaskFilter(1f, BlurMaskFilter.Blur.NORMAL)

    private var innerStrokeWidth = 0f
    private var innerBlurRadius = 0f
    private var outStrokeWidth = 0f
    private var outBlurRadius = 0f

    init {
        // disabling hardware acceleration to make blur effect working
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        animator = ValueAnimator.ofFloat(START_ANGLE, START_ANGLE + 360)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.interpolator = LinearInterpolator()
        animator.duration = INDETERMNATE_ROTATION_TIME
        animator.addUpdateListener {
            this.animationAngle = it.animatedValue as Float
            if (attached) {
                invalidate()
            }
        }
    }

    // e.g. when activity was resumed
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attached = true
        if (type == Type.INDETERMINATE) {
            if (!animator.isStarted) {
                animator.start()
            } else if (animator.isPaused) {
                animator.resume()
            }
        }
    }

    // Called e.g. when activity was paused. In that case we should pause animation,
    // because otherwise the invalidate() queue will overflow and animation may hung.
    // See: https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/widget/ProgressBar.java
    override fun onDetachedFromWindow() {
        attached = false
        if (type == Type.INDETERMINATE && animator.isStarted) {
            animator.pause()
        }

        // This should come after stopping animation, otherwise an invalidate message remains in the
        // queue, which can prevent the entire view hierarchy from being GC'ed during a rotation
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        innerStrokeWidth = STROKE_SIZE_TO_HEIGHT_RATIO * h
        innerBlurRadius = innerStrokeWidth / 2
        outStrokeWidth = innerStrokeWidth / 2
        outBlurRadius = innerStrokeWidth + innerBlurRadius

        innerBlurMaskFilter = BlurMaskFilter(innerBlurRadius, BlurMaskFilter.Blur.NORMAL)
        outerBlurMaskFilter = BlurMaskFilter(outBlurRadius, BlurMaskFilter.Blur.NORMAL)

        paint.shader = RadialGradient(
                w / 2F,
                h / 2F,
                h / 2F,
                intArrayOf(paint.color, paint.color, Color.TRANSPARENT),
                floatArrayOf(0F, 0.85F, 1F),
                Shader.TileMode.MIRROR
        )
    }

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, R.color.color_spinner)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // drawing inner circle
        val startAngle = if (type == Type.DETERMINATE) START_ANGLE else animationAngle
        val sweepAngle = if (type == Type.DETERMINATE) value * 360F else INDETERMINATE_SWEEP_ANGLE

        paint.strokeWidth = innerStrokeWidth
        paint.maskFilter = innerBlurMaskFilter

        spinnerRect.left =
                innerStrokeWidth / 2 + innerBlurRadius + outStrokeWidth + outBlurRadius
        spinnerRect.top =
                innerStrokeWidth / 2 + innerBlurRadius + outStrokeWidth + outBlurRadius
        spinnerRect.right =
                width.toFloat() - innerStrokeWidth / 2 - innerBlurRadius - outStrokeWidth - outBlurRadius
        spinnerRect.bottom =
                height.toFloat() - innerStrokeWidth / 2 - innerBlurRadius - outStrokeWidth - outBlurRadius

        canvas.drawArc(spinnerRect, startAngle, sweepAngle, false, paint)

        // drawing out circle (with stronger blur effect)
        paint.strokeWidth = outStrokeWidth
        paint.maskFilter = outerBlurMaskFilter

        spinnerRect.left = spinnerRect.left - outStrokeWidth / 2 - outBlurRadius
        spinnerRect.top = spinnerRect.top - outStrokeWidth / 2 - outBlurRadius
        spinnerRect.right = spinnerRect.right + outStrokeWidth / 2 + outBlurRadius
        spinnerRect.bottom = spinnerRect.bottom + outStrokeWidth / 2 + outBlurRadius

        canvas.drawArc(spinnerRect, startAngle, sweepAngle, false, paint)
    }

}
