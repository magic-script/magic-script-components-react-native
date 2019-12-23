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

import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.layoutUntilStableBounds
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HorizontalLinearLayoutManagerTest {
    private val EPSILON = 1e-5f
    private lateinit var linearManager: HorizontalLinearLayoutManager
    private lateinit var childrenList: List<TransformNode>
    // <child index, bounding>
    private val childrenBounds = mutableMapOf<Int, Bounding>()

    @Before
    fun setUp() {
        this.linearManager = HorizontalLinearLayoutManager()

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
    fun `should return correct layout bounds when horizontal`() {
        linearManager.itemPadding = Padding(0.2f, 0.2f, 0.1f, 0.1f)
        linearManager.parentWidth = 0f // dynamic
        linearManager.parentHeight = 5f
        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, 10)

        val boundsSize = linearManager.getLayoutBounds().size()

        assertEquals(4.6f, boundsSize.x, EPSILON)
        assertEquals(5f, boundsSize.y, EPSILON)
    }

    @Test
    fun `should position children correctly when parent size is dynamic`() {
        linearManager.parentWidth = 0f
        linearManager.parentHeight = 0f
        linearManager.itemPadding = Padding(0.5F, 0.5F, 0.5F, 0.5F)
        linearManager.itemVerticalAlignment = Alignment.VerticalAlignment.TOP
        linearManager.itemHorizontalAlignment = Alignment.HorizontalAlignment.LEFT

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, 10)

        assertEquals(1.5f, childrenList[0].localPosition.x, EPSILON)
        assertEquals(4.5f, childrenList[1].localPosition.x, EPSILON)
        assertEquals(-1f, childrenList[0].localPosition.y, EPSILON)
        assertEquals(-1f, childrenList[1].localPosition.y, EPSILON)
    }

    @Test
    fun `should center children vertically and horizontally`() {
        linearManager.parentWidth = 7f
        linearManager.parentHeight = 6f
        linearManager.itemHorizontalAlignment = Alignment.HorizontalAlignment.CENTER
        linearManager.itemVerticalAlignment = Alignment.VerticalAlignment.CENTER
        linearManager.itemPadding = Padding(0.5F, 0.5F, 0.5F, 0.5F)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, 10)

        assertEquals(2f, childrenList[0].localPosition.x, EPSILON)
        assertEquals(5f, childrenList[1].localPosition.x, EPSILON)
        assertEquals(-3f, childrenList[0].localPosition.y, EPSILON)
        assertEquals(-3f, childrenList[1].localPosition.y, EPSILON)
    }

    /*
    @Test
    fun `should layout correctly when children scaled down`() {
        linearManager.parentWidth = 3.5f
        linearManager.parentHeight = 6f
        linearManager.itemHorizontalAlignment = Alignment.HorizontalAlignment.CENTER
        linearManager.itemVerticalAlignment = Alignment.VerticalAlignment.CENTER
        linearManager.itemPadding = Padding(0.5F, 0.5F, 0.5F, 0.5F)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, 50)


        val child1Bounds = childrenBounds[0]
        val child2Bounds = childrenBounds[1]

        assertEquals(0.5f, childrenList[0].localScale.x, EPSILON)
    }
    */

    @Test
    fun `should align children bottom-right`() {
        linearManager.parentWidth = 7f
        linearManager.parentHeight = 6f
        linearManager.itemVerticalAlignment = Alignment.VerticalAlignment.BOTTOM
        linearManager.itemHorizontalAlignment = Alignment.HorizontalAlignment.RIGHT
        linearManager.itemPadding = Padding(0.5F, 0.5F, 0.5F, 0.5F)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, 10)

        assertEquals(2.5f, childrenList[0].localPosition.x, EPSILON)
        assertEquals(5.5f, childrenList[1].localPosition.x, EPSILON)
        assertEquals(-5f, childrenList[0].localPosition.y, EPSILON)
        assertEquals(-5f, childrenList[1].localPosition.y, EPSILON)
    }

}
