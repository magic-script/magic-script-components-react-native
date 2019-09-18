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
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.FontProvider
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils
import kotlinx.android.synthetic.main.toggle.view.*

class UiToggleNode(initProps: ReadableMap, context: Context) :
        UiNode(initProps, context, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_HEIGHT = "height"
        const val PROP_CHECKED = "on"
        const val PROP_TEXT = "text"
        const val PROP_TEXT_SIZE = "textSize"
        const val PROP_TEXT_COLOR = "textColor"

        const val DEFAULT_HEIGHT = 0.03359 // in meters
        const val SWITCH_WIDTH_TO_HEIGHT_RATIO = 2
    }

    var toggleChangedListener: ((on: Boolean) -> Unit)? = null

    private var isOn = false

    init {
        // set default properties values
        if (!properties.containsKey(PROP_HEIGHT)) {
            properties.putDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        }

        if (!properties.containsKey(PROP_TEXT_SIZE)) {
            val height = properties.getDouble(PROP_HEIGHT)
            properties.putDouble(PROP_TEXT_SIZE, height)
        }
    }

    override fun provideView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.toggle, null)
        view.tv_toggle.typeface = FontProvider.provideFont(context)
        view.iv_toggle.setOnClickListener {
            isOn = !isOn
            refreshImage()
            toggleChangedListener?.invoke(isOn)
        }
        return view
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        setIsChecked(props)
        setText(props)
        setTextSize(props)
        setTextColor(props)
    }

    override fun setViewSize() {
        var heightMeters = properties.getDouble(PROP_HEIGHT).toFloat()
        if (heightMeters == 0F) { // use default height when 0
            heightMeters = DEFAULT_HEIGHT.toFloat()
        }
        val switchHeightPx = Utils.metersToPx(heightMeters, context)
        val switchWidthPx = switchHeightPx * SWITCH_WIDTH_TO_HEIGHT_RATIO

        val switchParams = LinearLayout.LayoutParams(switchWidthPx, switchHeightPx)
        switchParams.leftMargin = (0.75 * switchWidthPx).toInt()
        view.iv_toggle.layoutParams = switchParams

        view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun applyAlignment() {
        // hardcoding the "pivot" point at the center of switch;
        // alignment cannot be changed for toggle according to Lumin implementation
        val bounds = getBounding()
        val nodeWidth = bounds.right - bounds.left
        val boundsCenterX = bounds.left + nodeWidth / 2
        val pivotOffsetX = localPosition.x - boundsCenterX // aligning according to center

        val switchHeight = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        val switchWidth = switchHeight * SWITCH_WIDTH_TO_HEIGHT_RATIO

        val x = contentNode.localPosition.x + pivotOffsetX - nodeWidth / 2 + switchWidth / 2
        val pos = Vector3(x.toFloat(), contentNode.localPosition.y, contentNode.localPosition.z)
        contentNode.localPosition = pos
    }

    private fun refreshImage() {
        if (isOn) {
            view.iv_toggle.setImageResource(R.drawable.toggle_on)
        } else {
            view.iv_toggle.setImageResource(R.drawable.toggle_off)
        }
    }

    private fun setIsChecked(props: Bundle) {
        if (props.containsKey(PROP_CHECKED)) {
            isOn = props.getBoolean(PROP_CHECKED)
            refreshImage()
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
            val size = Utils.metersToPx(sizeMeters, view.context).toFloat()
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

}