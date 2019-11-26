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
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.magicleap.magicscript.R
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.views.CustomScrollView
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
class UiListViewNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: CustomScrollView

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        val view = LayoutInflater.from(context).inflate(R.layout.scroll_view, null) as CustomScrollView
        this.viewSpy = spy(view)
    }

    @Test
    fun `should return correct bounds`() {
        val props = JavaOnlyMap.of(
                TransformNode.PROP_LOCAL_POSITION, JavaOnlyArray.of(1.0, 1.0, 0.0),
                UiListViewNode.PROP_WIDTH, 1.2, UiListViewNode.PROP_HEIGHT, 0.6
        )
        val node = createNodeWithViewSpy(props)
        node.build()
        val expectedBounds = Bounding(0.4f, 0.7f, 1.6f, 1.3f)

        val bounds = node.getBounding()

        assertTrue(Bounding.equalInexact(expectedBounds, bounds))
    }

    @Test
    fun `should apply orientation`() {
        val props = JavaOnlyMap.of(UiListViewNode.PROP_ORIENTATION, "horizontal")
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).scrollDirection = "horizontal"
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiListViewNode {
        return object : UiListViewNode(props, context, mock()) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

}