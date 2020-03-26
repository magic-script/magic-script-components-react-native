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

package com.magicleap.magicscript.ar

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.BaseTransformableNode
import com.google.ar.sceneform.ux.BaseTransformationController
import com.google.ar.sceneform.ux.PinchGesture
import com.google.ar.sceneform.ux.PinchGestureRecognizer
import kotlin.math.max
import kotlin.math.min

/**
 * Scale controller that works correctly even if the [transformableNode]
 * has specified different x, y and z local scale
 */
class ExtendedScaleController(
    transformableNode: BaseTransformableNode,
    gestureRecognizer: PinchGestureRecognizer
) : BaseTransformationController<PinchGesture>(transformableNode, gestureRecognizer) {

    var minScale = 0.01f
    var maxScale = 2f
    var sensitivity = 0.75f

    private lateinit var currentScaleRatio: Vector3

    override fun onActivated(node: Node?) {
        super.onActivated(node)
        val scale = transformableNode.localScale

        val ratioX = (scale.x - minScale) / getScaleDelta()
        val ratioY = (scale.y - minScale) / getScaleDelta()
        val ratioZ = (scale.z - minScale) / getScaleDelta()

        currentScaleRatio = Vector3(ratioX, ratioY, ratioZ)
    }

    override fun canStartTransformation(gesture: PinchGesture?) = transformableNode.isSelected

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
            x = x * scaleDelta + minScale
            y = y * scaleDelta + minScale
            z = z * scaleDelta + minScale
        }
    }

}