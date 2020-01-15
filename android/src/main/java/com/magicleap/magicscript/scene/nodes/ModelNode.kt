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
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.magicleap.magicscript.ar.ModelRenderableLoader
import com.magicleap.magicscript.ar.RenderableAnimator
import com.magicleap.magicscript.ar.RenderableResult
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read
import com.magicleap.magicscript.utils.readFilePath

class ModelNode(
    initProps: ReadableMap,
    private val context: Context,
    private val modelRenderableLoader: ModelRenderableLoader,
    private val renderableAnimator: RenderableAnimator
) : TransformNode(initProps, hasRenderable = true, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_MODEL_PATH = "modelPath"
        // initial resource scale applied when loading model (cannot be updated)
        const val PROP_IMPORT_SCALE = "importScale"

        const val DEFAULT_IMPORT_SCALE = 1.0
    }

    private var renderableCopy: ModelRenderable? = null

    init {
        properties.putDefault(PROP_IMPORT_SCALE, DEFAULT_IMPORT_SCALE)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        setModelPath(props)
        setImportScale(props)
    }

    override fun loadRenderable() {
        loadModel()
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for Model
    }

    override fun setClipBounds(clipBounds: Bounding) {
        val contentPosition = getContentPosition()
        val bounds = getBounding()
        val centerOffsetX = bounds.center().x - contentPosition.x
        val centerOffsetY = bounds.center().y - contentPosition.y

        if (contentPosition.x + centerOffsetX in clipBounds.left..clipBounds.right
            && contentPosition.y + centerOffsetY in clipBounds.bottom..clipBounds.top
        ) {
            show()
        } else {
            hide()
        }
    }

    override fun getContentBounding(): Bounding {
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

    private fun setModelPath(props: Bundle) {
        if (props.containsKey(PROP_MODEL_PATH)) {
            // cannot update the ModelRenderable before [renderableRequested],
            // because Sceneform may be uninitialized yet
            // (loadRenderable may have not been called)
            if (renderableRequested) {
                loadModel()
            }
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
            modelRenderableLoader.loadRenderable(modelUri) { result ->
                if (result is RenderableResult.Success) {
                    this.renderableCopy = result.renderable
                    renderableAnimator.play(result.renderable)
                    if (isVisible) {
                        contentNode.renderable = renderableCopy
                    }
                }
            }
        }
    }

}