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

package com.magicleap.magicscript.scene

import android.os.Bundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Scene
import com.magicleap.magicscript.scene.nodes.Prism
import com.magicleap.magicscript.scene.nodes.base.ReactNode

class ReactScene(initProps: ReadableMap) : ReactNode {
    private var properties = Arguments.toBundle(initProps) ?: Bundle()
    private var arScene: Scene? = null
    private var arFragment: CustomArFragment? = null
    private val prisms = mutableListOf<Prism>()

    override val reactParent: ReactNode?
        get() = null

    override val reactChildren: List<ReactNode>
        get() = prisms

    override fun build() {
        applyProperties(properties)
    }

    fun setArDependencies(fragment: CustomArFragment, scene: Scene) {
        this.arFragment = fragment
        this.arScene = scene

        applyProperties(properties)

        for (prism in prisms) {
            if (!scene.children.contains(prism)) {
                scene.addChild(prism)
            }
            if (!prism.initialized) {
                setupPrism(prism, fragment)
            }
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

            arFragment?.let { fragment ->
                if (!child.initialized) {
                    setupPrism(child, fragment)
                }
            }
        }
    }

    override fun removeContent(child: ReactNode) {
        if (child is Prism) {
            prisms.remove(child)
            arScene?.removeChild(child)
        }
    }

    override fun onPause() {
        // no-op
    }

    override fun onResume() {
        // no-op
    }

    override fun onDestroy() {
        arFragment = null
        arScene = null
    }

    private fun applyProperties(props: Bundle) {
        // TODO
    }

    private fun setupPrism(prism: Prism, arFragment: CustomArFragment) {
        val transformationSystem = arFragment.transformationSystem ?: return
        val session = arFragment.arSceneView?.session ?: return

        prism.setArDependencies(transformationSystem, session)
        prism.setScene(this)
        arFragment.addCameraObserver(prism)
    }

}