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

package com.reactlibrary.scene.nodes

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.reactlibrary.ar.CubeRenderableBuilder
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.props.Alignment
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

    private lateinit var context: Context
    private lateinit var cubeRenderableBuilder: CubeRenderableBuilder

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.cubeRenderableBuilder = mock()
    }

    @Test
    fun shouldNotChangeHardcodedAlignment() {
        val node = LineNode(JavaOnlyMap(), context, cubeRenderableBuilder)

        node.update(JavaOnlyMap.of(TransformNode.PROP_ALIGNMENT, "top-left"))

        assertEquals(Alignment.HorizontalAlignment.CENTER, node.horizontalAlignment)
        assertEquals(Alignment.VerticalAlignment.CENTER, node.verticalAlignment)
    }

    @Test
    fun shouldBuildOneLineSegmentWhenTwoPointsProvidedAndRendelableAttached() {
        val point1 = JavaOnlyArray.of(1.0, 2.0, 0.0)
        val point2 = JavaOnlyArray.of(3.0, 4.0, 1.0)
        val points = JavaOnlyArray.of(point1, point2)
        val props = JavaOnlyMap.of(LineNode.PROP_POINTS, points)
        val node = LineNode(props, context, cubeRenderableBuilder)
        node.build()

        node.attachRenderable()

        verify(cubeRenderableBuilder, times(1)).buildRenderable(any(), any(), any(), any())
    }

}