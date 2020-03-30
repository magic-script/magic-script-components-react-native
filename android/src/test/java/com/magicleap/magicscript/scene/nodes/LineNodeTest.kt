/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
import com.magicleap.magicscript.ar.renderable.CubeRenderableBuilder
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldHaveSize
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
        cubeBuilder = mock()
    }

    @Test
    fun `should not change hardcoded alignment`() {
        val node = createLineNode(JavaOnlyMap())
        node.build()

        node.update(reactMapOf(TransformNode.PROP_ALIGNMENT, "top-left"))

        assertEquals(Alignment.Horizontal.CENTER, node.horizontalAlignment)
        assertEquals(Alignment.Vertical.CENTER, node.verticalAlignment)
    }

    @Test
    fun `should return bounding based on provided points`() {
        val point1 = reactArrayOf(-2.0, -2.0, 0.0)
        val point2 = reactArrayOf(2.0, 2.0, 1.0)
        val points = reactArrayOf(point1, point2)
        val props = reactMapOf(LineNode.PROP_POINTS, points)
        val node = createLineNode(props)
        node.build()

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

        node.contentNode.children shouldHaveSize 2
    }

    @Test
    fun `should use only one cube renderable for all line segments`() {
        val point1 = reactArrayOf(1.0, 2.0, 0.0)
        val point2 = reactArrayOf(3.0, 4.0, 1.0)
        val point3 = reactArrayOf(4.0, 2.0, 1.5)
        val point4 = reactArrayOf(5.0, 1.0, 0.5)
        val points = reactArrayOf(point1, point2, point3, point4)
        val props = reactMapOf(LineNode.PROP_POINTS, points)
        val node = createLineNode(props)

        node.build()

        verify(cubeBuilder, times(1)).buildRenderable(any())
    }

    private fun createLineNode(props: JavaOnlyMap): LineNode {
        return LineNode(props, cubeBuilder)
    }

}