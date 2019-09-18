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
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.reactlibrary.scene.nodes.views.CustomButton
import com.reactlibrary.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 * [JavaOnlyMap] was not available in the initial versions of React
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UiButtonNodeTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun shouldHaveDefaultTextSize() {
        val node = UiButtonNode(JavaOnlyMap(), context)

        val textSize = node.getProperty(UiButtonNode.PROP_TEXT_SIZE)

        assertEquals(UiButtonNode.DEFAULT_TEXT_SIZE, textSize)
    }

    @Test
    fun shouldHaveDefaultRoundness() {
        val node = UiButtonNode(JavaOnlyMap(), context)

        val roundness = node.getProperty(UiButtonNode.PROP_ROUNDNESS)

        assertEquals(UiButtonNode.DEFAULT_ROUNDNESS, roundness)
    }


    @Test
    fun shouldSetTextWhenTextPropertyPresent() {
        val text = "ABC"
        val props = JavaOnlyMap.of(UiButtonNode.PROP_TEXT, text)
        val view = spy(CustomButton(context))
        val node = createButtonNode(props, view)

        node.build()

        verify(view).setText(text)
    }

    @Test
    fun shouldSetTextSizeWhenTextSizePropertyPresent() {
        val textSize = 0.05F
        val props = JavaOnlyMap.of(UiButtonNode.PROP_TEXT_SIZE, textSize)
        val view = spy(CustomButton(context))
        val node = createButtonNode(props, view)
        val textSizeInPixels = Utils.metersToFontPx(textSize, context).toFloat()

        node.build()

        verify(view).setTextSize(textSizeInPixels)
    }

    @Test
    fun shouldSetTextColorWhenColorPropertyPresent() {
        val textColor = JavaOnlyArray.of(1.0, 1.0, 1.0, 1.0)
        val props = JavaOnlyMap.of(UiButtonNode.PROP_TEXT_COLOR, textColor)
        val view = spy(CustomButton(context))
        val node = createButtonNode(props, view)

        node.build()

        verify(view).setTextColor(0xFFFFFFFF.toInt())
    }

    @Test
    fun shouldSetRoundnessWhenRoundnessPropertyPresent() {
        val roundness = 0.2
        val props = JavaOnlyMap.of(UiButtonNode.PROP_ROUNDNESS, roundness)
        val view = spy(CustomButton(context))
        val node = createButtonNode(props, view)

        node.build()

        verify(view).setRoundnessFactor(roundness.toFloat())
    }

    private fun createButtonNode(props: ReadableMap, viewMock: View): UiButtonNode {
        return object : UiButtonNode(props, context) {
            override fun provideView(context: Context): View {
                return viewMock
            }
        }
    }

}