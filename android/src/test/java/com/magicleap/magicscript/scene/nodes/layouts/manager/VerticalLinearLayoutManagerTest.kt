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

package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.layoutUntilStableBounds
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.shouldEqualInexact
import com.magicleap.magicscript.utils.Vector2
import org.amshove.kluent.shouldNotEqual
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VerticalLinearLayoutManagerTest {
    private val EPSILON = 1e-5f
    private lateinit var linearManager: VerticalLinearLayoutManager<LayoutParams>
    private lateinit var childrenList: List<TransformNode>
    // <child index, bounding>
    private val childrenBounds = mutableMapOf<Int, Bounding>()

    // Layout params
    private var size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
    private var itemPadding = Padding(0f, 0f, 0f, 0f)
    private var itemHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
    private var itemVerticalAlignment = Alignment.VerticalAlignment.TOP

    @Before
    fun setUp() {
        this.linearManager = VerticalLinearLayoutManager()

        childrenList = listOf(
            NodeBuilder()
                .withContentBounds(Bounding(-1f, -0.5F, 1F, 0.5F))
                .withAlignment("center-center")
                .build(),
            NodeBuilder()
                .withContentBounds(Bounding(-1f, -0.5F, 1F, 0.5F))
                .withAlignment("center-center")
                .build()
        )
    }

    @Test
    fun `should change children position when top padding set`() {
        size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        itemPadding = Padding(0.5f, 0f, 0f, 0f)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        childrenList[0].localPosition shouldNotEqual Vector3(0F, 0F, 0F)
        childrenList[1].localPosition shouldNotEqual Vector3(0F, 0F, 0F)
    }

    @Test
    fun `should return correct layout bounds`() {
        size = Vector2(WRAP_CONTENT_DIMENSION, 5f)
        itemPadding = Padding(0.2f, 0.2f, 0.1f, 0.1f)
        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        val bounds = linearManager.getLayoutBounds(getLayoutParams())

        bounds shouldEqualInexact Bounding(left = 0f, bottom = -5f, right = 2.3f, top = 0f)
    }

    @Test
    fun `should scale child nodes if layout size limited`() {
        size = Vector2(1f, 2f)
        itemPadding = Padding(0.05F, 0.05F, 0.05F, 0.05F)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        // 0.45 = (layout width - horizontal padding) / children sum width
        assertEquals(0.45f, childrenList[0].localScale.x, EPSILON)
        assertEquals(0.45f, childrenList[0].localScale.y, EPSILON)
        assertEquals(0.45f, childrenList[1].localScale.x, EPSILON)
        assertEquals(0.45f, childrenList[1].localScale.y, EPSILON)
    }

    private fun getLayoutParams() =
        LayoutParams(
            size = size,
            itemsAlignment = mapOf(
                Pair(0, Alignment(itemVerticalAlignment, itemHorizontalAlignment)),
                Pair(1, Alignment(itemVerticalAlignment, itemHorizontalAlignment))
            ),
            itemsPadding = mapOf(
                Pair(0, itemPadding),
                Pair(1, itemPadding)
            )
        )

}
