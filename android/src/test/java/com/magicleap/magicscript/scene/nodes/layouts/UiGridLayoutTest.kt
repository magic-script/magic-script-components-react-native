/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.layouts

import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.layouts.params.GridLayoutParams
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.manager.LayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiGridLayoutTest {

    private lateinit var layoutManager: LayoutManager<GridLayoutParams>

    // local bounds of children inside the layout
    private val layoutBounds = Bounding(0f, -1f, 1f, 1f)

    @Before
    fun setUp() {
        layoutManager = mock()
        whenever(layoutManager.getLayoutBounds(any())).thenReturn(layoutBounds)
    }

    @Test
    fun `should return layout bounds based on bounds returned by layout manager`() {
        val props = reactMapOf(
            TransformNode.PROP_ALIGNMENT, "bottom-center"
        )
        val node = createNode(props)
        node.build()
        val expectedBounds = Bounding(left = -0.5f, bottom = 0.0f, right = 0.5f, top = 2.0f)

        val bounds = node.getBounding()

        bounds shouldEqualInexact expectedBounds
    }

    @Test
    fun `should use dynamic number of columns by default`() {
        val node = createNode(JavaOnlyMap())
        node.build()

        node.columns shouldEqual UiGridLayout.DYNAMIC_VALUE
    }

    @Test
    fun `should use 1 row by default`() {
        val node = createNode(JavaOnlyMap())
        node.build()

        node.rows shouldEqual 1
    }

    @Test
    fun `should not be able to set dynamic columns and rows number together`() {
        val node = createNode(
            reactMapOf(
                UiGridLayout.PROP_COLUMNS, UiGridLayout.DYNAMIC_VALUE,
                UiGridLayout.PROP_ROWS, UiGridLayout.DYNAMIC_VALUE
            )
        )
        node.build()

        node.rows shouldEqual 1
        node.columns shouldEqual UiGridLayout.DYNAMIC_VALUE
    }

    @Test
    fun `columns should take precedence over rows when both are set`() {
        val node = createNode(
            reactMapOf(
                UiGridLayout.PROP_COLUMNS, 2,
                UiGridLayout.PROP_ROWS, 3
            )
        )
        node.build()

        node.columns shouldEqual 2
        node.rows shouldEqual UiGridLayout.DYNAMIC_VALUE
    }

    @Test
    fun `should apply item alignment when passed`() {
        val props = reactMapOf(UiGridLayout.PROP_DEFAULT_ITEM_ALIGNMENT, "bottom-right")
        val node = createNode(props)
        node.build()

        val layoutParams = node.getLayoutParams()

        layoutParams.itemVerticalAlignment shouldEqual Alignment.VerticalAlignment.BOTTOM
        layoutParams.itemHorizontalAlignment shouldEqual Alignment.HorizontalAlignment.RIGHT
    }

    @Test
    fun `should update number of columns when columns property updated`() {
        val node = createNode(JavaOnlyMap())
        node.build()

        val props = reactMapOf(UiGridLayout.PROP_COLUMNS, 3.0)
        node.update(props)
        val layoutParams = node.getLayoutParams()

        layoutParams.columns shouldEqual 3
    }


    private fun createNode(props: JavaOnlyMap): UiGridLayout {
        return UiGridLayout(props, layoutManager)
    }

}