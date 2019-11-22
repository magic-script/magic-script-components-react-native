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
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.reactlibrary.scene.nodes.toggle.ToggleViewManager
import com.reactlibrary.scene.nodes.toggle.UiToggleNode
import com.reactlibrary.utils.Utils
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
    private lateinit var toggleViewManager: ToggleViewManager

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.toggleViewManager = mock()
    }

    /*
    @Test
    fun `should use typeface from provider`() {
        val node = createNode(JavaOnlyMap())

        node.build()

        verify(textViewSpy).typeface = providerTypeface
    }
     */

    @Test
    fun `should have default height`() {
        val node = createNode(JavaOnlyMap())

        val height = node.getProperty(UiToggleNode.PROP_HEIGHT)

        assertEquals(UiToggleNode.DEFAULT_HEIGHT, height)
    }

    @Test
    fun `default text size should be equal to height`() {
        val height: Double = 0.1
        val props = JavaOnlyMap.of(UiToggleNode.PROP_HEIGHT, height)
        val node = createNode(props)

        val textSize = node.getProperty(UiToggleNode.PROP_TEXT_SIZE)

        assertEquals(height, textSize)
    }

    @Test
    fun `should apply text size when text size property present`() {
        val textSize = 0.2
        val sizeInPixels = Utils.metersToFontPx(textSize.toFloat(), context)
        val props = JavaOnlyMap.of(UiToggleNode.PROP_TEXT_SIZE, textSize)
        val node = createNode(props)

        node.build()

        verify(toggleViewManager).setTextSize(sizeInPixels)
    }

    @Test
    fun `should apply text when text property present`() {
        val text = "QWERTY"
        val props = JavaOnlyMap.of(UiToggleNode.PROP_TEXT, text)
        val node = createNode(props)

        node.build()

        verify(toggleViewManager).setText(eq(text))
    }

    @Test
    fun `should apply text color when color property present`() {
        val textColor = JavaOnlyArray.of(1.0, 1.0, 1.0, 1.0)
        val props = JavaOnlyMap.of(UiToggleNode.PROP_TEXT_COLOR, textColor)
        val node = createNode(props)

        node.build()

        verify(toggleViewManager).setTextColor(0xFFFFFFFF.toInt())
    }

    /*
    @Test
    fun `should set switch icon when type not specified`() {
        val node = createNode(JavaOnlyMap())

        node.build()

        verify(imageViewSpy).setImageResource(R.drawable.switch_off)
    }

    @Test
    fun `should set checkbox icon when checkbox property present`() {
        val props = JavaOnlyMap.of(UiToggleNode.PROP_TYPE, UiToggleNode.TYPE_CHECKBOX)
        val node = createNode(props)

        node.build()

        verify(imageViewSpy).setImageResource(R.drawable.checkbox_off)
    }

    @Test
    fun `should set checked radio icon when type is radio and checked`() {
        val props = JavaOnlyMap.of(
                UiToggleNode.PROP_TYPE, UiToggleNode.TYPE_RADIO,
                UiToggleNode.PROP_CHECKED, true
        )
        val node = createNode(props)

        node.build()

        verify(imageViewSpy).setImageResource(R.drawable.radio_on)
    }

    */

    private fun createNode(props: ReadableMap): UiToggleNode {
        return UiToggleNode(props, context, mock(), toggleViewManager)
    }

}