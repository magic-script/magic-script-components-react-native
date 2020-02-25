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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.*
import com.magicleap.magicscript.R
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.utils.Utils
import kotlinx.android.synthetic.main.text_edit.view.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiTextEditNodeTest {

    private lateinit var context: Context
    private lateinit var containerSpy: LinearLayout
    private lateinit var scrollViewSpy: ScrollView
    private lateinit var textViewSpy: TextView
    private lateinit var fontProvider: FontProvider
    private lateinit var providerTypeface: Typeface

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.containerSpy = spy(LinearLayout(context))
        this.scrollViewSpy = spy(ScrollView(context))
        this.textViewSpy = spy(TextView(context))
        this.providerTypeface = Typeface.DEFAULT_BOLD
        this.fontProvider = object : FontProvider {
            override fun provideFont(fontParams: FontParams?): Typeface {
                return providerTypeface
            }
        }
        whenever(containerSpy.sv_text_edit).thenReturn(scrollViewSpy)
        whenever(containerSpy.text_edit).thenReturn(textViewSpy)
    }

    @Test
    fun shouldUseTypefaceFromProvider() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        node.build()

        verify(textViewSpy).typeface = providerTypeface
    }

    @Test
    fun shouldHaveDefaultTextSize() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()

        val textSize = node.getProperty(UiTextEditNode.PROP_TEXT_SIZE)

        assertEquals(UiTextEditNode.DEFAULT_TEXT_SIZE, textSize)
    }

    @Test
    fun shouldHaveDefaultAlignment() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()

        val alignment = node.getProperty(TransformNode.PROP_ALIGNMENT)

        assertEquals(UiTextEditNode.DEFAULT_ALIGNMENT, alignment)
    }

    @Test
    fun shouldHaveDefaultTextPadding() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()

        val textPadding = node.getProperty(UiTextEditNode.PROP_TEXT_PADDING)

        assertEquals(UiTextEditNode.DEFAULT_TEXT_PADDING, textPadding)
    }

    @Test
    fun shouldHaveDefaultCharactersSpacing() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()

        val charSpacing = node.getProperty(UiTextEditNode.PROP_CHARACTERS_SPACING)

        assertEquals(UiTextEditNode.DEFAULT_CHARACTERS_SPACING, charSpacing)
    }

    @Test
    fun shouldApplyTextWhenTextPropertyPresent() {
        val text = "xyz"
        val props = reactMapOf(UiTextEditNode.PROP_TEXT, text)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(textViewSpy).text = text
    }

    @Test
    fun shouldApplyHintWhenHintPropertyPresentAndTextIsEmpty() {
        val hint = "hint text"
        val props = reactMapOf(
            UiTextEditNode.PROP_HINT, hint,
            UiTextEditNode.PROP_TEXT, ""

        )
        val node = createNodeWithViewSpy(props)
        val hintColor = context.getColor(R.color.text_color_hint)

        node.build()

        verify(textViewSpy, atLeastOnce()).text = hint
        verify(textViewSpy, atLeastOnce()).setTextColor(hintColor)
    }

    @Test
    fun shouldApplyTextSizeWhenTextSizePropertyPresent() {
        val textSize = 0.15
        val sizeInPixels = Utils.metersToFontPx(textSize.toFloat(), context).toFloat()
        val props = reactMapOf(UiTextEditNode.PROP_TEXT_SIZE, textSize)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(textViewSpy).setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeInPixels)
    }

    @Test
    fun shouldApplyTextAlignmentWhenAlignmentPropertyPresent() {
        val textAlignment = "center"
        val props = reactMapOf(UiTextEditNode.PROP_TEXT_ALIGNMENT, textAlignment)
        val node = createNodeWithViewSpy(props)

        node.build()

        val gravity = textViewSpy.gravity
        assertTrue(gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.CENTER_HORIZONTAL)
    }

    @Test
    fun shouldApplyTextColorWhenColorPropertyPresentAndTextNotEmpty() {
        val text = "some text"
        val textColor = reactArrayOf(0.5, 1.0, 1.0, 1.0)
        val props = reactMapOf(
            UiTextEditNode.PROP_TEXT_COLOR, textColor,
            UiTextEditNode.PROP_TEXT, text
        )
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(textViewSpy).setTextColor(0xFF7FFFFF.toInt())
    }

    @Test
    fun shouldApplyCharactersSpacingWhenSpacingPropertyPresent() {
        val spacing = 0.3 // 'EM' units
        val props = reactMapOf(UiTextEditNode.PROP_CHARACTERS_SPACING, spacing)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(textViewSpy).letterSpacing = spacing.toFloat()
    }

    @Test
    fun shouldApplyMultilineWhenMultilinePropertyIsTrue() {
        val props = reactMapOf(UiTextEditNode.PROP_MULTILINE, true)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(textViewSpy).setSingleLine(false)
    }

    @Test
    fun shouldApplyTextPaddingWhenPaddingPropertyPresent() {
        val padding = reactArrayOf(2.0, 3.0, 2.0, 3.0)
        val props = reactMapOf(UiTextEditNode.PROP_TEXT_PADDING, padding)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(textViewSpy).setPadding(anyInt(), anyInt(), anyInt(), anyInt())
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiTextEditNode {
        return object : UiTextEditNode(props, context, mock(), mock(), fontProvider) {
            override fun provideView(context: Context): View {
                return containerSpy
            }
        }
    }

}