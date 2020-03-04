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

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.magicleap.magicscript.UiNodeBuilder
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.layouts.params.LinearLayoutParams
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.ORIENTATION_HORIZONTAL
import com.magicleap.magicscript.scene.nodes.props.ORIENTATION_VERTICAL
import com.magicleap.magicscript.scene.nodes.props.Padding
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
    private lateinit var itemsPadding: Map<TransformNode, Padding>
    private lateinit var itemsAlignment: Map<TransformNode, Alignment>

    private lateinit var childrenList: List<TransformNode>

    // Layout params
    private var orientation: String = ORIENTATION_VERTICAL
    private var size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)

    @Before
    fun setUp() {
        this.verticalManager = mock()
        this.horizontalManager = mock()
        this.linearManager = LinearLayoutManager(verticalManager, horizontalManager)
        val context: Context = ApplicationProvider.getApplicationContext()

        childrenList = listOf(
            UiNodeBuilder(context)
                .withSize(2f, 1f)
                .withAlignment("center-center")
                .build(),
            UiNodeBuilder(context)
                .withSize(2f, 1f)
                .withAlignment("center-center")
                .build()
        )

        itemsPadding = mapOf(
            childrenList[0] to Padding(),
            childrenList[1] to Padding()
        )

        itemsAlignment = mapOf(
            childrenList[0] to Alignment(Alignment.Vertical.TOP, Alignment.Horizontal.LEFT),
            childrenList[1] to Alignment(Alignment.Vertical.TOP, Alignment.Horizontal.LEFT)
        )

    }

    @Test
    fun `should layout using vertical manager when orientation is vertical`() {
        orientation = ORIENTATION_VERTICAL
        val params = getLayoutParams()

        linearManager.layoutChildren(params, childrenList, mock())

        verify(verticalManager, atLeastOnce()).layoutChildren(any(), any(), any())
    }

    @Test
    fun `should layout using horizontal manager when orientation is horizontal`() {
        orientation = ORIENTATION_HORIZONTAL
        val params = getLayoutParams()

        linearManager.layoutChildren(params, childrenList, mock())

        verify(horizontalManager, atLeastOnce()).layoutChildren(any(), any(), any())
    }

    @Test
    fun `should not interact with horizontal manager when orientation is vertical`() {
        orientation = ORIENTATION_VERTICAL
        val params = getLayoutParams()

        linearManager.layoutChildren(params, childrenList, mock())
        linearManager.getLayoutBounds(params)

        verify(horizontalManager, never()).layoutChildren(any(), any(), any())
        verify(horizontalManager, never()).getLayoutBounds(any())
    }

    @Test
    fun `should not interact with vertical manager when orientation is horizontal`() {
        orientation = ORIENTATION_HORIZONTAL
        val params = getLayoutParams()

        linearManager.layoutChildren(params, childrenList, mock())
        linearManager.getLayoutBounds(params)

        verify(verticalManager, never()).layoutChildren(any(), any(), any())
        verify(verticalManager, never()).getLayoutBounds(any())
    }

    private fun getLayoutParams() =
        LinearLayoutParams(
            orientation = orientation,
            size = size,
            itemsAlignment = itemsAlignment,
            itemsPadding = itemsPadding
        )

}
