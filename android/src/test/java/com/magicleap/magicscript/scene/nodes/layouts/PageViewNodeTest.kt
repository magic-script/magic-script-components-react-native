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
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.PageViewLayoutParams
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.spy
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PageViewNodeTest {

    private lateinit var layoutManager: LayoutManager<PageViewLayoutParams>

    @Before
    fun setUp() {
        layoutManager = spy(VerticalLinearLayoutManager())
    }

    @Test
    fun `should set top-left alignment when no alignment is passed`() {
        val props = JavaOnlyMap()
        val node = createNode(props)
        node.build()

        node.verticalAlignment shouldEqual Alignment.VerticalAlignment.TOP
        node.horizontalAlignment shouldEqual Alignment.HorizontalAlignment.LEFT
    }

    @Test
    fun `should apply passed content alignment`() {
        val props = reactMapOf(PageViewNode.PROP_CONTENT_ALIGNMENT, "bottom-left")
        val node = createNode(props)
        node.build()

        val layoutParams = node.getLayoutParams()

        layoutParams.itemVerticalAlignment shouldEqual Alignment.VerticalAlignment.BOTTOM
        layoutParams.itemHorizontalAlignment shouldEqual Alignment.HorizontalAlignment.LEFT
    }

    @Test
    fun `should return correct bounds`() {
        val props = reactMapOf(
            TransformNode.PROP_ALIGNMENT, "top-left",
            PageViewNode.PROP_CONTENT_ALIGNMENT, "top-left",
            UiBaseLayout.PROP_WIDTH, 2.0,
            UiBaseLayout.PROP_HEIGHT, 1.0
        )
        val node = createNode(props)
        val expectedBounds = Bounding(0F, -1F, 2F, 0F)
        node.build()

        val bounds = node.getBounding()

        bounds shouldEqualInexact expectedBounds
    }

    @Test
    fun `should set visible page when is passed`() {
        val props = reactMapOf(PageViewNode.PROP_VISIBLE_PAGE, 1.0)
        val node = createNode(props)
        node.build()

        val layoutParams = node.getLayoutParams()

        layoutParams.visiblePage shouldEqual 1
    }

    private fun createNode(props: JavaOnlyMap): PageViewNode {
        return PageViewNode(props, layoutManager)
    }
}