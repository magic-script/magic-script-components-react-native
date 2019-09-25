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
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.CustomScrollBar
import com.reactlibrary.utils.FontProvider
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils
import kotlinx.android.synthetic.main.scroll_bar.*

class UiScrollBarNode(initProps: ReadableMap, context: Context) :
        UiNode(initProps, context, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_THUMB_POSITION = "thumbPosition"
        const val PROP_THUMB_SIZE = "thumbSize"
        
        const val DEFAULT_WIDTH = 0.03359 * 8
        const val DEFAULT_HEIGHT = 0.03359
        const val DEFAULT_THUMB_POSITION = 0.0
        const val DEFAULT_THUMB_SIZE = 0.33
    }

    var toggleChangedListener: ((on: Boolean) -> Unit)? = null

    // set default properties values
    init {
        if (!properties.containsKey(PROP_WIDTH)) {
            properties.putDouble(PROP_WIDTH, DEFAULT_WIDTH)
        }

        if (!properties.containsKey(PROP_HEIGHT)) {
            properties.putDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        }

        if (!properties.containsKey(PROP_THUMB_POSITION)) {
            properties.putDouble(PROP_THUMB_POSITION, DEFAULT_THUMB_POSITION)
        }

        if (!properties.containsKey(PROP_THUMB_SIZE)) {
            properties.putDouble(PROP_THUMB_SIZE, DEFAULT_THUMB_SIZE)
        }
    }

    // fun onTouchCallback(v: View, event: MotionEvent): Boolean {
        // throw Exception("Hi There!")
    // }
    
    override fun provideView(context: Context): View {
        val view = CustomScrollBar(context)
        view.setOnTouchListener{ _: View, event: MotionEvent ->
            view.onTouchCallback(event)
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
    }

    override fun setViewSize() {
        val widthInMeters = properties.getDouble(PROP_WIDTH).toFloat()
        val widthPx = Utils.metersToPx(widthInMeters, context)

        val heightInMeters = properties.getDouble(PROP_HEIGHT).toFloat()
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
}