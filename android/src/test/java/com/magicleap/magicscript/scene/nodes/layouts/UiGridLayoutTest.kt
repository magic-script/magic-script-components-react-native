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
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.nhaarman.mockitokotlin2.*
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

    private lateinit var gridLayoutManager: GridLayoutManager

    @Before
    fun setUp() {
        gridLayoutManager = mock()
        whenever(gridLayoutManager.getLayoutBounds()).thenReturn(
            Bounding(1f, 1f, 1f, 1f)
        )
    }

    @Test
    fun `should layout children on build`() {
        val node = createNode(JavaOnlyMap())
        node.build()

        verify(gridLayoutManager, atLeastOnce()).layoutChildren(any(), any())
    }

    @Test
    fun `should use dynamic number of columns by default`() {
        val node = createNode(JavaOnlyMap())
        node.build()

        verify(gridLayoutManager).columns = 0 // 0 means dynamic
    }

    @Test
    fun `should use 1 row by default`() {
        val node = createNode(JavaOnlyMap())
        node.build()

        verify(gridLayoutManager).rows = 1
    }

    @Test
    fun `should apply item alignment when item alignment property present`() {
        val props = reactMapOf(UiGridLayout.PROP_DEFAULT_ITEM_ALIGNMENT, "bottom-right")
        val node = createNode(props)
        node.build()

        verify(gridLayoutManager).itemVerticalAlignment = Alignment.VerticalAlignment.BOTTOM
        verify(gridLayoutManager).itemHorizontalAlignment = Alignment.HorizontalAlignment.RIGHT
    }

    @Test
    fun `should update number of columns when columns property updated`() {
        val node = createNode(JavaOnlyMap())
        node.build()

        val columns = 3
        val props = reactMapOf(UiGridLayout.PROP_COLUMNS, columns.toDouble())
        node.update(props)

        verify(gridLayoutManager).columns = columns
    }

    private fun createNode(props: JavaOnlyMap): UiGridLayout {
        return UiGridLayout(props, gridLayoutManager)
    }

}