/*
 * Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.ar.renderable.CubeRenderableBuilder
import com.magicleap.magicscript.ar.renderable.RenderableResult

/**
 * Prism content that can be scaled and rotated manually.
 * This node is a direct parent of the Prism children declared in React Native.
 */
class PrismContentNode(
    transformationSystem: TransformationSystem,
    private val cubeBuilder: CubeRenderableBuilder,
    initialSize: Vector3
) : TransformableNode(transformationSystem) {
    private var renderableCopy: Renderable? = null

    var editModeActive: Boolean = false
        set(value) {
            field = value
            if (value) {
                renderable = renderableCopy
                transformationSystem.selectNode(this)
            } else {
                renderable = null
                // deselect the node
                transformationSystem.selectNode(null)
            }
        }

    var extendedScaleController: ExtendedScaleController
        private set

    var scaleChangedListener: ((scale: Vector3) -> Unit)? = null

    private var lastScale = localScale
    private var renderableLoadRequest: CubeRenderableBuilder.LoadRequest? = null

    init {
        // disabling default translationController, because it only allows moving on planes
        translationController.isEnabled = false

        // disable default scale controller
        scaleController.isEnabled = false

        transformationSystem.selectionVisualizer = EmptySelectionVisualizer()

        val pinchRecognizer = transformationSystem.pinchRecognizer
        extendedScaleController = ExtendedScaleController(this, pinchRecognizer)
        addTransformationController(extendedScaleController)

        loadCube(initialSize)
    }

    fun setSize(size: Vector3) {
        loadCube(size)
    }

    fun onDestroy() {
        renderableLoadRequest?.let {
            cubeBuilder.cancel(it)
        }
    }

    override fun onTransformChange(node: Node) {
        super.onTransformChange(node)
        if (localScale != lastScale) {
            scaleChangedListener?.invoke(localScale)
            lastScale = localScale
        }
    }

    private fun loadCube(size: Vector3) {
        // cancel previous load task if exists
        renderableLoadRequest?.let {
            cubeBuilder.cancel(it)
        }

        val color = Color(1f, 0f, 0f, 0.5f)
        renderableLoadRequest = CubeRenderableBuilder.LoadRequest(size, Vector3.zero(), color) {
            if (it is RenderableResult.Success) {
                renderableCopy = it.renderable
                if (editModeActive) {
                    renderable = it.renderable
                }
            }
        }.also {
            cubeBuilder.buildRenderable(it)
        }
    }

}