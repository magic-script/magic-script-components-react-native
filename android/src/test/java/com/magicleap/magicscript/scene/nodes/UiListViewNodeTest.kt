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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.R
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout
import com.magicleap.magicscript.scene.nodes.layouts.UiLinearLayout
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.views.CustomScrollView
import com.magicleap.magicscript.shouldEqualInexact
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeLessThan
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiListViewNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: CustomScrollView

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        val view =
            LayoutInflater.from(context).inflate(R.layout.scroll_view, null) as CustomScrollView
        this.viewSpy = spy(view)
    }

    @Test
    fun `should use linear layout as children container`() {
        val node = createNodeWithViewSpy(reactMapOf())
        node.build()

        node.contentNode.children.size shouldNotBeLessThan 1
        val containerNode = node.contentNode.children.firstOrNull()
        containerNode shouldBeInstanceOf UiLinearLayout::class
    }

    @Test
    fun `should return correct bounds`() {
        val props = reactMapOf(
            TransformNode.PROP_LOCAL_POSITION, reactArrayOf(1.0, 1.0, 0.0),
            UiListViewNode.PROP_WIDTH, 1.2, UiListViewNode.PROP_HEIGHT, 0.6
        )
        val node = createNodeWithViewSpy(props)
        node.build()

        val bounding = node.getBounding()

        bounding.min shouldEqualInexact Vector3(0.4f, 0.7f, 0f)
        bounding.max shouldEqualInexact Vector3(1.6f, 1.3f, 0f)
    }

    @Test
    fun `should apply orientation`() {
        val props = reactMapOf(UiListViewNode.PROP_ORIENTATION, "horizontal")
        val node = createNodeWithViewSpy(props)
        node.build()
        val container = node.contentNode.children.first() as UiLinearLayout

        verify(viewSpy).scrollDirection = "horizontal"
        container.getProperty("orientation") shouldEqual "horizontal"
    }

    @Test
    fun `should update orientation`() {
        val props = reactMapOf(UiListViewNode.PROP_ORIENTATION, "vertical")
        val node = createNodeWithViewSpy(props)
        node.build()
        val container = node.contentNode.children.first() as UiLinearLayout

        node.update(reactMapOf(UiListViewNode.PROP_ORIENTATION, "horizontal"))

        verify(viewSpy).scrollDirection = "horizontal"
        container.getProperty(UiLinearLayout.PROP_ORIENTATION) shouldEqual "horizontal"
    }

    @Test
    fun `should disable scrolling when scrollingEnabled property is false`() {
        val props = reactMapOf(UiListViewNode.PROP_SCROLLING_ENABLED, false)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).scrollingEnabled = false
    }

    @Test
    fun `list item should be added to the linear layout container`() {
        val node = createNodeWithViewSpy(reactMapOf())
        node.build()
        val listItem = UiListViewItemNode(reactMapOf(), context, mock(), mock())
        listItem.build()

        node.addContent(listItem)

        val containerNode = node.contentNode.children.firstOrNull()
        containerNode shouldBeInstanceOf UiLinearLayout::class
        (containerNode as UiLinearLayout).childrenList shouldContain listItem
    }

    @Test
    fun `should apply default items alignment on the container`() {
        val alignment = "top-right"
        val props = reactMapOf(UiListViewNode.PROP_DEFAULT_ITEM_ALIGNMENT, alignment)
        val node = createNodeWithViewSpy(props)
        val listItem = UiListViewItemNode(reactMapOf(), context, mock(), mock())

        node.build()
        node.addContent(listItem)

        val containerNode = node.contentNode.children.firstOrNull()
        containerNode shouldBeInstanceOf UiLinearLayout::class
        val layoutParams = (containerNode as UiLinearLayout).getLayoutParams()
        layoutParams.itemsAlignment[listItem]?.vertical shouldEqual Alignment.Vertical.TOP
        layoutParams.itemsAlignment[listItem]?.horizontal shouldEqual Alignment.Horizontal.RIGHT
    }

    @Test
    fun `should apply skipInvisibleItems property on the container`() {
        val props = reactMapOf(UiListViewNode.PROP_SKIP_INVISIBLE_ITEMS, true)
        val node = createNodeWithViewSpy(props)

        node.build()

        val containerNode = node.contentNode.children.firstOrNull()
        containerNode shouldBeInstanceOf UiLinearLayout::class
        (containerNode as UiLinearLayout)
        containerNode.getProperty(UiBaseLayout.PROP_SKIP_INVISIBLE_ITEMS) shouldEqual true
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiListViewNode {
        return object : UiListViewNode(props, context, mock(), mock()) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

}