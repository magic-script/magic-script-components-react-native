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

import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.BaseTransformationController
import com.google.ar.sceneform.ux.DragGesture
import com.google.ar.sceneform.ux.DragGestureRecognizer

/**
 * Controller that only notifies the [onDragListener] and does not modify node position itself
 */
class PrismDragController(
    transformableNode: PrismContentNode,
    gestureRecognizer: DragGestureRecognizer
) : BaseTransformationController<DragGesture>(transformableNode, gestureRecognizer) {

    var onDragListener: ((deltaPx: Vector3) -> Unit)? = null

    override fun canStartTransformation(gesture: DragGesture) =
        (transformableNode as PrismContentNode).editModeActive

    override fun onContinueTransformation(gesture: DragGesture) {
        // val diff = gesture.delta
        onDragListener?.invoke(gesture.delta)
    }

    override fun onEndTransformation(gesture: DragGesture) {
        // no-op
    }

}