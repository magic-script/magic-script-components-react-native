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
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.PageViewLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PageViewNodeTest {

    private lateinit var layoutManager: PageViewLayoutManager

    @Before
    fun setUp() {
        layoutManager = mock()
    }

    @Test
    fun `should set top-left alignment when no alignment is passed`() {
        val props = JavaOnlyMap()
        val node = PageViewNode(props, layoutManager)
        node.build()

        verify(layoutManager).contentHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
        verify(layoutManager).contentVerticalAlignment = Alignment.VerticalAlignment.TOP
    }

    @Test
    fun `should set passed alignment`() {
        val props = JavaOnlyMap.of(PageViewNode.PROP_CONTENT_ALIGNMENT, "bottom-left")
        val node = PageViewNode(props, layoutManager)
        node.build()

        verify(layoutManager).contentHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
        verify(layoutManager).contentVerticalAlignment = Alignment.VerticalAlignment.BOTTOM
    }

    @Test
    fun `should return correct bounds`() {
        val props = JavaOnlyMap.of(UiLayout.PROP_WIDTH, 2.0, UiLayout.PROP_HEIGHT, 1.0)
        val node = PageViewNode(props, layoutManager)
        val expectedBounds = Bounding(-1F, -0.5F, 1F, 0.5F)
        node.build() // invokes the layout loop

        val bounds = node.getBounding()

        Assert.assertTrue(Bounding.equalInexact(expectedBounds, bounds))
    }

    @Test
    fun `should rescale child if bigger than layout size`() {
        val props = JavaOnlyMap.of(UiLayout.PROP_WIDTH, 1.0, UiLayout.PROP_HEIGHT, 1.0)
        val node = PageViewNode(props, layoutManager)

        val childNode = object : TransformNode(JavaOnlyMap(), false, false) {
            override fun onVisibilityChanged(visibility: Boolean) {

            }

            override fun getContentBounding(): Bounding {
                return Bounding(0f, 0f, 2f, 1f)
            }
        }
        childNode.build()
        node.addContent(childNode)
        node.build() // invokes the layout loop

        Assert.assertEquals(Vector3(0.5f, 0.5f, 1f), childNode.localScale)
    }

    @Test
    fun `should set visible page when is passed`() {
        val props = JavaOnlyMap.of(PageViewNode.PROP_VISIBLE_PAGE, 1.0)
        val node = PageViewNode(props, layoutManager)
        node.build()

        verify(layoutManager).visiblePage = 1
    }
}