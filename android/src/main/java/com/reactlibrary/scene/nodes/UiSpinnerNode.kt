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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.CustomSpinner
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.putDefaultBoolean
import com.reactlibrary.utils.putDefaultDouble

open class UiSpinnerNode(initProps: ReadableMap, context: Context) : UiNode(initProps, context, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_HEIGHT = "height"
        const val PROP_DETERMINATE = "determinate" // if true we can set progress
        const val PROP_VALUE = "value" // progress

        const val DEFAULT_HEIGHT = 0.07
        const val DEFAULT_DETERMINATE = false
        const val INDETERMINATE_VALUE = 0.25F // 45 degrees
    }

    private val rotationSpeed = 360f // angle per second
    private var currentAngle = 0f

    init {
        properties.putDefaultDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        properties.putDefaultBoolean(PROP_DETERMINATE, DEFAULT_DETERMINATE)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.spinner, null)
    }

    override fun onUpdate(frameTime: FrameTime) {
        super.onUpdate(frameTime)
        if (!properties.getBoolean(PROP_DETERMINATE)) {
            // View animation is buggy, so we rotate the Node
            currentAngle -= rotationSpeed * frameTime.deltaSeconds
            contentNode.localRotation = Quaternion.axisAngle(Vector3(0f, 0f, 1f), currentAngle)
        }
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

    override fun setViewSize() {
        val height = properties.getDouble(PROP_HEIGHT).toFloat()
        val widthPx = Utils.metersToPx(height, context)
        val heightPx = Utils.metersToPx(height, context)
        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

    private fun setDeterminate(props: Bundle) {
        if (props.containsKey(PROP_DETERMINATE)) {
            if (!props.getBoolean(PROP_DETERMINATE)) {
                (view as CustomSpinner).value = INDETERMINATE_VALUE
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