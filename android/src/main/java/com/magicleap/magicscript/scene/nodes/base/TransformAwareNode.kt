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

package com.magicleap.magicscript.scene.nodes.base

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3

open class TransformAwareNode : Node() {

    private val onTransformListeners = mutableListOf<LocalTransformListener>()

    fun addOnLocalTransformChangedListener(listener: LocalTransformListener) {
        onTransformListeners.add(listener)
    }

    override fun setLocalPosition(position: Vector3) {
        super.setLocalPosition(position)
        onLocalTransformChanged()
    }

    override fun setLocalScale(scale: Vector3) {
        super.setLocalScale(scale)
        onLocalTransformChanged()
    }

    override fun setLocalRotation(rotation: Quaternion) {
        super.setLocalRotation(rotation)
        onLocalTransformChanged()
    }

    open fun onLocalTransformChanged() {
        onTransformListeners.forEach {
            it.onTransformed()
        }
    }

    interface LocalTransformListener {
        fun onTransformed()
    }

}