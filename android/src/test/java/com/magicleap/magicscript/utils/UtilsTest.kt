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

package com.magicleap.magicscript.utils

import android.content.Context
import android.net.Uri
import android.util.DisplayMetrics
import androidx.test.core.app.ApplicationProvider
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.UiNodeBuilder
import com.magicleap.magicscript.ar.ModelType
import com.magicleap.magicscript.scene.nodes.props.AABB
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

        val bounding = Utils.calculateBoundsOfNode(node, collisionShape)

        bounding.min shouldEqualInexact Vector3(-2f, 0f, 0.9f)
        bounding.max shouldEqualInexact Vector3(0f, 4f, 1.1f)
    }

    @Test
    fun `should return bounding based on position only when there is no collision shape`() {
        val node = Node()
        node.localPosition = Vector3(1f, 1f, 1f)

        val bounding = Utils.calculateBoundsOfNode(node, null)

        bounding.min shouldEqualInexact Vector3(1f, 1f, 1f)
        bounding.max shouldEqualInexact Vector3(1f, 1f, 1f)
    }

    @Test
    fun `should return bounding with widest and highest area of all nodes`() {
        val testNode1 = Node()
        val testNode2 = Node()
        val testNode3 = Node()
        testNode1.localPosition = Vector3(1f, 2f, 3f)
        testNode2.localPosition = Vector3(10f, 20f, 30f)
        testNode3.localPosition = Vector3(100f, 200f, 300f)

        val bounding = Utils.calculateSumBounds(listOf(testNode1, testNode2, testNode3))

        bounding.min shouldEqualInexact Vector3(1f, 2f, 3f)
        bounding.max shouldEqualInexact Vector3(100f, 200f, 300f)
    }

    @Test
    fun `should return empty bounding when list of nodes is empty`() {
        val bounding = Utils.calculateSumBounds(emptyList())

        bounding shouldEqualInexact AABB(Vector3.zero(), Vector3.zero())
    }

    @Test
    fun `should return first node bounding if list contains only one node`() {
        val testNode = Node()
        testNode.localPosition = Vector3(1f, 1f, 1f)

        val bounding = Utils.calculateSumBounds(listOf(testNode))

        bounding.min shouldEqualInexact Vector3(1f, 1f, 1f)
        bounding.max shouldEqualInexact Vector3(1f, 1f, 1f)
    }

    @Test
    fun `should return minimum bounding for list of points`() {
        val points = listOf(
            Vector3(-1f, 2f, -4f),
            Vector3(-1f, -1f, 0f),
            Vector3(4f, -1f, 7f),
            Vector3(5f, 4f, 0f)
        )

        val bounding = Utils.findMinimumBounding(points)

        bounding.min shouldEqualInexact Vector3(-1f, -1f, -4f)
        bounding.max shouldEqualInexact Vector3(5f, 4f, 7f)
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


    /*
             ^
             |
             |
             |
        +---------+
        |    |    | Node bounds
        |    |    |
        |    |    |
        |  +---+  |
        |  | | |  |
        |  | C |  |
        |  | | |  |
    ----+--+---+--+--------------->
             |
             |

     */
    @Test
    fun `should return correct material clipping (case 1)`() {
        val clipBounds = AABB(min = Vector3(-1f, 0f, -1f), max = Vector3(1f, 3f, 1f))
        val nodeBounds = AABB(min = Vector3(-2f, 0f, 0f), max = Vector3(2f, 6f, 0f))
        // Fully visible node has material clipping equal to Bounding(-0.5f, 0.0f, 0.5f, 1.0f)
        // with origin at bottom-center, so clipping must be proportional to it.
        val expectedClipping = Bounding(left = -0.25f, bottom = 0f, right = 0.25f, top = 0.5f)

        val clipping = Utils.calculateMaterialClipping(nodeBounds, clipBounds)

        clipping shouldEqualInexact expectedClipping
    }

    /*
             ^
             |
             |
             |
        +---------+
        |    |    | Clip bounds
        |    |    |
        |    |    |
        |  +---+  |
        |  | | |  |
        |  | N |  |
        |  | | |  |
    ----+--+---+--+--------------->
             |
             |

     */
    @Test
    fun `should not clip material when node bounds inside clip bounds`() {
        val clipBounds = AABB(min = Vector3(-2f, 0f, 0f), max = Vector3(2f, 6f, 0f))
        val nodeBounds = AABB(min = Vector3(-1f, 0f, -1f), max = Vector3(1f, 3f, 1f))
        val expectedClipping = Bounding(left = -0.5f, bottom = 0f, right = 0.5f, top = 1f)

        val clipping = Utils.calculateMaterialClipping(nodeBounds, clipBounds)

        clipping shouldEqualInexact expectedClipping
    }

    /*
         ^
         |  Clip bounds
         +-----------------+
         |                 |
         |                 |
         |            +------+
         |            |    | | Node
     ------------------------------->
         |            |      |
         |            +------+
         |
     */
    @Test
    fun `should return correct material clipping (case 2)`() {
        val clipBounds = AABB(min = Vector3(0f, 0f, -1f), max = Vector3(5f, 3f, 1f))
        val nodeBounds = AABB(min = Vector3(3f, -1f, 0f), max = Vector3(6f, 1f, 0f))
        val expectedClipping = Bounding(left = -0.5f, bottom = 0.5f, right = 1 / 6f, top = 1f)

        val clipping = Utils.calculateMaterialClipping(nodeBounds, clipBounds)

        clipping shouldEqualInexact expectedClipping
    }


    /*
                                                  ^
                                                  |
                                              +-------+
                                              |   |   | Clip bounds
             Node                             |       |
         +-----------+                        |   |   |
         |           |                        |   |   |
       --+-----------+----------------------------------------------->
                                              |   |   |
                                              |   |   |
                                              |   |   |
                                              +-------+
                                                  |
     */
    @Test
    fun `should return empty material clipping horizontally outside clip bounds`() {
        val clipBounds = AABB(min = Vector3(-1f, -2f, -1f), max = Vector3(1f, 2f, 1f))
        val nodeBounds = AABB(min = Vector3(-10f, 0f, 0f), max = Vector3(-8f, 1f, 0f))
        // node should be completely invisible
        val expectedClipping = Bounding(left = 0f, bottom = 0f, right = 0f, top = 0f)

        val clipping = Utils.calculateMaterialClipping(nodeBounds, clipBounds)

        clipping shouldEqualInexact expectedClipping
    }


    @Test
    fun `should return empty material clipping when z position outside clip bounds`() {
        val clipBounds = AABB(min = Vector3(-4f, -4f, -1f), max = Vector3(4f, 4f, 1f))
        val nodeBounds = AABB(min = Vector3(-1f, -1f, -5f), max = Vector3(1f, 1f, -4f))
        val expectedClipping = Bounding(left = 0f, bottom = 0f, right = 0f, top = 0f)

        val clipping = Utils.calculateMaterialClipping(nodeBounds, clipBounds)

        clipping shouldEqualInexact expectedClipping
    }

    @Test
    fun `multiple calls of applyContentNodeAlignment should not break the alignment`() {
        val node = UiNodeBuilder(appContext, useContentNodeAlignment = true)
            .withSize(5f, 5f)
            .withAlignment("top-center")
            .build()

        Utils.applyContentNodeAlignment(node)
        Utils.applyContentNodeAlignment(node)
        Utils.applyContentNodeAlignment(node)

        node.contentNode.localPosition shouldEqualInexact Vector3(0f, -2.5f, 0f)
    }

    @Test
    fun `should create correct Pose of provided position and rotation`() {
        val position = Vector3(-2f, 1.5f, 0.8f)
        val rotation = Quaternion(0f, 0.3826834f, 0f, 0.9238795f).normalized()

        val pose = Utils.createPose(position, rotation)

        pose.tx() shouldEqualInexact position.x
        pose.ty() shouldEqualInexact position.y
        pose.tz() shouldEqualInexact position.z
        pose.qx() shouldEqualInexact rotation.x
        pose.qy() shouldEqualInexact rotation.y
        pose.qz() shouldEqualInexact rotation.z
        pose.qw() shouldEqualInexact rotation.w
    }

}