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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HorizontalLinearLayoutManagerTest {
    private val EPSILON = 1e-5f
    private lateinit var linearManager: HorizontalLinearLayoutManager<LayoutParams>
    private lateinit var childrenList: List<TransformNode>
    // <child index, bounding>
    private val childrenBounds = mutableMapOf<Int, AABB>()

    // Layout params
    private var size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
    private var itemPadding = Padding(0f, 0f, 0f, 0f)
    private var itemHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
    private var itemVerticalAlignment = Alignment.VerticalAlignment.TOP

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
    }

    @Test
    fun `should return correct layout bounds`() {
        itemPadding = Padding(0.2f, 0.2f, 0.1f, 0.1f)
        size = Vector2(WRAP_CONTENT_DIMENSION, 5f)
        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        val bounding = linearManager.getLayoutBounds(getLayoutParams())

        bounding.min shouldEqualInexact Vector3(0f, -5f, 0f)
        bounding.max shouldEqualInexact Vector3(4.6f, 0f, 0f)
    }

    @Test
    fun `should position children correctly when layout size is dynamic`() {
        size = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        itemPadding = Padding(0.5F, 0.5F, 0.5F, 0.5F)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        assertEquals(1.5f, childrenList[0].localPosition.x, EPSILON)
        assertEquals(4.5f, childrenList[1].localPosition.x, EPSILON)
        assertEquals(-1f, childrenList[0].localPosition.y, EPSILON)
        assertEquals(-1f, childrenList[1].localPosition.y, EPSILON)
    }

    @Test
    fun `should center children vertically and horizontally`() {
        size = Vector2(7f, 6f)
        itemHorizontalAlignment = Alignment.HorizontalAlignment.CENTER
        itemVerticalAlignment = Alignment.VerticalAlignment.CENTER
        itemPadding = Padding(0.5F, 0.5F, 0.5F, 0.5F)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        assertEquals(2f, childrenList[0].localPosition.x, EPSILON)
        assertEquals(5f, childrenList[1].localPosition.x, EPSILON)
        assertEquals(-3f, childrenList[0].localPosition.y, EPSILON)
        assertEquals(-3f, childrenList[1].localPosition.y, EPSILON)
    }

    @Test
    fun `should correctly scale down children when layout size limited`() {
        size = Vector2(4f, 6f)
        itemHorizontalAlignment = Alignment.HorizontalAlignment.CENTER
        itemVerticalAlignment = Alignment.VerticalAlignment.CENTER
        itemPadding = Padding(0.5F, 0.5F, 0.5F, 0.5F)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 50)

        // scale = (layout width - horizontal sum padding) / children sum width
        assertEquals(0.5f, childrenList[0].localScale.x, EPSILON)
        assertEquals(0.5f, childrenList[0].localScale.y, EPSILON)
        assertEquals(0.5f, childrenList[1].localScale.x, EPSILON)
        assertEquals(0.5f, childrenList[1].localScale.y, EPSILON)
    }

    @Test
    fun `should align children bottom-right`() {
        size = Vector2(7f, 6f)
        itemVerticalAlignment = Alignment.VerticalAlignment.BOTTOM
        itemHorizontalAlignment = Alignment.HorizontalAlignment.RIGHT
        itemPadding = Padding(0.5F, 0.5F, 0.5F, 0.5F)

        linearManager.layoutUntilStableBounds(childrenList, childrenBounds, getLayoutParams(), 10)

        assertEquals(2.5f, childrenList[0].localPosition.x, EPSILON)
        assertEquals(5.5f, childrenList[1].localPosition.x, EPSILON)
        assertEquals(-5f, childrenList[0].localPosition.y, EPSILON)
        assertEquals(-5f, childrenList[1].localPosition.y, EPSILON)
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
