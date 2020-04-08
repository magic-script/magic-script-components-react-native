/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.magicleap.magicscript.ar.RenderableAnimator
import com.magicleap.magicscript.ar.renderable.ModelRenderableLoader
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.utils.*

class ModelNode(
    initProps: ReadableMap,
    private val context: Context,
    private val modelRenderableLoader: ModelRenderableLoader,
    private val renderableAnimator: RenderableAnimator
) : TransformNode(initProps, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_MODEL_PATH = "modelPath"
        // initial resource scale applied when loading model (cannot be updated)
        const val PROP_IMPORT_SCALE = "importScale"

        const val DEFAULT_IMPORT_SCALE = 1.0
    }

    override var clipBounds: AABB?
        get() = super.clipBounds
        set(value) {
            super.clipBounds = value
            applyClipBounds()
        }

    private var renderableLoadRequest: ModelRenderableLoader.LoadRequest? = null
    private var renderableCopy: ModelRenderable? = null

    init {
        properties.putDefault(PROP_IMPORT_SCALE, DEFAULT_IMPORT_SCALE)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        setModelPath(props)
        setImportScale(props)
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for Model
    }

    override fun onTransformedLocally() {
        super.onTransformedLocally()
        applyClipBounds()
    }

    override fun getContentBounding(): AABB {
        return if (isVisible) {
            Utils.calculateBoundsOfNode(contentNode, contentNode.collisionShape)
        } else { // calculate bounding based on [renderableCopy]
            Utils.calculateBoundsOfNode(contentNode, renderableCopy?.collisionShape)
        }
    }

    override fun onVisibilityChanged(visibility: Boolean) {
        super.onVisibilityChanged(visibility)
        if (visibility) {
            contentNode.renderable = renderableCopy
        } else {
            contentNode.renderable = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        renderableLoadRequest?.let {
            modelRenderableLoader.cancel(it)
        }
    }

    private fun setModelPath(props: Bundle) {
        if (props.containsKey(PROP_MODEL_PATH)) {
            loadModel()
        }
    }

    private fun setImportScale(props: Bundle) {
        val importScale = props.read<Double>(PROP_IMPORT_SCALE)?.toFloat()
        if (importScale != null) {
            // applying the import scale on content node for simplicity, because
            // localScale may be changed by a layout that has limited size
            contentNode.localScale = Vector3(importScale, importScale, importScale)
        }
    }

    private fun loadModel() {
        val modelUri = properties.readFilePath(PROP_MODEL_PATH, context)
        if (modelUri != null) {
            // cancel previous request if exists
            renderableLoadRequest?.let {
                modelRenderableLoader.cancel(it)
            }

            renderableLoadRequest = ModelRenderableLoader.LoadRequest(modelUri) { result ->
                if (result is DataResult.Success) {
                    this.renderableCopy = result.data
                    renderableAnimator.play(result.data)
                    if (isVisible) {
                        contentNode.renderable = renderableCopy
                    }
                }
            }.also {
                modelRenderableLoader.loadRenderable(it)
            }
        }
    }

    private fun applyClipBounds() {
        clipBounds?.let {
            if (insideClipBounds(it)) {
                show()
            } else {
                hide()
            }
        }
    }

    private fun insideClipBounds(clipBounds: AABB): Boolean {
        val contentPosition = getContentPosition()
        val bounds = getBounding()

        val centerOffsetX = bounds.center().x - contentPosition.x
        val centerOffsetY = bounds.center().y - contentPosition.y
        val centerOffsetZ = bounds.center().z - contentPosition.z

        return contentPosition.x + centerOffsetX in clipBounds.min.x..clipBounds.max.x
                && contentPosition.y + centerOffsetY in clipBounds.min.y..clipBounds.max.y
                && contentPosition.z + centerOffsetZ in clipBounds.min.z..clipBounds.max.z
    }

}