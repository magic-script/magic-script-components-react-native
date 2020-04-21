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

import android.net.Uri
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.ar.renderable.CubeRenderableBuilder
import com.magicleap.magicscript.ar.renderable.ModelRenderableLoader
import com.magicleap.magicscript.utils.DataResult

/**
 * Prism content that can be scaled and rotated manually.
 * This node is a direct parent of the Prism children declared in React Native.
 */
class PrismContentNode(
    transformationSystem: TransformationSystem,
    private val modelLoader: ModelRenderableLoader,
    private val cubeBuilder: CubeRenderableBuilder,
    initialSize: Vector3,
    private val cornerModelPath: Uri
) : TransformableNode(transformationSystem) {
    private var boxModelCopy: Renderable? = null
    private var cornerModel: ModelRenderable? = null
    private val cornerNodes = mutableListOf<Node>()

    var size: Vector3 = initialSize
        set(value) {
            field = value
            loadBoxModel(value)
            layoutCornerNodes()
        }

    var editModeActive: Boolean = false
        set(value) {
            field = value
            renderable = if (value) boxModelCopy else null
            cornerNodes.forEach {
                it.renderable = if (value) cornerModel else null
            }
        }

    var prismDragController: PrismDragController
        private set

    var prismScaleController: PrismScaleController
        private set

    var prismRotationController: PrismRotationController
        private set

    var scaleChangedListener: ((scale: Vector3) -> Unit)? = null

    private var lastScale = localScale
    private var cubeLoadRequest: CubeRenderableBuilder.LoadRequest? = null
    private var modelLoadRequest: ModelRenderableLoader.LoadRequest? = null

    init {
        transformationSystem.selectionVisualizer = EmptySelectionVisualizer()
        disableDefaultControllers()

        val dragRecognizer = transformationSystem.dragRecognizer
        prismDragController = PrismDragController(this, dragRecognizer)
        addTransformationController(prismDragController)

        val pinchRecognizer = transformationSystem.pinchRecognizer
        prismScaleController = PrismScaleController(this, pinchRecognizer)
        addTransformationController(prismScaleController)

        val twistRecognizer = transformationSystem.twistRecognizer
        prismRotationController = PrismRotationController(this, twistRecognizer)
        addTransformationController(rotationController)

        createCornerNodes()
        loadCornerModel()
        layoutCornerNodes()
        loadBoxModel(initialSize)
    }

    fun onDestroy() {
        cubeLoadRequest?.let {
            cubeBuilder.cancel(it)
        }

        modelLoadRequest?.let {
            modelLoader.cancel(it)
        }
    }

    override fun onTransformChange(node: Node) {
        super.onTransformChange(node)
        if (localScale != lastScale) {
            scaleChangedListener?.invoke(localScale)
            lastScale = localScale
            adjustCornersScale()
        }
    }

    private fun loadBoxModel(size: Vector3) {
        // cancel previous load task if exists
        cubeLoadRequest?.let {
            cubeBuilder.cancel(it)
        }

        val color = Color(0.5f, 0.5f, 0.5f, 0.1f)
        cubeLoadRequest = CubeRenderableBuilder.LoadRequest(
            cubeSize = size,
            cubeCenter = Vector3.zero(),
            color = color,
            roughness = 1f,
            reflectance = 0f
        ) { result ->
            if (result is DataResult.Success) {
                boxModelCopy = result.data
                if (editModeActive) {
                    renderable = result.data
                }
            }
        }.also {
            cubeBuilder.buildRenderable(it)
        }
    }

    private fun loadCornerModel() {
        // cancel previous load task if exists
        modelLoadRequest?.let {
            modelLoader.cancel(it)
        }

        val recenterMode = RenderableSource.RecenterMode.NONE
        modelLoadRequest =
            ModelRenderableLoader.LoadRequest(cornerModelPath, recenterMode) { result ->
                if (result is DataResult.Success) {
                    cornerModel = result.data
                    if (editModeActive) {
                        cornerNodes.forEach {
                            it.renderable = result.data
                        }
                    }
                }
            }.also {
                modelLoader.loadRenderable(it)
            }
    }

    private fun disableDefaultControllers() {
        // disable default translation controller, because it only allows moving on planes
        translationController.isEnabled = false

        // disable default scale controller
        scaleController.isEnabled = false

        // disable default rotation controller
        rotationController.isEnabled = false
    }

    private fun createCornerNodes() {
        for (i in 0 until 8) {
            val cornerNode = Node()
            cornerNodes.add(cornerNode)
            addChild(cornerNode)
        }
    }

    private fun layoutCornerNodes() {
        // start at left-bottom-far
        val startPos = Vector3(-size.x / 2, -size.y / 2, -size.z / 2)
        val currentPosition = Vector3(startPos.x, startPos.y, startPos.z)

        val rotations = listOf<Quaternion>(
            Quaternion.eulerAngles(Vector3(0f, 90f, 0f)),
            Quaternion.eulerAngles(Vector3(0f, 0f, 0f)),
            Quaternion.eulerAngles(Vector3(0f, 0f, 180f)),
            Quaternion.eulerAngles(Vector3(0f, 0f, 90f)),
            Quaternion.eulerAngles(Vector3(0f, 180f, 0f)),
            Quaternion.eulerAngles(Vector3(0f, -90f, 0f)),
            Quaternion.eulerAngles(Vector3(-180f, 0f, -90f)),
            Quaternion.eulerAngles(Vector3(180f, 0f, 0f))
        )

        var idx = 0
        do {
            cornerNodes[idx].localPosition = currentPosition
            cornerNodes[idx].localRotation = rotations[idx]

            idx++
            val x = idx.and(1)
            val y = idx.and(2)
            val z = idx.and(4)

            currentPosition.x = if (x > 0) startPos.x + size.x else startPos.x
            currentPosition.y = if (y > 0) startPos.y + size.y else startPos.y
            currentPosition.z = if (z > 0) startPos.z + size.z else startPos.z

        } while (idx < 8)

    }

    // assigning world scale to corners,
    // because they should be independent of content scale
    private fun adjustCornersScale() {
        cornerNodes.forEach {
            it.worldScale = Vector3.one()
        }
    }

}