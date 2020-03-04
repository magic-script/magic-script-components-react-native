/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magicleap.magicscript.scene.nodes.layouts

import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.params.PageViewLayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PageViewNodeTest {

    private lateinit var layoutManager: LayoutManager<PageViewLayoutParams>

    // local bounds of children inside the layout
    private val layoutBounds = AABB(min = Vector3(-2f, -2f, 0f), max = Vector3(0f, 1f, 0f))

    @Before
    fun setUp() {
        layoutManager = mock()
        whenever(layoutManager.getLayoutBounds(any())).thenReturn(layoutBounds)
    }

    @Test
    fun `should return layout bounds based on bounds returned by layout manager`() {
        val props = reactMapOf(
            TransformNode.PROP_ALIGNMENT, "center-center"
        )
        val node = createNode(props)

        val bounding = node.getBounding()

        bounding.min shouldEqualInexact Vector3(-1f, -1.5f, 0f)
        bounding.max shouldEqualInexact Vector3(1f, 1.5f, 0f)
    }

    @Test
    fun `should use top-left alignment by default`() {
        val props = JavaOnlyMap()
        val node = createNode(props)

        node.verticalAlignment shouldEqual Alignment.Vertical.TOP
        node.horizontalAlignment shouldEqual Alignment.Horizontal.LEFT
    }

    @Test
    fun `should apply passed content alignment`() {
        val node =
            createNode(reactMapOf(PageViewNode.PROP_DEFAULT_CONTENT_ALIGNMENT, "bottom-left"))
        val child = NodeBuilder().build()
        node.addContent(child)

        val layoutParams = node.getLayoutParams()

        layoutParams.itemsAlignment[child] shouldNotBe null
        layoutParams.itemsAlignment[child]!!.vertical shouldEqual Alignment.Vertical.BOTTOM
        layoutParams.itemsAlignment[child]!!.horizontal shouldEqual Alignment.Horizontal.LEFT
    }

    @Test
    fun `should set visible page when is passed`() {
        val props = reactMapOf(PageViewNode.PROP_VISIBLE_PAGE, 1.0)
        val node = createNode(props)

        val layoutParams = node.getLayoutParams()

        layoutParams.visiblePage shouldEqual 1
    }

    private fun createNode(props: JavaOnlyMap): PageViewNode {
        return PageViewNode(props, layoutManager).apply {
            build()
        }
    }
}