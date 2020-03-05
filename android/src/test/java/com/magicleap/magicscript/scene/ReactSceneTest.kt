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

import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.Scene
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.Prism
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class ReactSceneTest {

    @Test
    fun `should attach prism to AR scene when notified that AR is ready`() {
        val reactScene = buildScene(reactMapOf())
        val arScene = spy<Scene>()
        val prism = buildPrism(reactMapOf())
        reactScene.addContent(prism)

        reactScene.setArDependencies(mock(), arScene)

        arScene.children.size shouldEqual 1
        arScene.children.first() shouldEqual prism
    }

    @Test
    fun `should detach prism from AR scene when removed with removedContent method`() {
        val reactScene = buildScene(reactMapOf())
        val arScene = spy<Scene>()
        val prism = buildPrism(reactMapOf())
        reactScene.addContent(prism)
        reactScene.setArDependencies(mock(), arScene)

        reactScene.removeContent(prism)

        arScene.children.size shouldEqual 0
    }

    private fun buildScene(props: JavaOnlyMap): ReactScene {
        return ReactScene(props).apply {
            build()
        }
    }

    private fun buildPrism(props: JavaOnlyMap): Prism {
        return Prism(props, mock()).apply {
            build()
        }
    }

}