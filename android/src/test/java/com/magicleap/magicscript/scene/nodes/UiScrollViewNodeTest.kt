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
import com.magicleap.magicscript.*
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.views.CustomScrollView
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiScrollViewNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: CustomScrollView
    private lateinit var tested: UiScrollViewNode

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val view =
            LayoutInflater.from(context).inflate(R.layout.scroll_view, null) as CustomScrollView
        viewSpy = spy(view)
        tested = createNodeWithViewSpy(JavaOnlyMap())
        tested.build()
    }

    @Test
    fun `should be vertical by default`() {
        val scrollDirection = tested.getProperty(UiScrollViewNode.PROP_SCROLL_DIRECTION)

        scrollDirection shouldEqual UiScrollViewNode.SCROLL_DIRECTION_VERTICAL
    }

    @Test
    fun `should return correct bounds`() {
        val scrollBoundsMap = getScrollBoundsMap(1.6, 0.4, 0.1)
        val tested = createNodeWithViewSpy(
            reactMapOf(UiScrollViewNode.PROP_SCROLL_BOUNDS, scrollBoundsMap)
        )
        tested.build() // need to recreate the view

        val bounding = tested.getBounding()

        bounding.min shouldEqualInexact Vector3(-0.8f, -0.2f, 0f)
        bounding.max shouldEqualInexact Vector3(0.8f, 0.2f, 0f)
    }

    @Test
    fun `should not change the hardcoded center-center alignment`() {
        tested.update(TransformNode.PROP_ALIGNMENT, "bottom-left")

        tested.verticalAlignment shouldEqual Alignment.Vertical.CENTER
        tested.horizontalAlignment shouldEqual Alignment.Horizontal.CENTER
    }

    @Test
    fun `should apply scroll direction`() {
        tested.update(UiScrollViewNode.PROP_SCROLL_DIRECTION, "horizontal")

        verify(viewSpy).scrollDirection = "horizontal"
    }

    @Test
    fun `should remove scrollbar`() {
        val scrollBar = UiScrollBarNode(JavaOnlyMap())
        tested.addContent(scrollBar)

        tested.removeContent(scrollBar)

        tested.contentNode.children.size shouldEqual 0
    }

    @Test
    fun `should clip the content based on scroll view size`() {
        val scrollBoundsMap = getScrollBoundsMap(0.8, 0.4, 0.2)
        val tested = createNodeWithViewSpy(
            reactMapOf(UiScrollViewNode.PROP_SCROLL_BOUNDS, scrollBoundsMap)
        )
        tested.build()
        val expectedClipBounds = AABB(
            min = Vector3(-0.4f, -0.2f, -0.1f),
            max = Vector3(0.4f, 0.2f, 0.1f)
        )

        val contentNode = spy(
            UiNodeBuilder(context)
                .withSize(0.8f, 0.8f)
                .build()
        )
        tested.addContent(contentNode)

        contentNode.clipBounds shouldNotBe null
        contentNode.clipBounds!! shouldEqualInexact expectedClipBounds
    }

    @Test
    fun `should apply scroll bars visibility`() {
        tested.update(UiScrollViewNode.PROP_SCROLLBAR_VISIBILITY, "off")

        verify(viewSpy).scrollBarsVisibility = "off"
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiScrollViewNode {
        return object : UiScrollViewNode(props, context, mock(), mock()) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

    private fun getScrollBoundsMap(width: Double, height: Double, thickness: Double): JavaOnlyMap {
        return reactMapOf(
            "min", reactArrayOf(0.0, 0.0, 0.0),
            "max", reactArrayOf(width, height, thickness)
        )
    }


}