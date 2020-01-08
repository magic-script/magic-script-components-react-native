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
import com.magicleap.magicscript.measureChildren
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.layouts.params.LinearLayoutParams
import com.magicleap.magicscript.scene.nodes.props.*
import com.magicleap.magicscript.utils.Vector2
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LinearLayoutManagerTest {
    private lateinit var linearManager: LinearLayoutManager
    private lateinit var verticalManager: VerticalLinearLayoutManager<LayoutParams>
    private lateinit var horizontalManager: HorizontalLinearLayoutManager<LayoutParams>

    private lateinit var childrenList: List<TransformNode>
    // <child index, bounding>
    private val childrenBounds = mutableMapOf<Int, Bounding>()

    // Layout params
    private var orientation: String = ORIENTATION_VERTICAL
    private var size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
    private var itemPadding = Padding(0f, 0f, 0f, 0f)
    private var itemHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
    private var itemVerticalAlignment = Alignment.VerticalAlignment.TOP

    @Before
    fun setUp() {
        this.verticalManager = mock()
        this.horizontalManager = mock()
        this.linearManager = LinearLayoutManager(verticalManager, horizontalManager)

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

        measureChildren(childrenList, childrenBounds)
    }

    @Test
    fun `should layout using vertical manager when orientation is vertical`() {
        orientation = ORIENTATION_VERTICAL
        val params = getLayoutParams()

        linearManager.layoutChildren(params, childrenList, childrenBounds)

        verify(verticalManager, atLeastOnce()).layoutNode(any(), any<LayoutInfo<LayoutParams>>())
    }

    @Test
    fun `should layout using horizontal manager when orientation is horizontal`() {
        orientation = ORIENTATION_HORIZONTAL
        val params = getLayoutParams()

        linearManager.layoutChildren(params, childrenList, childrenBounds)

        verify(horizontalManager, atLeastOnce()).layoutNode(any(), any<LayoutInfo<LayoutParams>>())
    }

    @Test
    fun `should not interact with horizontal manager when orientation is vertical`() {
        orientation = ORIENTATION_VERTICAL
        val params = getLayoutParams()

        linearManager.layoutChildren(params, childrenList, childrenBounds)
        linearManager.getLayoutBounds(params)

        verify(horizontalManager, never()).layoutNode(any(), any<LayoutInfo<LayoutParams>>())
        verify(horizontalManager, never()).getContentWidth(any(), any())
        verify(horizontalManager, never()).getContentHeight(any(), any())
        verify(horizontalManager, never()).getLayoutBounds(any())
    }

    @Test
    fun `should not interact with vertical manager when orientation is horizontal`() {
        orientation = ORIENTATION_HORIZONTAL
        val params = getLayoutParams()

        linearManager.layoutChildren(params, childrenList, childrenBounds)
        linearManager.getLayoutBounds(params)

        verify(verticalManager, never()).layoutNode(any(), any<LayoutInfo<LayoutParams>>())
        verify(verticalManager, never()).getContentWidth(any(), any())
        verify(verticalManager, never()).getContentHeight(any(), any())
        verify(verticalManager, never()).getLayoutBounds(any())
    }

    private fun getLayoutParams() =
        LinearLayoutParams(
            size = size,
            itemPadding = itemPadding,
            itemHorizontalAlignment = itemHorizontalAlignment,
            itemVerticalAlignment = itemVerticalAlignment,
            orientation = orientation
        )

}
