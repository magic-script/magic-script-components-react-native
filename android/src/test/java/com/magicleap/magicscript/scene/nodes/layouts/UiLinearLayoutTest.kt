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
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.nhaarman.mockitokotlin2.*
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiLinearLayoutTest {

    private lateinit var linearLayoutManager: VerticalLinearLayoutManager

    @Before
    fun setUp() {
        linearLayoutManager = mock()
        whenever(linearLayoutManager.getLayoutBounds()).thenReturn(
            Bounding(1f, 1f, 1f, 1f)
        )
    }

    @Test
    fun `should layout children on build`() {
        val node = createNode(JavaOnlyMap())
        node.build()

        verify(linearLayoutManager, atLeastOnce()).layoutChildren(any(), any())
    }

    @Test
    fun `should apply vertical orientation by default`() {
        val node = createNode(JavaOnlyMap())
        node.build()

        linearLayoutManager shouldBeInstanceOf VerticalLinearLayoutManager::class
    }

    @Test
    fun `should apply item padding when item padding property present`() {
        val padding = reactArrayOf(1.5, 2.0, 1.5, 0.0)
        val props = reactMapOf(UiLinearLayout.PROP_DEFAULT_ITEM_PADDING, padding)
        val node = createNode(props)
        node.build()

        verify(linearLayoutManager).itemPadding = any()
    }

    private fun createNode(props: JavaOnlyMap): UiLinearLayout {
        return UiLinearLayout(props, linearLayoutManager)
    }

}