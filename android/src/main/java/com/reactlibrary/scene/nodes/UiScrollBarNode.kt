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
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.CustomScrollBar
import com.reactlibrary.utils.Vector2
import com.reactlibrary.utils.putDefaultDouble
import com.reactlibrary.utils.putDefaultString

open class UiScrollBarNode(initProps: ReadableMap, context: Context, viewRenderableLoader: ViewRenderableLoader) :
        UiNode(initProps, context, viewRenderableLoader, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_THUMB_POSITION = "thumbPosition"
        const val PROP_THUMB_SIZE = "thumbSize"
        const val PROP_ORIENTATION = "orientation"

        const val ORIENTATION_VERTICAL = "vertical"
        const val ORIENTATION_HORIZONTAL = "horizontal"

        const val DEFAULT_WIDTH = 0.04
        const val DEFAULT_HEIGHT = 1.2
    }

    init {
        // set default properties values
        properties.putDefaultDouble(PROP_WIDTH, DEFAULT_WIDTH)
        properties.putDefaultDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        properties.putDefaultDouble(PROP_THUMB_POSITION, 0.0)
        properties.putDefaultDouble(PROP_THUMB_SIZE, 0.0)
        properties.putDefaultString(PROP_ORIENTATION, ORIENTATION_VERTICAL)
    }

    fun setOnScrollChangeListener(listener: ((on: Float) -> Unit)) {
        (view as CustomScrollBar).onScrollChangeListener = listener
    }

    override fun provideView(context: Context): View {
        return CustomScrollBar(context)
    }

    override fun provideDesiredSize(): Vector2 {
        val width = properties.getDouble(PROP_WIDTH, DEFAULT_WIDTH)
        val height = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        return Vector2(width.toFloat(), height.toFloat())
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
            val value = valueString != ORIENTATION_HORIZONTAL
            (view as CustomScrollBar).isVertical = value
        }
    }
}
