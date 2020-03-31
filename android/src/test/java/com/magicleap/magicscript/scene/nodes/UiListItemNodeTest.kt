/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.UiNodeBuilder
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.shouldEqualInexact
import com.magicleap.magicscript.utils.Vector2
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiListItemNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: View

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.viewSpy = spy(View(context))
    }

    @Test
    fun `should apply background color`() {
        val color = reactArrayOf(1.0, 1.0, 1.0, 1.0)
        val props = reactMapOf(UiListViewItemNode.PROP_BACKGROUND_COLOR, color)

        buildNodeWithViewSpy(props)

        verify(viewSpy).setBackgroundColor(0xFFFFFFFF.toInt())
    }

    @Test
    fun `should align content at the center when minSize not specified`() {
        val node = buildNodeWithViewSpy(reactMapOf())
        // contentAlignment should not matter because content size is equal to list item size
        node.contentAlignment = Alignment(Alignment.Vertical.BOTTOM, Alignment.Horizontal.LEFT)
        node.contentPadding = Padding(0f, 0f, 0f, 0f)
        val child = UiNodeBuilder(context)
            .withSize(0.4f, 0.2f)
            .withAlignment("center-center")
            .build()

        node.addContent(child)

        child.localPosition.x shouldEqualInexact 0f
        child.localPosition.y shouldEqualInexact 0f
    }

    @Test
    fun `should correctly align content if contentAlignment is top-right and minSize set`() {
        val node = buildNodeWithViewSpy(reactMapOf())
        node.contentAlignment = Alignment(Alignment.Vertical.TOP, Alignment.Horizontal.RIGHT)
        node.minSize = Vector2(2f, 1f)
        node.contentPadding = Padding(0f, 0f, 0f, 0f)
        val child = UiNodeBuilder(context)
            .withSize(0.4f, 0.2f)
            .withAlignment("top-right")
            .build()

        node.addContent(child)

        child.localPosition.x shouldEqualInexact 1.0f
        child.localPosition.y shouldEqualInexact 0.5f
    }

    @Test
    fun `should correctly align content center-left if contentPadding specified`() {
        val node = buildNodeWithViewSpy(reactMapOf())
        node.contentAlignment = Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.LEFT)
        node.minSize = Vector2(2f, 1f)
        node.contentPadding = Padding(top = 0f, right = 0f, bottom = 0f, left = 0.1f)
        val child = UiNodeBuilder(context)
            .withSize(0.4f, 0.2f)
            .withAlignment("center-center")
            .build()

        node.addContent(child)

        child.localPosition.x shouldEqualInexact -0.7f
        child.localPosition.y shouldEqualInexact 0f
    }

    @Test
    fun `should correctly align content center-center if contentPadding specified`() {
        val node = buildNodeWithViewSpy(reactMapOf())
        node.contentAlignment = Alignment(Alignment.Vertical.CENTER, Alignment.Horizontal.LEFT)
        node.minSize = Vector2(2f, 1f)
        node.contentPadding = Padding(top = 0.2f, right = 0.2f, bottom = 0.1f, left = 0.1f)
        val child = UiNodeBuilder(context)
            .withSize(0.8f, 0.5f)
            .withAlignment("center-center")
            .build()

        node.addContent(child)

        child.localPosition.x shouldEqualInexact -0.5f
        child.localPosition.y shouldEqualInexact -0.05f
    }

    private fun buildNodeWithViewSpy(props: ReadableMap): UiListViewItemNode {
        return object : UiListViewItemNode(props, context, mock(), mock()) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }.apply {
            build()
        }
    }

}