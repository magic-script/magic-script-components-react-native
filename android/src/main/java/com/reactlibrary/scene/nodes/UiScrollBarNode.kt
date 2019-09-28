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
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.CustomScrollBar
import com.reactlibrary.utils.putDefaulDouble
import com.reactlibrary.utils.putDefaulString
import com.reactlibrary.utils.Utils

class UiScrollBarNode(initProps: ReadableMap, context: Context) :
        UiNode(initProps, context, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_THUMB_POSITION = "thumbPosition"
        const val PROP_THUMB_SIZE = "thumbSize"
        const val PROP_ORIENTATION = "orientation"

        const val ORIENTATION_VERTICAL = "vertical"
        const val ORIENTATION_HORIZONTAL = "horizontal"
    }

    var scrollChangeListener: ((on: Float) -> Unit)? = null

    init {
        // set default properties values
        properties.putDefaulDouble(PROP_WIDTH, 0.04)
        properties.putDefaulDouble(PROP_HEIGHT, 1.2)
        properties.putDefaulDouble(PROP_THUMB_POSITION, 0.0)
        properties.putDefaulDouble(PROP_THUMB_SIZE, 0.0)
        properties.putDefaulString(PROP_ORIENTATION, ORIENTATION_VERTICAL)
    }

    override fun provideView(context: Context): View {
        val view = CustomScrollBar(context)
        view.setOnTouchListener{ _: View, event: MotionEvent ->
            view.touchCallback(event)
            scrollChangeListener?.invoke(view.thumbPosition)
            true
        }
        return view
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        setThumbPosition(props)
        setThumbSize(props)
        setOrientation(props)
    }

    override fun setViewSize() {
        val widthInMeters = this.properties.getDouble(PROP_WIDTH).toFloat()
        val widthPx = Utils.metersToPx(widthInMeters, context)

        val heightInMeters = this.properties.getDouble(PROP_HEIGHT).toFloat()
        val heightPx = Utils.metersToPx(heightInMeters, context)

        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

    private fun setThumbPosition(props: Bundle) {
        if (props.containsKey(PROP_THUMB_POSITION)) {
            val value = props.getDouble(PROP_THUMB_POSITION).toFloat()
            (view as CustomScrollBar).thumbPosition = value
        }
    }

    private fun setThumbSize(props: Bundle) {
        if (props.containsKey(PROP_THUMB_SIZE)) {
            val value = props.getDouble(PROP_THUMB_SIZE).toFloat()
            (view as CustomScrollBar).thumbSize = value
        }
    }

    private fun setOrientation(props: Bundle) {
        if (props.containsKey(PROP_ORIENTATION)) {
            val valueString = props.getString(PROP_ORIENTATION)
            val value = if ( valueString==ORIENTATION_VERTICAL ){ true } 
                else { false }
            (view as CustomScrollBar).isVertical = value
        }
    }
}
