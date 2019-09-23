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
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.TransformNode
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
class UiTextNodeTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun shouldHaveDefaultTextSize() {
        val node = UiTextNode(JavaOnlyMap(), context)

        val textSize = node.getProperty(UiTextNode.PROP_TEXT_SIZE)

        assertEquals(UiTextNode.DEFAULT_TEXT_SIZE, textSize)
    }

    @Test
    fun shouldHaveDefaultAlignment() {
        val node = UiTextNode(JavaOnlyMap(), context)

        val alignment = node.getProperty(TransformNode.PROP_ALIGNMENT)

        assertEquals(UiTextNode.DEFAULT_ALIGNMENT, alignment)
    }

    @Test
    fun shouldSetTextWhenTextPropertyPresent() {
        val text = "ABC"
        val props = JavaOnlyMap.of(UiTextNode.PROP_TEXT, text)
        val view = TextView(context)
        val node = createTextNode(props, view)

        node.build()

        assertEquals(text, view.text)
    }

    @Test
    fun shouldSetTextSizeWhenTextSizePropertyPresent() {
        val textSize = 0.15F
        val props = JavaOnlyMap.of(UiTextNode.PROP_TEXT_SIZE, textSize)
        val view = TextView(context)
        val node = createTextNode(props, view)
        val textSizeInPixels = Utils.metersToFontPx(textSize, context).toFloat()

        node.build()

        assertEquals(textSizeInPixels, view.textSize)
    }

    private fun createTextNode(props: ReadableMap, viewMock: View): UiTextNode {
        return object : UiTextNode(props, context) {
            override fun provideView(context: Context): View {
                return viewMock
            }
        }
    }


}