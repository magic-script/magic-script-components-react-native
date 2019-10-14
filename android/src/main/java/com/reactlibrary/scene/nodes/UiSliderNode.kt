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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.CustomSlider
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.putDefaultDouble

open class UiSliderNode(initProps: ReadableMap, context: Context, viewRenderableLoader: ViewRenderableLoader)
    : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_VALUE = "value"
        const val PROP_MIN = "min"
        const val PROP_MAX = "max"

        const val DEFAULT_WIDTH = 0.5
        const val DEFAULT_HEIGHT = 0.018
    }

    fun setOnSliderChangedListener(listener: (value: Float) -> Unit) {
        (view as CustomSlider).onScrollChangeListener = { value ->
            listener(value)
        }
    }

    init {
        properties.putDefaultDouble(PROP_WIDTH, DEFAULT_WIDTH)
        properties.putDefaultDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.slider, null)
    }

    override fun setupView() {
        val widthInMeters = properties.getDouble(PROP_WIDTH).toFloat()
        val widthPx = Utils.metersToPx(widthInMeters, context)

        val heightInMeters = properties.getDouble(PROP_HEIGHT).toFloat()
        val heightPx = Utils.metersToPx(heightInMeters, context)

        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        setValue(props)
        setMin(props)
        setMax(props)
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for slider?
    }

    private fun setValue(props: Bundle) {
        if (props.containsKey(PROP_VALUE)) {
            val value = props.getDouble(PROP_VALUE).toFloat()
            (view as CustomSlider).value = value
        }
    }

    private fun setMin(props: Bundle) {
        if (props.containsKey(PROP_MIN)) {
            val min = props.getDouble(PROP_MIN).toFloat()
            (view as CustomSlider).min = min
        }
    }

    private fun setMax(props: Bundle) {
        if (props.containsKey(PROP_MAX)) {
            val max = props.getDouble(PROP_MAX).toFloat()
            (view as CustomSlider).max = max
        }
    }

}