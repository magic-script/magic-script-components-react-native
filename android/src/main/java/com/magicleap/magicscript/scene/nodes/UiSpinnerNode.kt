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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.views.CustomSpinner
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.putDefault

open class UiSpinnerNode(
        initProps: ReadableMap,
        context: Context,
        viewRenderableLoader: ViewRenderableLoader
) : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        // properties
        const val PROP_HEIGHT = "height"
        const val PROP_DETERMINATE = "determinate" // if true we can set progress
        const val PROP_VALUE = "value" // progress

        const val DEFAULT_HEIGHT = 0.07
        const val DEFAULT_DETERMINATE = false
    }

    init {
        properties.putDefault(PROP_HEIGHT, DEFAULT_HEIGHT)
        properties.putDefault(PROP_DETERMINATE, DEFAULT_DETERMINATE)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.spinner, null)
    }

    override fun provideDesiredSize(): Vector2 {
        val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        val width = height
        return Vector2(width.toFloat(), height.toFloat())
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        setDeterminate(props)
        setValue(props)
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for spinner
    }

    private fun setDeterminate(props: Bundle) {
        if (props.containsKey(PROP_DETERMINATE)) {
            val isDeterminate = props.getBoolean(PROP_DETERMINATE)
            (view as CustomSpinner).type = if (isDeterminate) {
                CustomSpinner.Type.DETERMINATE
            } else {
                CustomSpinner.Type.INDETERMINATE
            }
        }
    }

    private fun setValue(props: Bundle) {
        if (!properties.getBoolean(PROP_DETERMINATE)) {
            return
        }

        if (props.containsKey(PROP_VALUE)) {
            (view as CustomSpinner).value = props.getDouble(PROP_VALUE).toFloat()
        }
    }

}