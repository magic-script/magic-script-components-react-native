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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.font.FontProvider
import com.reactlibrary.icons.ToggleIconsProvider
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.*
import kotlinx.android.synthetic.main.toggle.view.*

open class UiToggleNode(initProps: ReadableMap,
                        context: Context,
                        viewRenderableLoader: ViewRenderableLoader,
                        private val fontProvider: FontProvider,
                        private val toggleIconsProvider: ToggleIconsProvider
) :
        UiNode(initProps, context, viewRenderableLoader, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_HEIGHT = "height"
        const val PROP_CHECKED = "on"
        const val PROP_TEXT = "text"
        const val PROP_TYPE = "type"
        const val PROP_TEXT_SIZE = "textSize"
        const val PROP_TEXT_COLOR = "textColor"

        const val DEFAULT_HEIGHT = 0.03359 // in meters
        const val SWITCH_WIDTH_TO_HEIGHT_RATIO = 2
        const val SWITCH_SPACING_RATIO = 0.75F // spacing from text (relative to switch width)
        const val TYPE_DEFAULT = "default" // switch
        const val TYPE_CHECKBOX = "checkbox"
        const val TYPE_RADIO = "radio"
    }

    var toggleChangedListener: ((on: Boolean) -> Unit)? = null

    var isOn = false
        set(value) {
            if (value != field) {
                toggleChangedListener?.invoke(value)
            }
            field = value
            refreshImage()
        }

    init {
        // set default properties values
        properties.putDefault(PROP_HEIGHT, DEFAULT_HEIGHT)
        properties.putDefault(PROP_TYPE, TYPE_DEFAULT)
        val height = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        properties.putDefault(PROP_TEXT_SIZE, height)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.toggle, null)
    }

    override fun provideDesiredSize(): Vector2 {
        // size is set for nested views, see setupView()
        return Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
    }

    override fun setupView() {
        var heightMeters = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT).toFloat()
        if (heightMeters == WRAP_CONTENT_DIMENSION) {
            heightMeters = DEFAULT_HEIGHT.toFloat()
        }
        val switchHeightPx = Utils.metersToPx(heightMeters, context)
        val switchWidthPx = switchHeightPx * SWITCH_WIDTH_TO_HEIGHT_RATIO

        val switchParams = LinearLayout.LayoutParams(switchWidthPx, switchHeightPx)
        switchParams.leftMargin = (SWITCH_SPACING_RATIO * switchWidthPx).toInt()
        view.iv_toggle.layoutParams = switchParams

        view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )

        view.tv_toggle.typeface = fontProvider.provideFont()
        setupClickListener()
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        setType(props)
        setIsChecked(props)
        setText(props)
        setTextSize(props)
        setTextColor(props)
    }

    override fun applyAlignment() {
        // hardcoding the "pivot" point at the center of switch;
        // alignment cannot be changed for toggle according to Lumin implementation
        val bounds = getContentBounding()
        val nodeWidth = bounds.size().x
        val pivotOffsetX = -bounds.center().x // aligning according to center

        val switchHeight = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        val switchWidth = switchHeight * SWITCH_WIDTH_TO_HEIGHT_RATIO

        val x = contentNode.localPosition.x + pivotOffsetX - nodeWidth / 2 + switchWidth / 2
        val pos = Vector3(x.toFloat(), contentNode.localPosition.y, contentNode.localPosition.z)
        contentNode.localPosition = pos
    }

    override fun setAlignment(props: Bundle) {
        // cannot override hardcoded alignment
    }

    private fun findToggleGroupParent(): ToggleGroupNode? {
        var parentNode = parent
        while (parentNode != null) {
            if (parentNode is ToggleGroupNode) {
                return parentNode
            }
            parentNode = parentNode.parent
        }
        return null
    }

    private fun refreshImage() {
        val iconType = properties.getString(PROP_TYPE, TYPE_DEFAULT)
        val iconId = toggleIconsProvider.provideIconId(iconType, isOn)
        view.iv_toggle.setImageResource(iconId)
    }

    private fun setType(props: Bundle) {
        props.ifContainsString(PROP_TYPE) {
            refreshImage()
        }
    }

    private fun setIsChecked(props: Bundle) {
        if (props.containsKey(PROP_CHECKED)) {
            val value = props.getBoolean(PROP_CHECKED)
            val toggleGroup = findToggleGroupParent()
            if (toggleGroup == null) {
                isOn = value
            } else {
                toggleGroup.setupToggle(this, wantBeActive = value)
            }
        }
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            view.tv_toggle.text = text
            setNeedsRebuild()
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE).toFloat()
            val size = Utils.metersToFontPx(sizeMeters, view.context).toFloat()
            view.tv_toggle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            setNeedsRebuild()
        }
    }

    private fun setTextColor(props: Bundle) {
        val color = PropertiesReader.readColor(props, PROP_TEXT_COLOR)
        if (color != null) {
            view.tv_toggle.setTextColor(color)
        }
    }

    private fun setupClickListener() {
        view.iv_toggle.setOnClickListener {
            // disabling parent view is not sufficient
            if (!properties.getBoolean(PROP_ENABLED)) {
                return@setOnClickListener
            }
            val toggleGroup = findToggleGroupParent()
            if (toggleGroup == null) {
                isOn = !isOn
            } else {
                toggleGroup.setupToggle(this, wantBeActive = !isOn)
            }
        }
    }

}