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

package com.magicleap.magicscript.scene.nodes.prism

import android.os.Bundle
import android.view.MotionEvent
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.ar.CubeRenderableBuilder
import com.magicleap.magicscript.ar.RenderableResult
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.utils.logMessage
import com.magicleap.magicscript.utils.read

class Prism(initProps: ReadableMap) : AnchorNode() {

    companion object {
        const val PROP_SIZE = "size"
        const val PROP_POSITION = "position"
    }

    private var container: CustomTransformableNode? = null
    private var content: TransformNode? = null

    var beingTouched: Boolean = false

    var visible: Boolean = false
        set(value) {
            field = value
            container?.renderable = if (value) {
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

    init {
        setSize(properties)
        setPosition(properties)
    }

    fun build(transformationSystem: TransformationSystem, cubeBuilder: CubeRenderableBuilder) {
        if (container != null) {
            return
        }

        container = CustomTransformableNode(transformationSystem).also {
            it.setParent(this)
            if (content != null) {
                it.addChild(content)
            }
        }.apply {
            // disabling default translationController, because it only allows moving on planes
            translationController.isEnabled = false
            scaleController.isEnabled = false
        }

        attachRenderable(cubeBuilder)
    }

    fun addContent(node: TransformNode) {
        if (content != null) {
            logMessage("Content already added", true)
            return
        }

        content = node
        container?.let {
            it.setParent(this)
            it.addChild(node)
        }
    }

    private fun attachRenderable(cubeBuilder: CubeRenderableBuilder) {
        val color = Color(1f, 0f, 0f, 0.5f)
        cubeBuilder.buildRenderable(volume, Vector3.zero(), color, resultCallback = {
            if (it is RenderableResult.Success) {
                renderableCopy = it.renderable
                if (visible) {
                    container?.renderable = it.renderable
                }
            }
        })
    }

    private fun setSize(props: Bundle) {
        props.read<Vector3>(PROP_SIZE)?.let { size ->

        }
    }

    private fun setPosition(props: Bundle) {
        props.read<Vector3>(PROP_POSITION)?.let { size ->

        }
    }

    inner class CustomTransformableNode(transformationSystem: TransformationSystem) :
        TransformableNode(transformationSystem) {

        override fun onTouchEvent(hitTestResult: HitTestResult, motionEvent: MotionEvent): Boolean {
            // action up is for some reason not returned for AnchorNode, so we attach renderable and
            // process click events of the container node
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> beingTouched = true
                MotionEvent.ACTION_UP -> beingTouched = false
            }
            return super.onTouchEvent(hitTestResult, motionEvent)
        }

    }

}