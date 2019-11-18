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

package com.reactlibrary.scene.nodes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Vector2
import com.reactlibrary.utils.putDefault

class UiCircleConfirmationNode(initProps: ReadableMap,
                               context: Context,
                               viewRenderableLoader: ViewRenderableLoader)
    : UiNode(initProps, context, viewRenderableLoader, useContentNodeAlignment = true) {

    var onConfirmationCompletedListener: (() -> Unit)? = null
    var onConfirmationCanceledListener: (() -> Unit)? = null
    var onConfirmationUpdatedListener: ((value: Float) -> Unit)? = null

    companion object {
        // properties
        const val PROP_HEIGHT = "height" // radius of a circle

        const val DEFAULT_HEIGHT = 0.1
    }

    init {
        properties.putDefault(PROP_HEIGHT, DEFAULT_HEIGHT)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.spinner, null)
    }

    override fun provideDesiredSize(): Vector2 {
        val radius = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        val height = (radius * 2).toFloat()
        return Vector2(height, height)
    }

}