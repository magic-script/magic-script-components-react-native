/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes

import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.CubeRenderableBuilder
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class LineNodeTest {

    private lateinit var cubeBuilder: CubeRenderableBuilder

    @Before
    fun setUp() {
        this.cubeBuilder = mock()
    }

    @Test
    fun `should not change hardcoded alignment`() {
        val node = createLineNode(JavaOnlyMap())

        node.update(reactMapOf(TransformNode.PROP_ALIGNMENT, "top-left"))

        assertEquals(Alignment.HorizontalAlignment.CENTER, node.horizontalAlignment)
        assertEquals(Alignment.VerticalAlignment.CENTER, node.verticalAlignment)
    }

    @Test
    fun `should return bounding based on provided points`() {
        val point1 = reactArrayOf(-2.0, -2.0, 0.0)
        val point2 = reactArrayOf(2.0, 2.0, 1.0)
        val points = reactArrayOf(point1, point2)
        val props = reactMapOf(LineNode.PROP_POINTS, points)
        val node = createLineNode(props)
        node.build()
        node.attachRenderable()

        val bounding = node.getBounding()

        bounding.min shouldEqualInexact Vector3(-2f, -2f, 0f)
        bounding.max shouldEqualInexact Vector3(2f, 2f, 1f)
    }

    @Test
    fun `should build two line segments when three points provided`() {
        val point1 = reactArrayOf(1.0, 2.0, 0.0)
        val point2 = reactArrayOf(3.0, 4.0, 1.0)
        val point3 = reactArrayOf(4.0, 2.0, 1.5)
        val points = reactArrayOf(point1, point2, point3)
        val props = reactMapOf(LineNode.PROP_POINTS, points)
        val node = createLineNode(props)
        node.build()

        node.attachRenderable()

        verify(cubeBuilder, times(2)).buildRenderable(any(), any(), any(), any())
    }

    @Test
    fun `should not build cube when only one point provided`() {
        val point = reactArrayOf(1.0, 2.0, 0.0)
        val points = reactArrayOf(point)
        val props = reactMapOf(LineNode.PROP_POINTS, points)
        val node = createLineNode(props)
        node.build()

        node.attachRenderable()

        verify(cubeBuilder, never()).buildRenderable(any(), any(), any(), any())
    }

    private fun createLineNode(props: JavaOnlyMap): LineNode {
        return LineNode(props, cubeBuilder)
    }

}