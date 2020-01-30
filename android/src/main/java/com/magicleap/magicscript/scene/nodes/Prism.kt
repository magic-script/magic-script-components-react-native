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

import android.os.Bundle
import android.view.MotionEvent
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.ar.CubeRenderableBuilder
import com.magicleap.magicscript.ar.RenderableResult

class Prism(
    initProps: ReadableMap,
    private val cubeRenderableBuilder: CubeRenderableBuilder,
    transformationSystem: TransformationSystem
) : TransformableNode(transformationSystem) {

    companion object {
        const val PROP_VOLUME = "volume"
    }

    var beingTouched = false

    var visible: Boolean = false
        set(value) {
            field = value
            renderable = if (value) {
                renderableCopy
            } else {
                null
            }
        }

    /**
     * All node's properties (packed to Bundle to avoid "already consumed"
     * exceptions when reading from [ReadableMap])
     */
    private val properties = Arguments.toBundle(initProps) ?: Bundle()

    private var volume: Vector3 = Vector3(1.5f, 1.5f, 0.5f)

    private var renderableCopy: Renderable? = null

    fun attachRenderable() {
        val color = Color(1f, 0f, 0f, 0.25f)
        cubeRenderableBuilder.buildRenderable(volume, Vector3.zero(), color, resultCallback = {
            if (it is RenderableResult.Success) {
                renderableCopy = it.renderable
                if (visible) {
                    renderable = it.renderable
                }
            }
        })
    }


    override fun onTouchEvent(hitTestResult: HitTestResult, motionEvent: MotionEvent): Boolean {
        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> beingTouched = true
            MotionEvent.ACTION_UP -> beingTouched = false
        }
        return super.onTouchEvent(hitTestResult, motionEvent)
    }
}