/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.toggle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.readColor

open class UiToggleNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    private val toggleViewManager: ToggleViewManager
) : UiNode(initProps, context, viewRenderableLoader, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_HEIGHT = "height"
        const val PROP_CHECKED = "on"
        const val PROP_TEXT = "text"
        const val PROP_TYPE = "type"
        const val PROP_TEXT_SIZE = "textSize"
        const val PROP_TEXT_COLOR = "textColor"

        const val TYPE_DEFAULT = "default" // switch
        const val TYPE_CHECKBOX = "checkbox"
        const val TYPE_RADIO = "radio"

        const val DEFAULT_HEIGHT = 0.03359 // in meters
    }

    var toggleChangedListener: ((on: Boolean) -> Unit)? = null

    var isOn = false
        set(value) {
            if (value != field) {
                toggleChangedListener?.invoke(value)
            }
            field = value
            toggleViewManager.setActive(value)
        }

    private val toggleClickListener = {
        // disabling parent view is not sufficient
        if (properties.getBoolean(PROP_ENABLED)) {
            val toggleGroup = findToggleGroupParent()
            if (toggleGroup == null) {
                isOn = !isOn
            } else {
                toggleGroup.updateToggle(this, wantBeActive = !isOn)
            }
        }
    }

    init {
        // set default properties values
        properties.putDefault(PROP_HEIGHT, DEFAULT_HEIGHT)
        properties.putDefault(PROP_TYPE, TYPE_DEFAULT)
        val height = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        properties.putDefault(PROP_TEXT_SIZE, height)
    }

    // container
    override fun provideView(context: Context): View {
        return LinearLayout(context)
    }

    override fun provideDesiredSize(): Vector2 {
        // size is set for nested views, see setupView()
        return Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
    }

    override fun setupView() {
        val textView = LayoutInflater.from(context).inflate(R.layout.toggle_text, null) as TextView
        val imageView =
            LayoutInflater.from(context).inflate(R.layout.toggle_switch, null) as ImageView
        val type = properties.getString(PROP_TYPE, TYPE_DEFAULT)

        var heightMeters = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT).toFloat()
        if (heightMeters == WRAP_CONTENT_DIMENSION) {
            heightMeters = DEFAULT_HEIGHT.toFloat()
        }

        val toggleConfig = ToggleConfig(
            toggleType = type,
            toggleHeight = heightMeters,
            container = view as ViewGroup,
            imageView = imageView,
            textView = textView,
            onToggleClickListener = this.toggleClickListener
        )
        toggleViewManager.setupToggleView(context, toggleConfig)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_HEIGHT) || props.containsKey(PROP_TYPE)) {
            setNeedsRebuild()
        }
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

        val type = properties.getString(PROP_TYPE, TYPE_DEFAULT)
        val switchHeight = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT).toFloat()
        val switchWidth = toggleViewManager.getToggleWidth(type, switchHeight)

        val x = if (type == TYPE_DEFAULT) {
            contentNode.localPosition.x + pivotOffsetX - nodeWidth / 2 + switchWidth / 2
        } else {
            contentNode.localPosition.x + pivotOffsetX + nodeWidth / 2 - switchWidth / 2
        }
        val pos = Vector3(x, contentNode.localPosition.y, contentNode.localPosition.z)
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

    private fun setIsChecked(props: Bundle) {
        if (props.containsKey(PROP_CHECKED)) {
            val value = props.getBoolean(PROP_CHECKED)
            val toggleGroup = findToggleGroupParent()
            if (toggleGroup == null) {
                isOn = value
            } else {
                toggleGroup.updateToggle(this, wantBeActive = value)
            }
        }
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            toggleViewManager.setText(text)
            setNeedsRebuild()
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE).toFloat()
            val size = Utils.metersToFontPx(sizeMeters, view.context)
            toggleViewManager.setTextSize(size)
            setNeedsRebuild()
        }
    }

    private fun setTextColor(props: Bundle) {
        val color = props.readColor(PROP_TEXT_COLOR)
        if (color != null) {
            toggleViewManager.setTextColor(color)
        }
    }

}