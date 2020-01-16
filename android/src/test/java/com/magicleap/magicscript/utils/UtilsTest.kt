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

package com.magicleap.magicscript.utils

import android.content.Context
import android.net.Uri
import android.util.DisplayMetrics
import androidx.test.core.app.ApplicationProvider
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ModelType
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.shouldEqual
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UtilsTest {

    private lateinit var appContext: Context

    @Before
    fun setUp() {
        this.appContext = ApplicationProvider.getApplicationContext<Context>()
    }

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
    fun `should return bounding based on node position and collision shape`() {
        val node = Node()
        node.localPosition = Vector3(-1f, 2f, 1f)
        val center = Vector3.zero()
        val size = Vector3(2f, 4f, 0.2f)
        val collisionShape = Box(size, center)
        val expectedBounding = Bounding(left = -2f, bottom = 0f, right = 0f, top = 4f)

        val bounding = Utils.calculateBoundsOfNode(node, collisionShape)

        bounding shouldEqualInexact expectedBounding
    }

    @Test
    fun `should return bounding based on position only when there is no collision shape`() {
        val node = Node()
        node.localPosition = Vector3(1f, 1f, 1f)
        val expectedBounding = Bounding(left = 1f, bottom = 1f, right = 1f, top = 1f)

        val bounding = Utils.calculateBoundsOfNode(node, null)

        bounding shouldEqualInexact expectedBounding
    }

    @Test
    fun `should return bounding with widest and highest area of all nodes`() {
        val testNode1 = Node()
        val testNode2 = Node()
        val testNode3 = Node()
        testNode1.localPosition = Vector3(1f, 2f, 3f)
        testNode2.localPosition = Vector3(10f, 20f, 30f)
        testNode3.localPosition = Vector3(100f, 200f, 300f)
        val expectedBounding = Bounding(left = 1f, bottom = 2f, right = 100f, top = 200f)

        val bounding = Utils.calculateSumBounds(listOf(testNode1, testNode2, testNode3))

        bounding shouldEqualInexact expectedBounding
    }

    @Test
    fun `should return empty bounding when list of nodes is empty`() {
        val bounding = Utils.calculateSumBounds(emptyList())
        val expectedBounding = Bounding(0f, 0f, 0f, 0f)

        bounding shouldEqualInexact expectedBounding
    }

    @Test
    fun `should return first node bounding if list contains only one node`() {
        val testNode = Node()
        testNode.localPosition = Vector3(1f, 1f, 1f)
        val expectedBounding = Bounding(1f, 1f, 1f, 1f)

        val bounding = Utils.calculateSumBounds(listOf(testNode))

        bounding shouldEqualInexact expectedBounding
    }

    @Test
    fun `should return minimum bounding for list of points`() {
        val points = listOf(
            Vector3(-1f, 2f, 0f),
            Vector3(-1f, -1f, 0f),
            Vector3(4f, -1f, 0f),
            Vector3(5f, 4f, 0f)
        )
        val expectedBounding = Bounding(-1f, -1f, 5f, 4f)

        val bounding = Utils.findMinimumBounding(points)

        bounding shouldEqualInexact expectedBounding
    }

    @Test
    fun `should detect sfb model type when URL ends with sfb`() {
        val modelPath = Uri.parse("http://sample-models/model.sfb")

        val modelType = Utils.detectModelType(modelPath, appContext)

        modelType shouldEqual ModelType.SFB
    }

    @Test
    fun `should detect glb model type when URL contains glb extension`() {
        val modelPath = Uri.parse("https://sample-models/model.glb?param=123")

        val modelType = Utils.detectModelType(modelPath, appContext)

        modelType shouldEqual ModelType.GLB
    }

}