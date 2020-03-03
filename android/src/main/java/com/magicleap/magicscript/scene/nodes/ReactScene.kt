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

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Scene
import com.magicleap.magicscript.ar.CubeRenderableBuilderImpl
import com.magicleap.magicscript.scene.CustomArFragment
import com.magicleap.magicscript.scene.nodes.base.ReactNode

class ReactScene(initialProps: ReadableMap) : ReactNode {

    private var arScene: Scene? = null
    private var arFragment: CustomArFragment? = null
    private val prisms = mutableListOf<Prism>()

    override val parentNode: ReactNode?
        get() = null

    override val childrenNodes: List<ReactNode>
        get() = prisms

    fun onArFragmentReady(fragment: CustomArFragment) {
        this.arFragment = fragment

        for (prism in prisms) {
            if (!prism.built) {
                buildPrism(prism, fragment)
            }
        }
    }

    fun setArScene(scene: Scene) {
        this.arScene = scene
        for (prism in prisms) {
            if (!scene.children.contains(prism)) {
                scene.addChild(prism)
            }
        }
    }

    override fun update(props: ReadableMap) {

    }

    override fun addContent(child: ReactNode) {
        if (child is Prism) {
            prisms.add(child)
            arScene?.addChild(child)

            arFragment?.let { fragment ->
                if (!child.built) {
                    buildPrism(child, fragment)
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

    }

    override fun onResume() {

    }

    override fun onDestroy() {

    }

    private fun buildPrism(prism: Prism, arFragment: CustomArFragment) {
        val transformationSystem = arFragment.transformationSystem ?: return
        val session = arFragment.arSceneView?.session ?: return
        val context = arFragment.context ?: return

        prism.build(transformationSystem, session, CubeRenderableBuilderImpl(context))
        prism.setScene(this)
        arFragment.addCameraObserver(prism)
    }

}