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
class HorizontalLinearLayoutManagerTest {
    private lateinit var linearManager: HorizontalLinearLayoutManager<LayoutParams>
    private lateinit var childrenList: List<TransformNode>
    private lateinit var itemsPadding: Map<TransformNode, Padding>
    private lateinit var itemsAlignment: Map<TransformNode, Alignment>

    private val childrenBounds = mutableMapOf<TransformNode, AABB>()

    // Layout params
    private var size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)

    @Before
    fun setUp() {
        this.linearManager = HorizontalLinearLayoutManager()
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
    fun `should return correct layout bounds after children layout`() {
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.2f, 0.4f, 0.1f, 0.1f),
            childrenList[1] to Padding(0.2f, 0.2f, 0.1f, 0.6f)
        )
        size = Vector2(WRAP_CONTENT_DIMENSION, 5f)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        val bounding = linearManager.getLayoutBounds(getLayoutParams())
        bounding.min shouldEqualInexact Vector3(0f, -5f, 0f)
        bounding.max shouldEqualInexact Vector3(5.3f, 0f, 0f)
    }

    @Test
    fun `should layout children correctly when items padding is different`() {
        size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.5F, 0.2F, 0.5F, 0.4F),
            childrenList[1] to Padding(1F, 0.5F, 0.5F, 0.5F)
        )

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        childrenList[0].localPosition shouldEqualInexact Vector3(1.4f, -1f, 0f)
        childrenList[1].localPosition shouldEqualInexact Vector3(4.1f, -1.5f, 0f)
    }

    @Test
    fun `should center children vertically and horizontally`() {
        size = Vector2(7f, 6f)
        itemsAlignment = mapOf(
            childrenList[0] to Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.CENTER),
            childrenList[1] to Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.CENTER)
        )
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.5F, 0.5F, 0.5F, 0.5F),
            childrenList[1] to Padding(0.5F, 0.5F, 0.5F, 0.5F)
        )

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        childrenList[0].localPosition shouldEqualInexact Vector3(2f, -3f, 0f)
        childrenList[1].localPosition shouldEqualInexact Vector3(5f, -3f, 0f)
    }

    @Test
    fun `should correctly scale down children when layout size limited`() {
        size = Vector2(4f, 6f)
        itemsAlignment = mapOf(
            childrenList[0] to Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.CENTER),
            childrenList[1] to Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.CENTER)
        )
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.5F, 0.5F, 0.5F, 0.5F),
            childrenList[1] to Padding(0.5F, 0.5F, 0.5F, 0.5F)
        )

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 50)

        // scale = (layout width - horizontal sum padding) / children sum width
        childrenList[0].localScale shouldEqualInexact Vector3(0.5f, 0.5f, 1f)
        childrenList[1].localScale shouldEqualInexact Vector3(0.5f, 0.5f, 1f)
    }

    @Test
    fun `should align children bottom-right`() {
        size = Vector2(7f, 6f)
        itemsAlignment = mapOf(
            childrenList[0] to Alignment(Alignment.Vertical.BOTTOM, Alignment.Horizontal.RIGHT),
            childrenList[1] to Alignment(Alignment.Vertical.BOTTOM, Alignment.Horizontal.RIGHT)
        )
        itemsPadding = mapOf(
            childrenList[0] to Padding(0.5F, 0.5F, 0.5F, 0.5F),
            childrenList[1] to Padding(0.5F, 0.5F, 0.5F, 0.5F)
        )

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        childrenList[0].localPosition shouldEqualInexact Vector3(2.5f, -5f, 0f)
        childrenList[1].localPosition shouldEqualInexact Vector3(5.5f, -5f, 0f)
    }

    private fun getLayoutParams() =
        LayoutParams(
            size = size,
            itemsAlignment = itemsAlignment,
            itemsPadding = itemsPadding
        )

}
