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

package com.magicleap.magicscript.scene

import android.os.Bundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.scene.nodes.base.ReactNode
import com.magicleap.magicscript.scene.nodes.prism.Prism
import com.magicleap.magicscript.utils.Utils

class ReactScene(
    initProps: ReadableMap,
    private val arResourcesProvider: ArResourcesProvider
) : ReactNode, ArResourcesProvider.ArSceneChangedListener, ArResourcesProvider.PlaneTapListener {

    var deepLink: String? = null

    var onCreatedListener: ((deepLink: String?) -> Unit)? = null
        set(value) {
            field = value
            if (value != null && built) {
                value.invoke(deepLink)
            }
        }

    private var properties = Arguments.toBundle(initProps) ?: Bundle()
    private val prisms = mutableListOf<Prism>()
    private var built = false

    private val arScene get() = arResourcesProvider.getArScene() // ar scene may change at runtime

    init {
        arResourcesProvider.addArSceneChangedListener(this)
        arResourcesProvider.addPlaneTapListener(this)
    }

    override val reactParent: ReactNode?
        get() = null

    override val reactChildren: List<ReactNode>
        get() = prisms

    override fun build() {
        if (built) {
            return
        }
        applyProperties(properties)
        onCreatedListener?.invoke(deepLink)
        built = true
    }

    override fun onSceneChanged(arScene: Scene) {
        attachPrismsToArScene(arScene)
    }

    private fun attachPrismsToArScene(arScene: Scene) {
        prisms.forEach {
            // detach from old scene
            it.setParent(null)
        }

        for (prism in prisms) {
            if (!arScene.children.contains(prism)) {
                arScene.addChild(prism)
            }
            setupPrism(prism)
        }
    }

    override fun update(props: ReadableMap) {
        val propsToUpdate = Arguments.toBundle(props) ?: Bundle()
        this.properties.putAll(propsToUpdate) // save new props
        applyProperties(propsToUpdate)
    }

    override fun addContent(child: ReactNode) {
        if (child is Prism) {
            prisms.add(child)
            arScene?.addChild(child)

            setupPrism(child)
        }
    }

    override fun removeContent(child: ReactNode) {
        if (child is Prism) {
            prisms.remove(child)
            arScene?.removeChild(child)
        }
    }

    override fun onPlaneTap(hitResult: HitResult) {
        if (arResourcesProvider.isPlaneDetectionEnabled() && prisms.isNotEmpty()) {
            // it's important to release unused anchors
            val prism = prisms.firstOrNull()
            if (prism != null) {
                val x = hitResult.hitPose.tx()
                var y = hitResult.hitPose.ty()
                val z = hitResult.hitPose.tz()
                val trackable = hitResult.trackable
                if (trackable is Plane && trackable.type == Plane.Type.HORIZONTAL_UPWARD_FACING) {
                    // move anchor up so the bottom of Prism sticks to floor
                    y += +prism.size.y / 2 * prism.scale.y
                }
                val position = Vector3(x, y, z)
                val rotation = Quaternion.identity() // no rotation
                val pose = Utils.createPose(position, rotation)
                prism.anchorAtPlane(pose)
            }
        }
    }

    override fun onHostPause() {
        // no-op
    }

    override fun onHostResume() {
        // no-op
    }

    override fun onHostDestroy() {
        // no-op
    }

    override fun onDestroy() {
        arResourcesProvider.removeArSceneChangedListener(this)
        arResourcesProvider.removePlaneTapListener(this)
    }

    private fun applyProperties(props: Bundle) {
        // TODO
    }

    private fun setupPrism(prism: Prism) {
        prism.setScene(this)
    }

}