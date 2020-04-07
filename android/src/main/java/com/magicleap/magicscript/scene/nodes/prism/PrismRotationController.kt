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

import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.BaseTransformationController
import com.google.ar.sceneform.ux.TwistGesture
import com.google.ar.sceneform.ux.TwistGestureRecognizer

/**
 * Controller that only notifies the [onRotatedListener] and does not modify node rotation itself
 */
class PrismRotationController(
    transformableNode: PrismContentNode,
    gestureRecognizer: TwistGestureRecognizer
) : BaseTransformationController<TwistGesture>(transformableNode, gestureRecognizer) {

    var onRotatedListener: ((rotationDelta: Quaternion) -> Unit)? = null

    // Rate that the node rotates in degrees per degree of twisting.
    var rotationRateDegrees = 2.5f

    override fun canStartTransformation(gesture: TwistGesture) =
        (transformableNode as PrismContentNode).editModeActive

    override fun onContinueTransformation(gesture: TwistGesture) {
        val rotationAmount = -gesture.deltaRotationDegrees * rotationRateDegrees
        val rotationDelta = Quaternion(Vector3.up(), rotationAmount)
        onRotatedListener?.invoke(rotationDelta)
    }

    override fun onEndTransformation(gesture: TwistGesture) {
        // no-op
    }
}