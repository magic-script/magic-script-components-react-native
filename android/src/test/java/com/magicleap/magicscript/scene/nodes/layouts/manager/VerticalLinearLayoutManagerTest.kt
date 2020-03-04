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
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.UiNodeBuilder
import com.magicleap.magicscript.layoutUntilStableBounds
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.shouldEqualInexact
import com.magicleap.magicscript.utils.Vector2
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VerticalLinearLayoutManagerTest {
    private lateinit var linearManager: VerticalLinearLayoutManager<LayoutParams>
    private lateinit var childrenList: List<TransformNode>
    private lateinit var itemsPadding: Map<TransformNode, Padding>
    private lateinit var itemsAlignment: Map<TransformNode, Alignment>

    private val childrenBounds = mutableMapOf<TransformNode, AABB>()

    // Layout params
    private var size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)

    @Before
    fun setUp() {
        this.linearManager = VerticalLinearLayoutManager()
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
    fun `should correctly layout children when padding is the same`() {
        size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.5f, 0.5f, 0f, 0.2f),
            childrenList[1] to Padding(0.5f, 0.5f, 0f, 0.2f)
        )

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        // items are positioned starting at top left
        childrenList[0].localPosition shouldEqualInexact Vector3(1.2f, -1f, 0f)
        childrenList[1].localPosition shouldEqualInexact Vector3(1.2f, -2.5f, 0f)
    }

    @Test
    fun `should correctly layout children when padding is different`() {
        size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        itemsPadding = mapOf(
            childrenList[0] to Padding(0f, 0f, 1f, 0f),
            childrenList[1] to Padding(0.5f, 0f, 0f, 0f)
        )

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        childrenList[0].localPosition shouldEqualInexact Vector3(1f, -0.5f, 0f)
        childrenList[1].localPosition shouldEqualInexact Vector3(1f, -3f, 0f)
    }

    @Test
    fun `should return correct layout bounds when width is dynamic`() {
        size = Vector2(WRAP_CONTENT_DIMENSION, 5f)
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.2f, 0.1f, 0.1f, 0.1f),
            childrenList[1] to Padding(0.2f, 0.2f, 0.1f, 0.7f)
        )
        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        val bounding = linearManager.getLayoutBounds(getLayoutParams())

        bounding.min shouldEqualInexact Vector3(0f, -5f, 0f)
        bounding.max shouldEqualInexact Vector3(2.9f, 0f, 0f)
    }

    @Test
    fun `should scale child nodes if layout size limited`() {
        size = Vector2(1f, 2f)
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.05F, 0.05F, 0.05F, 0.05F),
            childrenList[1] to Padding(0.05F, 0.05F, 0.05F, 0.05F)
        )

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        // 0.45 = (layout width - horizontal padding) / children sum width
        childrenList[0].localScale shouldEqualInexact Vector3(0.45f, 0.45f, 1f)
        childrenList[1].localScale shouldEqualInexact Vector3(0.45f, 0.45f, 1f)
    }

    private fun getLayoutParams() =
        LayoutParams(
            size = size,
            itemsAlignment = itemsAlignment,
            itemsPadding = itemsPadding
        )

}
