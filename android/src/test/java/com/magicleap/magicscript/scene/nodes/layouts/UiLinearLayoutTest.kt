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
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.manager.LinearLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
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
class UiLinearLayoutTest {

    private lateinit var layoutManager: LinearLayoutManager

    // local bounds of children inside the layout
    private val layoutBounds = Bounding(0f, -3f, 2f, 1f)

    @Before
    fun setUp() {
        layoutManager = mock()
        whenever(layoutManager.getLayoutBounds(any())).thenReturn(layoutBounds)
    }

    @Test
    fun `should return layout bounds based on bounds returned by layout manager`() {
        val props = reactMapOf(
            TransformNode.PROP_ALIGNMENT, "top-left"
        )
        val node = createNode(props)
        val expectedBounds = Bounding(0f, -4f, 2f, 0f)

        val bounds = node.getBounding()

        bounds shouldEqualInexact expectedBounds
    }

    @Test
    fun `should use vertical orientation by default`() {
        val node = createNode(JavaOnlyMap())

        node.getProperty("orientation") shouldEqual "vertical"
    }

    @Test
    fun `should apply item padding when item padding property present`() {
        val padding = reactArrayOf(1.5, 2.0, 1.5, 0.0)
        val props = reactMapOf(UiLinearLayout.PROP_DEFAULT_ITEM_PADDING, padding)
        val node = createNode(props)

        val layoutParams = node.getLayoutParams()

        layoutParams.itemsPadding[0] shouldEqual Padding(1.5f, 2.0f, 1.5f, 0.0f)
    }

    private fun createNode(props: JavaOnlyMap): UiLinearLayout {
        return UiLinearLayout(props, layoutManager).apply {
            build()
            addContent(NodeBuilder().build())
        }
    }

}