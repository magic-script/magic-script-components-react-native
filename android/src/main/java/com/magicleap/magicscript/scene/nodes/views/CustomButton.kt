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
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat.getColor
import com.magicleap.magicscript.utils.Vector2
import kotlin.math.max
import kotlin.math.min

class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class IconPosition {
        LEFT, RIGHT
    }

    enum class LabelPosition {
        LEFT, RIGHT, TOP, BOTTOM
    }

    var text = ""
        set(value) {
            field = value
            invalidate()
            requestLayout() // need to measure the view
        }

    var roundnessFactor = 1F // from 0 to 1 (fully rounded)
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Icon size in pixels
     */
    var iconSize: Vector2 = Vector2(0F, 0F)
        set(value) {
            field = value
            usingDefaultIconSize = false
            if (iconBitmap != null) {
                invalidate()
                requestLayout()
            }
        }

    var iconPosition = IconPosition.LEFT
        set(value) {
            field = value
            if (iconBitmap != null) {
                invalidate()
            }
        }

    var labelPosition = LabelPosition.TOP
        set(value) {
            field = value
            if (iconBitmap != null) {
                invalidate()
            }
        }

    var borderEnabled = true
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    // relative to button height
    var defaultIconHeightFactor: Float = 0.65F
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    var textVisible = true
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    var iconVisible = true
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    var labelVisible = false
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    var onPressedChangeListener: ((Boolean) -> Unit)? = null

    // border width = shorter button dimension * borderWidthFactor
    private val borderWidthFactor = 0.07F
    private val iconSpacingFactor = 0.3F // spacing offset from text (relative to icon width)

    private val textPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = getColor(context, android.R.color.white)
        textSize = 12F
    }

    private val iconPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = getColor(context, android.R.color.white)
        colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private val bgPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, android.R.color.white)
    }

    private val textBounds = Rect()
    private val iconBounds = RectF()

    private var paddingHorizontal = 0
    private var paddingVertical = 0
    private var iconBitmap: Bitmap? = null
    private var iconPadding = 0F
    private var usingDefaultIconSize = true

    private val textWidth
        get() = textPaint.getTextBounds(text, 0, text.length, textBounds).run {
            textBounds.width().toFloat()
        }

    private val textHeight
        get() = textPaint.getTextBounds(text, 0, text.length, textBounds).run {
            textBounds.height().toFloat()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        // read text area size (caution: textBounds is non zero for empty text)
        var textWidth = this.textWidth
        var textHeight = this.textHeight

        if (borderEnabled || labelVisible) {
            textWidth += 2F * paddingHorizontal
            textHeight += 2F * paddingVertical
        }

        // set default icon size
        iconBitmap?.let { icon ->
            if (usingDefaultIconSize) {
                val referenceHeight = if (heightMode == MeasureSpec.EXACTLY) {
                    heightSize.toFloat()
                } else {
                    textHeight + 2F * paddingVertical
                }
                val iconHeight = defaultIconHeightFactor * referenceHeight
                val iconWidth = icon.width / icon.height * iconHeight
                iconSize = Vector2(iconWidth, iconHeight)
            }
            iconPadding = iconSpacingFactor * iconSize.x
        }

        val defaultWidth = calculateDefaultWidth(textWidth)
        val defaultHeight = calculateDefaultHeight(textHeight)

        // button width and height
        val width: Int = if (widthMode == MeasureSpec.EXACTLY) { // exact size provided
            widthSize
        } else { // WRAP_CONTENT
            defaultWidth.toInt()
        }

        val height: Int = if (heightMode == MeasureSpec.EXACTLY) { // exact size provided
            if (labelVisible
                && (labelPosition == LabelPosition.BOTTOM || labelPosition == LabelPosition.TOP)
            ) {
                max(heightSize, defaultHeight.toInt())
            } else {
                heightSize
            }
        } else { // WRAP_CONTENT
            defaultHeight.toInt()
        }
        setMeasuredDimension(width, height)
    }

    private fun calculateDefaultHeight(textHeight: Float): Float =
        if (labelVisible && (labelPosition == LabelPosition.TOP || labelPosition == LabelPosition.BOTTOM)) {
            textHeight + iconSize.y + iconPadding
        } else {
            if (text.isNotEmpty()) {
                max(iconSize.y + iconPadding, textHeight)
            } else {
                iconSize.y + iconPadding
            }
        }

    private fun calculateDefaultWidth(textWidth: Float): Float =
        if (labelVisible && (labelPosition == LabelPosition.TOP || labelPosition == LabelPosition.BOTTOM)) {
            max(textWidth, iconSize.x + iconPadding)
        } else {
            if (text.isNotEmpty()) {
                max(textWidth + iconSize.x + iconPadding, textWidth)
            } else {
                iconSize.x + iconPadding
            }
        }

    override fun dispatchSetPressed(pressed: Boolean) {
        invalidate()
        onPressedChangeListener?.invoke(pressed)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (borderEnabled) {
            drawBorder(canvas)
        }

        if (iconVisible) {
            // draw icon if provided
            iconBitmap?.let {
                drawIcon(canvas, it)
            }
        }

        if (textVisible) {
            // draw text
            val textOffsetX = when {
                iconBitmap == null || !iconVisible -> 0F
                iconPosition == IconPosition.LEFT -> iconSize.x + iconPadding
                else -> 0f
            }
            val textOffsetCenterVertical = height / 2f - textBounds.exactCenterY() - paddingVertical
            drawText(canvas, textOffsetX, textOffsetCenterVertical)
        }

        if (labelVisible && isPressed) {
            // draw label
            val textOffsetX = when {
                iconBitmap == null -> 0F
                labelPosition == LabelPosition.RIGHT -> iconSize.x + iconPadding
                labelPosition == LabelPosition.TOP -> (iconBounds.width() + iconPadding) / 2
                labelPosition == LabelPosition.BOTTOM -> (iconBounds.width() + iconPadding) / 2
                else -> 0f
            }

            val labelOffsetCenterVertical =
                height / 2f - textBounds.exactCenterY() - paddingVertical

            val textOffsetY = when {
                iconBitmap == null -> 0F
                labelPosition == LabelPosition.BOTTOM -> iconSize.y + iconPadding * 2f
                labelPosition == LabelPosition.LEFT -> labelOffsetCenterVertical
                labelPosition == LabelPosition.RIGHT -> labelOffsetCenterVertical
                else -> 0f
            }
            drawText(canvas, textOffsetX, textOffsetY)
        }
    }

    fun setTextSize(textSizePx: Float) {
        textPaint.textSize = textSizePx
        invalidate()
        requestLayout() // need to measure the view
    }

    fun setTextColor(@ColorInt color: Int) {
        textPaint.color = color
        invalidate()
    }

    fun setIconColor(@ColorInt color: Int) {
        iconPaint.color = color
        iconPaint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        invalidate()
    }

    fun setTextPadding(paddingHorizontalPx: Int, paddingVerticalPx: Int) {
        paddingHorizontal = paddingHorizontalPx
        paddingVertical = paddingVerticalPx
        invalidate()
        requestLayout() // need to measure the view
    }

    fun setTypeface(typeface: Typeface) {
        textPaint.typeface = typeface
    }

    /**
     * Sets characters spacing in 'EM' units
     */
    fun setCharactersSpacing(spacing: Float) {
        textPaint.letterSpacing = spacing
        invalidate()
        requestLayout() // need to measure the view
    }

    /**
     * Sets an icon for the button or removes current icon (when passed null)
     */
    fun setIcon(drawable: Drawable?) {
        if (drawable != null) {
            val icon = drawableToBitmap(drawable)
            this.iconBitmap = icon
        } else {
            this.iconBitmap = null
        }
        invalidate()
        requestLayout()
    }

    private fun drawBorder(canvas: Canvas) {
        val strokeSize = borderWidthFactor * min(width, height)
        val radius = (height.toFloat() - strokeSize) / 2 * roundnessFactor

        bgPaint.strokeWidth = strokeSize
        canvas.drawRoundRect(
            strokeSize / 2,
            strokeSize / 2,
            width.toFloat() - strokeSize / 2,
            height.toFloat() - strokeSize / 2,
            radius,
            radius,
            bgPaint
        )
    }

    private fun drawIcon(canvas: Canvas, icon: Bitmap) {
        if (labelVisible) {
            calculateIconBoundsForLabelWithIcon()
        } else if (textVisible) {
            calculateIconBoundsForTextWithIcon()
        } else {
            calculateIconBoundsForIconOnly()
        }

        canvas.drawBitmap(icon, null, iconBounds, iconPaint)
    }

    private fun calculateIconBoundsForLabelWithIcon() {
        val centerX = width / 2f
        val centerY = height / 2f


        when (labelPosition) {
            LabelPosition.LEFT -> {
                iconBounds.right = width - paddingHorizontal.toFloat()
                iconBounds.left = iconBounds.right - iconSize.x
                iconBounds.top = (this.height - iconSize.y) / 2F
                iconBounds.bottom = iconBounds.top + iconSize.y
            }
            LabelPosition.RIGHT -> {
                iconBounds.left = paddingHorizontal.toFloat()
                iconBounds.right = iconBounds.left + iconSize.x
                iconBounds.top = (this.height - iconSize.y) / 2F
                iconBounds.bottom = iconBounds.top + iconSize.y
            }
            LabelPosition.TOP -> {
                iconBounds.right =
                    centerX + (max(textWidth / 2, iconSize.x / 2))
                iconBounds.left = iconBounds.right - iconSize.x
                iconBounds.top = centerY - iconSize.y / 2 + iconPadding
                iconBounds.bottom = iconBounds.top + iconSize.y
            }
            LabelPosition.BOTTOM -> {
                iconBounds.right = centerX + iconSize.x / 2
                iconBounds.left = iconBounds.right - iconSize.x
                iconBounds.top = iconPadding
                iconBounds.bottom = iconBounds.top + iconSize.y
            }
        }
    }

    private fun calculateIconBoundsForTextWithIcon() {
        val iconOffset = if (text.isNotEmpty()) {
            textBounds.width() / 2F + iconPadding
        } else 0F

        if (iconPosition == IconPosition.LEFT) {
            iconBounds.right = this.width / 2 + iconSize.x / 2 - iconOffset
            iconBounds.left = iconBounds.right - iconSize.x
        } else {
            iconBounds.left = this.width / 2 - iconSize.x / 2 + iconOffset
            iconBounds.right = iconBounds.left + iconSize.x
        }
        iconBounds.top = (this.height - iconSize.y) / 2
        iconBounds.bottom = iconBounds.top + iconSize.y
    }

    private fun calculateIconBoundsForIconOnly() {
        iconBounds.left = (this.width - iconSize.x) / 2
        iconBounds.right = iconBounds.left + iconSize.x
        iconBounds.top = (this.height - iconSize.y) / 2
        iconBounds.bottom = iconBounds.top + iconSize.y
    }

    private fun drawText(canvas: Canvas, offsetFromX: Float, offsetY: Float) {
        val textX =
            width / 2 - textBounds.exactCenterX() - (iconBounds.width() + iconPadding) / 2 + offsetFromX
        val textY = paddingVertical + offsetY
        canvas.drawText(text, textX, textY, textPaint)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap = Bitmap.createBitmap(
            max(drawable.intrinsicWidth, 1),
            max(drawable.intrinsicHeight, 1),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}