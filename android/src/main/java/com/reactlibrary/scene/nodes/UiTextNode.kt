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

package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.font.FontProvider
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.*

open class UiTextNode(initProps: ReadableMap,
                      context: Context,
                      viewRenderableLoader: ViewRenderableLoader,
                      private val fontProvider: FontProvider)
    : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        // properties
        const val PROP_TEXT = "text"
        const val PROP_TEXT_SIZE = "textSize"
        const val PROP_BOUNDS_SIZE = "boundsSize"
        const val PROP_WRAP = "wrap"
        const val PROP_TEXT_ALIGNMENT = "textAlignment"
        const val PROP_TEXT_COLOR = "textColor"
        const val PROP_CHARACTERS_SPACING = "charSpacing"
        const val PROP_FONT_PARAMS = "fontParameters"

        const val DEFAULT_TEXT_SIZE = 0.025 // in meters
        const val DEFAULT_ALIGNMENT = "bottom-left" // view alignment (pivot)
        const val WRAP_CONTENT_DIMENSION = 0F // 0 width or height means "wrap content"
    }

    init {
        // set default values of properties
        properties.putDefaultDouble(PROP_TEXT_SIZE, DEFAULT_TEXT_SIZE)
        properties.putDefaultString(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text, null) as TextView
    }

    fun setBackgroundColor(color: Int) {
        view.setBackgroundColor(color)
    }

    override fun setupView() {
        // dimensions in pixels
        var widthPx = ViewGroup.LayoutParams.WRAP_CONTENT
        var heightPx = ViewGroup.LayoutParams.WRAP_CONTENT

        val size = readSize()
        val width = size.first
        val height = size.second

        if (width == WRAP_CONTENT_DIMENSION) {
            (view as TextView).setSingleLine(true)
        } else {
            widthPx = Utils.metersToPx(width, context)
        }

        if (height != WRAP_CONTENT_DIMENSION) {
            heightPx = Utils.metersToPx(height, context)
        }
        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)

        val fontParams = FontParamsReader.readFontParams(properties, PROP_FONT_PARAMS)
        if (fontParams == null) {  // setting a default typeface
            (view as TextView).typeface = fontProvider.provideFont()
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_BOUNDS_SIZE)) {
            setNeedsRebuild()
        }

        setText(props)
        setTextSize(props)
        setTextAlignment(props)
        setTextColor(props)
        setCharactersSpacing(props)
        setWrap(props)
        setFontParams(props)
    }

    private fun canResizeOnContentChange(): Boolean {
        val size = readSize()
        return size.first == WRAP_CONTENT_DIMENSION || size.second == WRAP_CONTENT_DIMENSION
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            (view as TextView).text = text
            // rebuild only if size can be changed
            if (canResizeOnContentChange()) {
                setNeedsRebuild()
            }
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE).toFloat()
            val size = Utils.metersToFontPx(sizeMeters, view.context).toFloat()
            (view as TextView).setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            // rebuild only if size can be changed
            if (canResizeOnContentChange()) {
                setNeedsRebuild()
            }
        }
    }

    private fun setTextAlignment(props: Bundle) {
        when (props.getString(PROP_TEXT_ALIGNMENT)) {
            "left" -> {
                (view as TextView).gravity = Gravity.LEFT
            }
            "center" -> {
                (view as TextView).gravity = Gravity.CENTER_HORIZONTAL
            }
            "right" -> {
                (view as TextView).gravity = Gravity.RIGHT
            }
        }
    }

    private fun setTextColor(props: Bundle) {
        val color = PropertiesReader.readColor(props, PROP_TEXT_COLOR)
        if (color != null) {
            (view as TextView).setTextColor(color)
        }
    }

    private fun setCharactersSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTERS_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTERS_SPACING)
            (view as TextView).letterSpacing = spacing.toFloat()
            // rebuild only if size can be changed
            if (canResizeOnContentChange()) {
                setNeedsRebuild()
            }
        }
    }

    private fun setWrap(props: Bundle) {
        val width = readSize().first
        if (width == WRAP_CONTENT_DIMENSION) {
            return
        }
        if (props.containsKey(PROP_BOUNDS_SIZE)) {
            val boundsData = props.get(PROP_BOUNDS_SIZE) as Bundle
            val wrap = boundsData.getBoolean(PROP_WRAP)
            (view as TextView).setSingleLine(!wrap)
        }
    }

    private fun setFontParams(props: Bundle) {
        val fontParams = FontParamsReader.readFontParams(props, PROP_FONT_PARAMS) ?: return
        val textView = (view as TextView)
        textView.typeface = fontProvider.provideFont(fontParams)
        textView.isAllCaps = fontParams.allCaps
    }

    private fun readSize(): Pair<Float, Float> {
        if (properties.containsKey(PROP_BOUNDS_SIZE)) {
            val boundsData = properties.get(PROP_BOUNDS_SIZE) as Bundle
            val bounds = boundsData.getSerializable(PROP_BOUNDS_SIZE) as ArrayList<Double>
            val width = bounds[0].toFloat()
            val height = bounds[1].toFloat()
            return Pair(width, height)
        } else {
            return Pair(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        }
    }

}