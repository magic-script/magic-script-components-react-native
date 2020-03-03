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

package com.magicleap.magicscript.scene.nodes.button

import android.content.Context
import android.os.Bundle
import android.view.View
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.views.CustomButton
import com.magicleap.magicscript.utils.*

open class UiButtonNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper,
    private val fontProvider: FontProvider,
    private val iconsRepo: IconsRepository
) : UiNode(initProps, context, viewRenderableLoader, nodeClipper) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_TEXT = "text"
        const val PROP_TEXT_SIZE = "textSize"
        const val PROP_TEXT_COLOR = "textColor"
        const val PROP_ROUNDNESS = "roundness"
        const val PROP_ICON_TYPE = "iconType"
        const val PROP_ICON_COLOR = "iconColor"
        const val PROP_ICON_SIZE = "iconSize"
        const val PROP_TYPE = "type"
        const val PROP_LABEL_SIDE = "labelSide"

        const val DEFAULT_ROUNDNESS = 1.0
        const val DEFAULT_TEXT_SIZE = 0.0167
        val DEFAULT_COLOR = JavaOnlyArray.of(0.75, 0.75, 0.75, 1.00)
        val DEFAULT_ICON_SIZE = JavaOnlyArray.of(0.04, 0.04)

        // text padding = factor * text height
        private const val PADDING_FACTOR_HORIZONTAL = 1.55F
        const val PADDING_FACTOR_VERTICAL = 1.15F
        private const val ANIMATED_Z = -0.05f

        const val BUTTON_TYPE_ICON = "icon"
        const val BUTTON_TYPE_ICON_WITH_LABEL = "icon-with-label"
        const val BUTTON_TYPE_TEXT = "text"
        const val BUTTON_TYPE_TEXT_WITH_ICON = "text-with-icon"

        const val LABEL_SIDE_TOP = "top"
        const val LABEL_SIDE_LEFT = "left"
        const val LABEL_SIDE_BOTTOM = "bottom"
        const val LABEL_SIDE_RIGHT = "right"
    }

    protected open val charactersSpacing = 0.1F

    private var playingAnim = false

    init {
        // set default values of properties
        properties.putDefault(PROP_ROUNDNESS, DEFAULT_ROUNDNESS)
        properties.putDefault(PROP_ICON_COLOR, DEFAULT_COLOR)
        properties.putDefault(PROP_TEXT_COLOR, DEFAULT_COLOR)
        properties.putDefault(PROP_ICON_SIZE, DEFAULT_ICON_SIZE)

        if (!properties.containsKey(PROP_TEXT_SIZE)) {
            // calculate text size based on button height
            val height = properties.getDouble(PROP_HEIGHT)
            if (height.toFloat() != WRAP_CONTENT_DIMENSION) {
                val textSize = height / 3
                properties.putDouble(PROP_TEXT_SIZE, textSize)
            } else {
                properties.putDouble(
                    PROP_TEXT_SIZE,
                    DEFAULT_TEXT_SIZE
                )
            }
        }
    }

    override fun provideView(context: Context): View {
        return CustomButton(context)
    }

    override fun provideDesiredSize(): Vector2 {
        val width = properties.getDouble(PROP_WIDTH, WRAP_CONTENT_DIMENSION.toDouble())
        val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        return Vector2(width.toFloat(), height.toFloat())
    }

    override fun setupView() {
        super.setupView()

        (view as CustomButton).apply {
            val font = fontProvider.provideFont()
            setTypeface(font)

            val textSize = properties.getDouble(
                PROP_TEXT_SIZE,
                DEFAULT_TEXT_SIZE
            ).toFloat()
            // padding is added when button width or height is "wrap content"
            val textHeightPx = Utils.metersToFontPx(textSize, context)
            val textPaddingHorizontal = (PADDING_FACTOR_HORIZONTAL * textHeightPx).toInt()
            val textPaddingVertical = (PADDING_FACTOR_VERTICAL * textHeightPx).toInt()
            setTextPadding(textPaddingHorizontal, textPaddingVertical)

            setCharactersSpacing(charactersSpacing)
            onPressedChangeListener = { pressed ->
                val z =
                    if (pressed) {
                        ANIMATED_Z
                    } else {
                        0f
                    }
                contentNode.localPosition = Vector3(localPosition.x, localPosition.y, z)
            }
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        setText(props)
        setTextSize(props)
        setTextColor(props)
        setRoundness(props)
        setIcon(props)
        setIconColor(props)
        setIconSize(props)
        setType(props)
        setLabelSide(props)
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for button
    }

    private fun canResizeOnContentChange(): Boolean {
        val width = properties.getDouble(PROP_WIDTH).toFloat()
        val height = properties.getDouble(PROP_HEIGHT).toFloat()
        return width == WRAP_CONTENT_DIMENSION || height == WRAP_CONTENT_DIMENSION
    }

    private fun setText(props: Bundle) {
        val text = props.getString(PROP_TEXT)
        if (text != null) {
            (view as CustomButton).text = text
            // rebuild only if size can be changed
            if (canResizeOnContentChange()) {
                setNeedsRebuild()
            }
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val textSize = props.getDouble(PROP_TEXT_SIZE).toFloat()
            val size = Utils.metersToFontPx(textSize, view.context).toFloat()
            (view as CustomButton).setTextSize(size)
            // rebuild only if size can be changed
            if (canResizeOnContentChange()) {
                setNeedsRebuild()
            }
        }
    }

    private fun setTextColor(props: Bundle) {
        val color = props.readColor(PROP_TEXT_COLOR)
        if (color != null) {
            (view as CustomButton).setTextColor(color)
        }
    }

    // Sets the corners roundness (0 - sharp, 1 - fully rounded)
    private fun setRoundness(props: Bundle) {
        if (props.containsKey(PROP_ROUNDNESS)) {
            val roundness = props.getDouble(PROP_ROUNDNESS).toFloat()
            (view as CustomButton).roundnessFactor = roundness
        }
    }

    private fun setIcon(props: Bundle) {
        val iconName = props.getString(PROP_ICON_TYPE)
        if (iconName != null) {
            val icon = iconsRepo.getIcon(iconName, false)
            if (icon != null) {
                (view as CustomButton).setIcon(icon)
            }
        }
    }

    private fun setIconColor(props: Bundle) {
        val color = props.readColor(PROP_ICON_COLOR)
        if (color != null) {
            (view as CustomButton).setIconColor(color)
        }
    }

    private fun setIconSize(props: Bundle) {
        val size = props.read<Vector2>(PROP_ICON_SIZE)
        if (size != null) {
            val widthPx = Utils.metersToPx(size.x, view.context).toFloat()
            val heightPx = Utils.metersToPx(size.y, view.context).toFloat()
            (view as CustomButton).iconSize = Vector2(widthPx, heightPx)
        }
    }

    private fun setType(props: Bundle) {
        props.getString(PROP_TYPE)?.let { buttonType ->
            when (buttonType) {
                BUTTON_TYPE_TEXT_WITH_ICON -> {
                    showIcon()
                    showText()
                    showBorder()
                    hideLabel()
                }
                BUTTON_TYPE_ICON -> {
                    showIcon()
                    hideText()
                    hideBorder()
                    hideLabel()
                }

                BUTTON_TYPE_ICON_WITH_LABEL -> {
                    showIcon()
                    hideText()
                    hideBorder()
                    showLabel()
                }

                BUTTON_TYPE_TEXT -> {
                    hideIcon()
                    showText()
                    showBorder()
                    hideLabel()
                }
            }
        }
    }

    private fun showText() {
        (view as CustomButton).textVisible = true
    }

    private fun showIcon() {
        (view as CustomButton).iconVisible = true
    }

    private fun hideText() {
        (view as CustomButton).textVisible = false
    }

    private fun hideIcon() {
        (view as CustomButton).iconVisible = false
    }

    private fun showLabel() {
        (view as CustomButton).labelVisible = true
    }

    private fun hideLabel() {
        (view as CustomButton).labelVisible = false
    }

    private fun showBorder() {
        (view as CustomButton).borderEnabled = true
    }

    private fun hideBorder() {
        (view as CustomButton).borderEnabled = false
    }

    private fun setLabelSide(props: Bundle) {
        props.getString(PROP_LABEL_SIDE)?.let { side ->
            when (side) {
                LABEL_SIDE_TOP -> {
                    (view as CustomButton).labelPosition = CustomButton.LabelPosition.TOP
                }

                LABEL_SIDE_LEFT -> {
                    (view as CustomButton).labelPosition = CustomButton.LabelPosition.LEFT
                }

                LABEL_SIDE_BOTTOM -> {
                    (view as CustomButton).labelPosition = CustomButton.LabelPosition.BOTTOM
                }

                LABEL_SIDE_RIGHT -> {
                    (view as CustomButton).labelPosition = CustomButton.LabelPosition.RIGHT
                }
            }
        }
    }

}