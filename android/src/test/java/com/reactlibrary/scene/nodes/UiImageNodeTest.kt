/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactlibrary.scene.nodes

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import junit.framework.Assert.assertEquals
import kotlinx.android.synthetic.main.image.view.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiImageNodeTest {

    private lateinit var context: Context
    private lateinit var containerSpy: FrameLayout
    private lateinit var imageViewSpy: ImageView
    private lateinit var viewRenderableLoader: ViewRenderableLoader

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.viewRenderableLoader = mock()
        this.containerSpy = spy(FrameLayout(context))
        this.imageViewSpy = spy(ImageView(context))
        whenever(containerSpy.image_view).thenReturn(imageViewSpy)
    }

    @Test
    fun shouldMixColorWithImageWhenImagePathAndColorPropertiesPresent() {
        val props = JavaOnlyMap.of(
                UiImageNode.PROP_FILE_PATH, "http://sample-image.com",
                UiImageNode.PROP_COLOR, JavaOnlyArray.of(1.0, 1.0, 1.0, 1.0)
        )
        val node = createNodeWithViewSpy(props)
        val expectedFilter = PorterDuffColorFilter(0XFFFFFFFF.toInt(), PorterDuff.Mode.MULTIPLY)

        node.build()

        assertEquals(expectedFilter, imageViewSpy.colorFilter)
    }

    @Test
    fun shouldApplyBackgroundColorWhenProvidedColorWithoutImagePath() {
        val props = JavaOnlyMap.of(
                UiImageNode.PROP_COLOR, JavaOnlyArray.of(0.0, 0.0, 0.0, 0.0)
        )
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(imageViewSpy).setBackgroundColor(0)
    }

    @Test
    fun shouldApplyFrameWhenUseFramePropertyIsTrue() {
        val props = JavaOnlyMap.of(UiImageNode.PROP_FRAME, true)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(containerSpy).setBackgroundResource(R.drawable.image_border)
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiImageNode {
        return object : UiImageNode(props, context, mock()) {
            override fun provideView(context: Context): View {
                return containerSpy
            }
        }
    }

}