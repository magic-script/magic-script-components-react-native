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

package com.magicleap.magicscript.scene.nodes

import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.utils.putDefault

/**
 * This node only holds information needed by ScrollViewNode
 * (it's mapped to a native scroll bar view)
 */
class UiScrollBarNode(initProps: ReadableMap) :
    TransformNode(initProps, false, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_THUMB_POSITION = "thumbPosition"
        const val PROP_THUMB_SIZE = "thumbSize"
        const val PROP_ORIENTATION = "orientation"

        const val ORIENTATION_VERTICAL = "vertical"
        const val ORIENTATION_HORIZONTAL = "horizontal"

        const val DEFAULT_ORIENTATION = ORIENTATION_VERTICAL
        const val DEFAULT_WIDTH = 0.04
        const val DEFAULT_HEIGHT = 1.2
        const val DEFAULT_THUMB_SIZE = 0.0
        const val DEFAULT_THUMB_POSITION = 0.0
    }

    val width: Float
        get() = properties.getDouble(PROP_WIDTH, DEFAULT_WIDTH).toFloat()

    val height: Float
        get() = properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT).toFloat()

    val thumbPosition: Float
        get() = properties.getDouble(PROP_THUMB_POSITION, DEFAULT_THUMB_POSITION).toFloat()

    val thumbSize: Float
        get() = properties.getDouble(PROP_THUMB_SIZE, DEFAULT_THUMB_SIZE).toFloat()

    val orientation: String
        get() = properties.getString(PROP_ORIENTATION, DEFAULT_ORIENTATION)

    init {
        // set default properties values
        properties.putDefault(PROP_WIDTH, DEFAULT_WIDTH)
        properties.putDefault(PROP_HEIGHT, DEFAULT_HEIGHT)
        properties.putDefault(PROP_THUMB_POSITION, DEFAULT_THUMB_POSITION)
        properties.putDefault(PROP_THUMB_SIZE, DEFAULT_THUMB_SIZE)
        properties.putDefault(PROP_ORIENTATION, ORIENTATION_VERTICAL)
    }
}
