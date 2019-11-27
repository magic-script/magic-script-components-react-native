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
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.R
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.views.CustomScrollView
import com.magicleap.magicscript.update
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertTrue

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
        val scrollBoundsMap = JavaOnlyMap.of(
            "min", JavaOnlyArray.of(-0.8, -0.2, 0.1),
            "max", JavaOnlyArray.of(0.8, 0.2, 0.1)
        )
        val tested = createNodeWithViewSpy(
            JavaOnlyMap.of(
                UiScrollViewNode.PROP_SCROLL_BOUNDS,
                scrollBoundsMap
            )
        )
        tested.build() // need to recreate the view
        val expectedBounds = Bounding(-0.8f, -0.2f, 0.8f, 0.2f)

        val bounds = tested.getBounding()

        assertTrue(Bounding.equalInexact(expectedBounds, bounds))
    }

    @Test
    fun `should not change the hardcoded center-center alignment`() {
        tested.update(TransformNode.PROP_ALIGNMENT, "bottom-left")

        tested.verticalAlignment shouldEqual Alignment.VerticalAlignment.CENTER
        tested.horizontalAlignment shouldEqual Alignment.HorizontalAlignment.CENTER
    }

    @Test
    fun `should apply scroll direction`() {
        tested.update(UiScrollViewNode.PROP_SCROLL_DIRECTION, "horizontal")

        verify(viewSpy).scrollDirection = "horizontal"
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiScrollViewNode {
        return object : UiScrollViewNode(props, context, mock()) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

}