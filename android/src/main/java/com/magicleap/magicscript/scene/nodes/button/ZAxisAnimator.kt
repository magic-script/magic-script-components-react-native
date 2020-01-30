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

package com.magicleap.magicscript.scene.nodes.button

import android.os.Handler
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.scene.nodes.base.NodeAnimator

class ZAxisAnimator : NodeAnimator {

    companion object {
        private const val INITIAL_Z = 0f
        private const val ANIMATED_Z = -0.05f
        private const val ANIMATION_DURATION = 150L
    }

    override fun play(node: Node, onCompletedListener: () -> Unit) {
        setZPosition(node, ANIMATED_Z)
        Handler().postDelayed({
            setZPosition(node, INITIAL_Z)
            onCompletedListener()
        }, ANIMATION_DURATION)
    }

    private fun setZPosition(node: Node, z: Float) {
        node.localPosition = Vector3(node.localPosition.x, node.localPosition.y, z)
    }

}