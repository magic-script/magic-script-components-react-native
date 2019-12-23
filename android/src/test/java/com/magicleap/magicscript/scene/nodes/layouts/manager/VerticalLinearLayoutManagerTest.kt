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
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import org.amshove.kluent.shouldNotEqual
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VerticalLinearLayoutManagerTest {
    private val EPSILON = 1e-5f
    private lateinit var linearManager: VerticalLinearLayoutManager
    private lateinit var childrenList: List<TransformNode>
    // <child index, bounding>
    private val childrenBounds = mutableMapOf<Int, Bounding>()

    @Before
    fun setUp() {
        this.linearManager = VerticalLinearLayoutManager()
        linearManager.itemVerticalAlignment = Alignment.VerticalAlignment.TOP
        linearManager.itemHorizontalAlignment = Alignment.HorizontalAlignment.LEFT

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
        linearManager.itemPadding = Padding(0.5f, 0f, 0f, 0f)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, 10)

        childrenList[0].localPosition shouldNotEqual Vector3(0F, 0F, 0F)
        childrenList[1].localPosition shouldNotEqual Vector3(0F, 0F, 0F)
    }

    @Test
    fun `should return correct layout bounds when vertical`() {
        linearManager.itemPadding = Padding(0.2f, 0.2f, 0.1f, 0.1f)
        linearManager.parentWidth = 0f // dynamic
        linearManager.parentHeight = 5f
        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, 10)

        val boundsSize = linearManager.getLayoutBounds().size()

        assertEquals(2.3f, boundsSize.x, EPSILON)
        assertEquals(5f, boundsSize.y, EPSILON)
    }


    @Test
    fun `should scale child node if parent size limited`() {
        linearManager.itemPadding = Padding(0.05F, 0.05F, 0.05F, 0.05F)
        linearManager.parentWidth = 1f
        linearManager.parentHeight = 2f

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, 10)

        // 0.45 = (parent width - horizontal padding) / child width
        assertEquals(0.45f, childrenList[0].localScale.x, EPSILON)
        assertEquals(0.45f, childrenList[0].localScale.y, EPSILON)
    }

}
