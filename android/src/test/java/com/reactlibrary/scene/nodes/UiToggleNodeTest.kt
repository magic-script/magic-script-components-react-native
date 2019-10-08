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
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.reactlibrary.R
import com.reactlibrary.utils.Utils
import kotlinx.android.synthetic.main.toggle.view.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiToggleNodeTest {

    private lateinit var context: Context
    private lateinit var containerSpy: LinearLayout
    private lateinit var textViewSpy: TextView
    private lateinit var switchSpy: ImageView

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.containerSpy = spy(LinearLayout(context))
        this.textViewSpy = spy(TextView(context))
        this.switchSpy = spy(ImageView(context))
        whenever(containerSpy.tv_toggle).thenReturn(textViewSpy)
        whenever(containerSpy.iv_toggle).thenReturn(switchSpy)
    }

    @Test
    fun shouldHaveDefaultHeight() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val height = node.getProperty(UiToggleNode.PROP_HEIGHT)

        assertEquals(UiToggleNode.DEFAULT_HEIGHT, height)
    }

    @Test
    fun defaultTextSizeShouldBeEqualToHeight() {
        val height: Double = 0.1
        val props = JavaOnlyMap.of(UiToggleNode.PROP_HEIGHT, height)
        val node = createNodeWithViewSpy(props)

        val textSize = node.getProperty(UiToggleNode.PROP_TEXT_SIZE)

        assertEquals(height, textSize)
    }

    @Test
    fun shouldApplyTextSizeWhenTextSizePropertyPresent() {
        val textSize = 0.2
        val sizeInPixels = Utils.metersToFontPx(textSize.toFloat(), context).toFloat()
        val props = JavaOnlyMap.of(UiToggleNode.PROP_TEXT_SIZE, textSize)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(textViewSpy).setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeInPixels)
    }

    @Test
    fun shouldApplyTextWhenTextPropertyPresent() {
        val text = "QWERTY"
        val props = JavaOnlyMap.of(UiToggleNode.PROP_TEXT, text)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(textViewSpy).text = text
    }

    @Test
    fun shouldApplyTextColorWhenColorPropertyPresent() {
        val textColor = JavaOnlyArray.of(1.0, 1.0, 1.0, 1.0)
        val props = JavaOnlyMap.of(UiToggleNode.PROP_TEXT_COLOR, textColor)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(textViewSpy).setTextColor(0xFFFFFFFF.toInt())
    }

    @Test
    fun shouldCheckTheSwitchWhenCheckPropertyIsTrue() {
        val props = JavaOnlyMap.of(UiToggleNode.PROP_CHECKED, true)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(switchSpy).setImageResource(R.drawable.toggle_on)
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiToggleNode {
        return object : UiToggleNode(props, context, mock(), mock()) {
            override fun provideView(context: Context): View {
                return containerSpy
            }
        }
    }

}