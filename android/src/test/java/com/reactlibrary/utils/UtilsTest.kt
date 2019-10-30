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

package com.reactlibrary.utils

import android.content.Context
import android.util.DisplayMetrics
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.nhaarman.mockitokotlin2.whenever
import com.reactlibrary.scene.nodes.props.Bounding
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UtilsTest {

    // epsilon
    private val eps = 1e-5f

    @Test
    fun shouldReturnCorrectNumberOfPixels() {
        val meters = 0.5F
        val screenDensity = 2 * Utils.BASELINE_DENSITY
        val expectedPixels = Utils.DP_TO_METER_RATIO
        val context = mock(Context::class.java, RETURNS_DEEP_STUBS)
        val displayMetrics = DisplayMetrics().apply {
            xdpi = screenDensity
            ydpi = screenDensity
        }
        whenever(context.resources.displayMetrics).thenReturn(displayMetrics)

        val pixels = Utils.metersToPx(meters, context)

        assertEquals(expectedPixels, pixels)
    }

    @Test
    fun shouldReturnCorrectNumberOfPixelsForFont() {
        val meters = 0.5F
        val screenDensity = 2 * Utils.BASELINE_DENSITY
        val expectedPixels = (Utils.DP_TO_METER_RATIO * Utils.FONT_SCALE_FACTOR).toInt()
        val context = mock(Context::class.java, RETURNS_DEEP_STUBS)
        val displayMetrics = DisplayMetrics().apply {
            xdpi = screenDensity
            ydpi = screenDensity
        }
        whenever(context.resources.displayMetrics).thenReturn(displayMetrics)

        val pixels = Utils.metersToFontPx(meters, context)

        assertEquals(expectedPixels, pixels)
    }

    @Test
    fun shouldReturnBasicBoundingWhenCollisionShapeIsNotBox() {
        val node = Node()
        node.localPosition = Vector3(1f, 1f, 1f)

        val bounding = Utils.calculateBoundsOfNode(node)

        assertNotNull(bounding)
        assertEquals(node.localPosition.x, bounding.left, eps)
        assertEquals(node.localPosition.x, bounding.right, eps)
        assertEquals(node.localPosition.y, bounding.top, eps)
        assertEquals(node.localPosition.y, bounding.bottom, eps)
    }

    @Test
    fun shouldReturnBoundingWithWidestAndHighestAreaOfAllNodes() {
        val testNode1 = Node()
        val testNode2 = Node()
        val testNode3 = Node()
        testNode1.localPosition = Vector3(1f, 2f, 3f)
        testNode2.localPosition = Vector3(10f, 20f, 30f)
        testNode3.localPosition = Vector3(100f, 200f, 300f)

        val bounding = Utils.calculateSumBounds(listOf(testNode1, testNode2, testNode3))

        assertNotNull(bounding)
        assertEquals(1f, bounding.left, eps)
        assertEquals(100f, bounding.right, eps)
        assertEquals(2f, bounding.bottom, eps)
        assertEquals(200f, bounding.top, eps)
    }

    @Test
    fun shouldReturnEmptyBoundingWhenListOfNodesIsEmpty() {
        val bounding = Utils.calculateSumBounds(emptyList())

        assertEquals(Bounding(0f, 0f, 0f, 0f), bounding)
    }

    @Test
    fun shouldReturnFirstNodeBoundingIfListContainsOnlyOneNode() {
        val testNode = Node()
        testNode.localPosition = Vector3(1f, 1f, 1f)
        val expectedBounding = Bounding(1f, 1f, 1f, 1f)

        val bounding = Utils.calculateSumBounds(listOf(testNode))

        assertTrue(Bounding.equalInexact(expectedBounding, bounding))
    }

    @Test
    fun shouldReturnMinimumBoundingForListOfPoints() {
        val points = listOf(
                Vector2(-1f, 2f),
                Vector2(-1f, -1f),
                Vector2(4f, -1f),
                Vector2(5f, 4f)
        )
        val expectedBounding = Bounding(-1f, -1f, 5f, 4f)

        val bounding = Utils.findMinimumBounding(points)

        assertTrue(Bounding.equalInexact(expectedBounding, bounding))
    }

}