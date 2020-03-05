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

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.ar.CubeRenderableBuilder
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class PrismTest {

    private val cubeBuilder: CubeRenderableBuilder = mock()

    @Test
    fun `should clip child according to prism size when new child added`() {
        val size = JavaOnlyArray.of(0.4f, 0.6f, 0.2f)
        val expectedClip = AABB(min = Vector3(-0.2f, -0.3f, -0.1f), max = Vector3(0.2f, 0.3f, 0.1f))
        val prism = buildPrism(reactMapOf(Prism.PROP_SIZE, size))
        val child = spy(NodeBuilder().build())

        prism.addContent(child)

        verify(child).clipBounds = expectedClip
    }

    @Test
    fun `should rebuild cube with new size when size changed`() {
        val size = JavaOnlyArray.of(1f, 1f, 1f)
        val prism = buildPrism(reactMapOf(Prism.PROP_SIZE, size))
        val sizeUpdated = JavaOnlyArray.of(2f, 2f, 2f)

        prism.update(reactMapOf(Prism.PROP_SIZE, sizeUpdated))

        verify(cubeBuilder).buildRenderable(eq(Vector3(2f, 2f, 2f)), any(), any(), any())
    }

    private fun buildPrism(props: JavaOnlyMap): Prism {
        return Prism(props, cubeBuilder).apply {
            build()
        }
    }

}