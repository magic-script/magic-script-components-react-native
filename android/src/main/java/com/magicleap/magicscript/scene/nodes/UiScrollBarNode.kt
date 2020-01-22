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
        const val PROP_LENGTH = "length"
        const val PROP_THICKNESS = "thickness"
        const val PROP_THUMB_POSITION = "thumbPosition"
        const val PROP_THUMB_SIZE = "thumbSize"
        const val PROP_ORIENTATION = "orientation"

        const val ORIENTATION_VERTICAL = "vertical"
        const val ORIENTATION_HORIZONTAL = "horizontal"

        const val DEFAULT_ORIENTATION = ORIENTATION_VERTICAL
        const val DEFAULT_LENGTH = 0.0
        const val DEFAULT_THICKNESS = 0.0
        const val DEFAULT_THUMB_POSITION = 0.0
        const val THUMB_SIZE_AUTO = 0.0 // calculated automatically
    }

    val length: Float
        get() = properties.getDouble(PROP_LENGTH, DEFAULT_LENGTH).toFloat()

    val thickness: Float
        get() = properties.getDouble(PROP_THICKNESS, DEFAULT_THICKNESS).toFloat()

    val thumbPosition: Float
        get() = properties.getDouble(PROP_THUMB_POSITION, DEFAULT_THUMB_POSITION).toFloat()

    val thumbSize: Float
        get() = properties.getDouble(PROP_THUMB_SIZE, THUMB_SIZE_AUTO).toFloat()

    val orientation: String
        get() = properties.getString(PROP_ORIENTATION, DEFAULT_ORIENTATION)

    init {
        // set default properties values
        properties.putDefault(PROP_LENGTH, DEFAULT_LENGTH)
        properties.putDefault(PROP_THICKNESS, DEFAULT_THICKNESS)
        properties.putDefault(PROP_THUMB_POSITION, DEFAULT_THUMB_POSITION)
        properties.putDefault(PROP_THUMB_SIZE, THUMB_SIZE_AUTO)
        properties.putDefault(PROP_ORIENTATION, ORIENTATION_VERTICAL)
    }
}
