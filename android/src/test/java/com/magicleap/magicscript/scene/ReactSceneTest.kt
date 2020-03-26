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

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.Prism
import com.nhaarman.mockitokotlin2.*
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class ReactSceneTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val arResourcesProvider: ArResourcesProvider = mock()
    private val arScene = spy<Scene>()

    @Before
    fun setUp() {
        whenever(arResourcesProvider.getArScene()).thenReturn(arScene)
        whenever(arResourcesProvider.getTransformationSystem()).thenReturn(getTransformationSystem())
    }

    @Test
    fun `should attach prism to AR scene when AR scene loaded`() {
        val reactScene = buildScene(reactMapOf())
        val prism = buildPrism(reactMapOf())
        reactScene.addContent(prism)

        arScene.children.size shouldEqual 1
        arScene.children.first() shouldEqual prism
    }

    @Test
    fun `should detach prism from AR scene when removed with removedContent method`() {
        val reactScene = buildScene(reactMapOf())
        val prism = buildPrism(reactMapOf())
        reactScene.addContent(prism)

        reactScene.removeContent(prism)

        arScene.children.size shouldEqual 0
    }

    @Test
    fun `should unregister AR resources listeners on destroy`() {
        val reactScene = buildScene(reactMapOf())

        reactScene.onDestroy()

        verify(arResourcesProvider).removeArSceneChangedListener(eq(reactScene))
        verify(arResourcesProvider).removePlaneTapListener(eq(reactScene))
    }

    private fun buildScene(props: JavaOnlyMap): ReactScene {
        return ReactScene(props, arResourcesProvider).apply {
            build()
        }
    }

    private fun buildPrism(props: JavaOnlyMap): Prism {
        return Prism(props, mock(), mock(), arResourcesProvider)
            .apply {
                build()
            }
    }

    private fun getTransformationSystem(): TransformationSystem {
        val displayMetrics = context.resources.displayMetrics
        return TransformationSystem(displayMetrics, FootprintSelectionVisualizer())
    }

}