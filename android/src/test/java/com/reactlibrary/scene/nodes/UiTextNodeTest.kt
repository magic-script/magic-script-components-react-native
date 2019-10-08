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
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.FontParamsReader
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
class UiTextNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: TextView

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.viewSpy = spy(TextView(context))
    }

    @Test
    fun shouldHaveDefaultTextSize() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val textSize = node.getProperty(UiTextNode.PROP_TEXT_SIZE)

        assertEquals(UiTextNode.DEFAULT_TEXT_SIZE, textSize)
    }

    @Test
    fun shouldHaveDefaultAlignment() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val alignment = node.getProperty(TransformNode.PROP_ALIGNMENT)

        assertEquals(UiTextNode.DEFAULT_ALIGNMENT, alignment)
    }

    @Test
    fun shouldApplyTextWhenTextPropertyPresent() {
        val text = "ABC"
        val props = JavaOnlyMap.of(UiTextNode.PROP_TEXT, text)
        val node = createNodeWithViewSpy(props)

        node.build()

        assertEquals(text, viewSpy.text)
    }

    @Test
    fun shouldApplyTextSizeWhenTextSizePropertyPresent() {
        val textSize = 0.15F
        val props = JavaOnlyMap.of(UiTextNode.PROP_TEXT_SIZE, textSize)
        val node = createNodeWithViewSpy(props)
        val textSizeInPixels = Utils.metersToFontPx(textSize, context).toFloat()

        node.build()

        verify(viewSpy).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPixels)
    }

    @Test
    fun shouldApplySingleLineWhenWrapPropertyIsFalse() {
        val boundsData = JavaOnlyMap.of(
                UiTextNode.PROP_BOUNDS_SIZE, JavaOnlyArray.of(0.5, 0.0),
                UiTextNode.PROP_WRAP, false
        )
        val props = JavaOnlyMap.of(UiTextNode.PROP_BOUNDS_SIZE, boundsData)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).setSingleLine(true)
    }

    @Test
    fun shouldApplySingleLineWhenWidthIsDynamic() {
        val boundsData = JavaOnlyMap.of(
                UiTextNode.PROP_BOUNDS_SIZE, JavaOnlyArray.of(0.0, 0.2),
                UiTextNode.PROP_WRAP, true // wrap has no effect when size is 0 (dynamic)
        )
        val props = JavaOnlyMap.of(UiTextNode.PROP_BOUNDS_SIZE, boundsData)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).setSingleLine(true)
    }

    @Test
    fun shouldApplyTextAlignmentWhenAlignmentPropertyPresent() {
        val textAlignment = "right"
        val props = JavaOnlyMap.of(UiTextNode.PROP_TEXT_ALIGNMENT, textAlignment)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).gravity = Gravity.RIGHT
    }

    @Test
    fun shouldApplyTextColorWhenColorPropertyPresent() {
        val textColor = JavaOnlyArray.of(0.0, 0.0, 0.0, 0.0)
        val props = JavaOnlyMap.of(UiTextNode.PROP_TEXT_COLOR, textColor)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).setTextColor(0)
    }

    @Test
    fun shouldApplyCapitalLettersWhenAllCapsPropertyIsTrue() {
        val allCapsProp = JavaOnlyMap.of(FontParamsReader.PROP_ALL_CAPS, true)
        val props = JavaOnlyMap.of(UiTextEditNode.PROP_FONT_PARAMS, allCapsProp)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).isAllCaps = true
    }

    @Test
    fun shouldApplyCharactersSpacingWhenSpacingPropertyPresent() {
        val spacing = 0.1 // 'EM' units
        val props = JavaOnlyMap.of(UiTextNode.PROP_CHARACTERS_SPACING, spacing)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).letterSpacing = spacing.toFloat()
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiTextNode {
        return object : UiTextNode(props, context, mock(), mock()) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

}