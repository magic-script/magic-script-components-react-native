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
import com.magicleap.magicscript.scene.nodes.views.CustomProgressBar
import com.magicleap.magicscript.utils.PropertiesReader
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.putDefault

open class UiProgressBarNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader
) : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_VALUE = "value" // progress
        const val PROP_MIN = "min" // min progress
        const val PROP_MAX = "max" // max progress
        const val PROP_PROGRESS_COLOR = "progressColor"
        const val PROP_PROGRESS_COLOR_BEGIN = "beginColor"
        const val PROP_PROGRESS_COLOR_END = "endColor"

        const val DEFAULT_WIDTH = 0.5
        const val DEFAULT_HEIGHT = 0.004
    }

    init {
        // set default values of properties
        properties.putDefault(PROP_WIDTH, DEFAULT_WIDTH)
        properties.putDefault(PROP_HEIGHT, DEFAULT_HEIGHT)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.progress_bar, null)
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

        setValue(props)
        setMin(props)
        setMax(props)
        setProgressColor(props)
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for progress bar
    }

    private fun setValue(props: Bundle) {
        if (props.containsKey(PROP_VALUE)) {
            val value = props.getDouble(PROP_VALUE).toFloat()
            (view as CustomProgressBar).value = value
        }
    }

    private fun setMin(props: Bundle) {
        if (props.containsKey(PROP_MIN)) {
            val min = props.getDouble(PROP_MIN).toFloat()
            (view as CustomProgressBar).min = min
        }
    }

    private fun setMax(props: Bundle) {
        if (props.containsKey(PROP_MAX)) {
            val max = props.getDouble(PROP_MAX).toFloat()
            (view as CustomProgressBar).max = max
        }
    }

    private fun setProgressColor(props: Bundle) {
        if (props.containsKey(PROP_PROGRESS_COLOR)) {
            val colorsBundle = props.getBundle(PROP_PROGRESS_COLOR)!!
            val beginColor = PropertiesReader.readColor(colorsBundle, PROP_PROGRESS_COLOR_BEGIN)
            val endColor = PropertiesReader.readColor(colorsBundle, PROP_PROGRESS_COLOR_END)
            if (beginColor != null) {
                (view as CustomProgressBar).beginColor = beginColor
            }
            if (endColor != null) {
                (view as CustomProgressBar).endColor = endColor
            }
        }
    }

}