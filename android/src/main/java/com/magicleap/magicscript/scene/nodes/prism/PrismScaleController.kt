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
import com.google.ar.sceneform.ux.BaseTransformationController
import com.google.ar.sceneform.ux.PinchGesture
import com.google.ar.sceneform.ux.PinchGestureRecognizer
import com.magicleap.magicscript.utils.minus
import kotlin.math.max
import kotlin.math.min

/**
 * Scale controller that works correctly even if the [transformableNode]
 * has specified different x, y and z local scale. In order to change the scale programmatically,
 * use the [setScale] function instead of [Node.setLocalScale] - this way the controller will
 * properly update the min and max scale limits.
 */
class PrismScaleController(
    transformableNode: PrismContentNode,
    gestureRecognizer: PinchGestureRecognizer
) : BaseTransformationController<PinchGesture>(transformableNode, gestureRecognizer) {

    var sensitivity = 0.75f

    private val minScaleFactor = 0.5f
    private val maxScaleFactor = 1.75f
    private var minScale = transformableNode.localScale.scaled(minScaleFactor)
    private var maxScale = transformableNode.localScale.scaled(maxScaleFactor)
    private var currentScaleRatio: Vector3

    init {
        currentScaleRatio = calculateScaleRatio(transformableNode.localScale)
    }

    /**
     * Sets the scale of the [transformableNode] as well as updates the min and max
     * allowed scale.
     */
    fun setScale(scale: Vector3) {
        minScale = scale.scaled(minScaleFactor)
        maxScale = scale.scaled(maxScaleFactor)
        currentScaleRatio = calculateScaleRatio(scale)
        transformableNode.localScale = getFinalScale()
    }

    override fun onActivated(node: Node?) {
        super.onActivated(node)
        setScale(transformableNode.localScale)
    }

    override fun canStartTransformation(gesture: PinchGesture) =
        (transformableNode as PrismContentNode).editModeActive

    override fun onContinueTransformation(gesture: PinchGesture) {
        val diff = gesture.gapDeltaInches() * sensitivity

        if (currentScaleRatio.x + diff in 0f..1f
            || currentScaleRatio.y + diff in 0f..1f
            || currentScaleRatio.z + diff in 0f..1f
        ) {
            currentScaleRatio.x += diff
            currentScaleRatio.y += diff
            currentScaleRatio.z += diff
            transformableNode.localScale = getFinalScale()
        }
    }

    override fun onEndTransformation(gesture: PinchGesture) {
        // no-op
    }

    private fun calculateScaleRatio(initialScale: Vector3): Vector3 {
        val scaleDelta = getScaleDelta()
        val ratioX = (initialScale.x - minScale.x) / scaleDelta.x
        val ratioY = (initialScale.y - minScale.y) / scaleDelta.y
        val ratioZ = (initialScale.z - minScale.z) / scaleDelta.z

        return Vector3(ratioX, ratioY, ratioZ)
    }

    private fun getScaleDelta() = maxScale - minScale

    private fun getClampedScaleRatio(): Vector3 {
        val x = min(1.0f, max(0.0f, currentScaleRatio.x))
        val y = min(1.0f, max(0.0f, currentScaleRatio.y))
        val z = min(1.0f, max(0.0f, currentScaleRatio.z))
        return Vector3(x, y, z)
    }

    private fun getFinalScale(): Vector3 {
        val scaleDelta = getScaleDelta()
        return getClampedScaleRatio().apply {
            x = x * scaleDelta.x + minScale.x
            y = y * scaleDelta.y + minScale.y
            z = z * scaleDelta.z + minScale.z
        }
    }

}