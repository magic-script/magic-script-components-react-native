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

package com.reactlibrary.scene.nodes.toggle

import android.content.Context
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.reactlibrary.update
import com.reactlibrary.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiToggleNodeTest {

    private lateinit var toggle: UiToggleNode
    private lateinit var context: Context

    @Mock
    private lateinit var toggleViewManager: ToggleViewManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        context = ApplicationProvider.getApplicationContext()
        toggle = createNode(JavaOnlyMap())
        toggle.build()
    }

    @Test
    fun `should have default height`() {
        val height = toggle.getProperty(UiToggleNode.PROP_HEIGHT)

        assertEquals(UiToggleNode.DEFAULT_HEIGHT, height)
    }

    @Test
    fun `default text size should be equal to height`() {
        val height = 0.1
        val toggle = createNode(JavaOnlyMap.of(UiToggleNode.PROP_HEIGHT, height))

        val textSize = toggle.getProperty(UiToggleNode.PROP_TEXT_SIZE)

        assertEquals(height, textSize)
    }

    @Test
    fun `should apply text size when text size property updated`() {
        val textSize = 0.2
        val sizeInPixels = Utils.metersToFontPx(textSize.toFloat(), context)

        toggle.update(UiToggleNode.PROP_TEXT_SIZE, textSize)

        verify(toggleViewManager).setTextSize(sizeInPixels)
    }

    @Test
    fun `should apply text when text property updated`() {
        val text = "QWERTY"

        toggle.update(UiToggleNode.PROP_TEXT, text)

        verify(toggleViewManager).setText(eq(text))
    }

    @Test
    fun `should apply text color when color property updated`() {
        val textColor = JavaOnlyArray.of(1.0, 1.0, 1.0, 1.0)

        toggle.update(UiToggleNode.PROP_TEXT_COLOR, textColor)

        verify(toggleViewManager).setTextColor(0xFFFFFFFF.toInt())
    }

    fun createNode(props: ReadableMap): UiToggleNode {
        return UiToggleNode(props, context, mock(), toggleViewManager)
    }

}