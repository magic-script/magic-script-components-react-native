/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.font.FontStyle
import com.magicleap.magicscript.font.FontWeight
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.selected
import com.magicleap.magicscript.text
import com.magicleap.magicscript.utils.Utils
import com.nhaarman.mockitokotlin2.*
import kotlinx.android.synthetic.main.text_edit.view.*
import org.amshove.kluent.shouldEqual
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
    private lateinit var editTextViewSpy: EditText
    private lateinit var fontProvider: FontProvider
    private lateinit var providerTypeface: Typeface

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.containerSpy = spy(LinearLayout(context))
        this.scrollViewSpy = spy(ScrollView(context))
        this.editTextViewSpy = spy(EditText(context))
        this.providerTypeface = Typeface.DEFAULT_BOLD
        this.fontProvider = object : FontProvider {
            override fun provideFont(fontStyle: FontStyle?, fontWeight: FontWeight?): Typeface {
                return providerTypeface
            }
        }
        whenever(containerSpy.sv_text_edit).thenReturn(scrollViewSpy)
        whenever(containerSpy.text_edit).thenReturn(editTextViewSpy)
    }

    @Test
    fun `should use typeface from provider`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        node.build()

        verify(editTextViewSpy).typeface = providerTypeface
    }

    @Test
    fun `should have default text size`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()

        val textSize = node.getProperty(UiTextEditNode.PROP_TEXT_SIZE)

        assertEquals(UiTextEditNode.DEFAULT_TEXT_SIZE, textSize)
    }

    @Test
    fun `should have default alignment`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()

        val alignment = node.getProperty(TransformNode.PROP_ALIGNMENT)

        assertEquals(UiTextEditNode.DEFAULT_ALIGNMENT, alignment)
    }

    @Test
    fun `should have default text padding`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()

        val textPadding = node.getProperty(UiTextEditNode.PROP_TEXT_PADDING)

        assertEquals(UiTextEditNode.DEFAULT_TEXT_PADDING, textPadding)
    }

    @Test
    fun `should have default characters spacing`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()

        val charSpacing = node.getProperty(UiTextEditNode.PROP_CHARACTERS_SPACING)

        assertEquals(UiTextEditNode.DEFAULT_CHARACTERS_SPACING, charSpacing)
    }

    @Test
    fun `should apply text when text property present`() {
        val text = "xyz"
        val props = reactMapOf(UiTextEditNode.PROP_TEXT, text)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(editTextViewSpy).setText(eq(text), anyOrNull())
    }

    @Test
    fun `should apply hint when hint property present and text is empty`() {
        val hint = "hint text"
        val props = reactMapOf(
            UiTextEditNode.PROP_HINT, hint,
            UiTextEditNode.PROP_TEXT, ""

        )
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(editTextViewSpy, atLeastOnce()).hint = hint
    }

    @Test
    fun `should apply text size when text size property present`() {
        val textSize = 0.15
        val sizeInPixels = Utils.metersToFontPx(textSize.toFloat(), context).toFloat()
        val props = reactMapOf(UiTextEditNode.PROP_TEXT_SIZE, textSize)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(editTextViewSpy).setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeInPixels)
    }

    @Test
    fun `should apply text alignment when alignment property present`() {
        val textAlignment = "center"
        val props = reactMapOf(UiTextEditNode.PROP_TEXT_ALIGNMENT, textAlignment)
        val node = createNodeWithViewSpy(props)

        node.build()

        val gravity = editTextViewSpy.gravity
        assertTrue(gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.CENTER_HORIZONTAL)
    }

    @Test
    fun `should apply text color when color property present and text not empty`() {
        val text = "some text"
        val textColor = reactArrayOf(0.5, 1.0, 1.0, 1.0)
        val props = reactMapOf(
            UiTextEditNode.PROP_TEXT_COLOR, textColor,
            UiTextEditNode.PROP_TEXT, text
        )
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(editTextViewSpy).setTextColor(0xFF7FFFFF.toInt())
    }

    @Test
    fun `should apply characters spacing when spacing property present`() {
        val spacing = 0.3 // 'EM' units
        val props = reactMapOf(UiTextEditNode.PROP_CHARACTERS_SPACING, spacing)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(editTextViewSpy).letterSpacing = spacing.toFloat()
    }

    @Test
    fun `should apply line spacing when lineSpacing property present`() {
        val spacing = 0.9 // multiplier
        val props = reactMapOf(UiTextEditNode.PROP_LINE_SPACING, spacing)
        val node = createNodeWithViewSpy(props)

        node.build()

        editTextViewSpy.lineSpacingMultiplier shouldEqual spacing.toFloat()
    }

    @Test
    fun `should apply multiline when multiline property is true`() {
        val props = reactMapOf(UiTextEditNode.PROP_MULTILINE, true)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(editTextViewSpy).setSingleLine(false)
    }

    @Test
    fun `should apply text padding when padding property present`() {
        val padding = reactArrayOf(2.0, 3.0, 2.0, 3.0)
        val props = reactMapOf(UiTextEditNode.PROP_TEXT_PADDING, padding)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(editTextViewSpy).setPadding(anyInt(), anyInt(), anyInt(), anyInt())
    }

    @Test
    fun `should read selected begin and end`() {
        val node = createNodeWithViewSpy(
            JavaOnlyMap.of()
                .text("longText")
                .selected(1, 3)
        )

        node.build()

        verify(editTextViewSpy, atLeastOnce()).setSelection(1, 3)
    }

    @Test
    fun `should set selected begin and end in min-max text lenght`() {
        val node = createNodeWithViewSpy(
            JavaOnlyMap.of()
                .text("text")
                .selected(-1, 5)
        )

        node.build()

        verify(editTextViewSpy, atLeastOnce()).setSelection(0, 4)
    }


    private fun createNodeWithViewSpy(props: ReadableMap): UiTextEditNode {
        return object : UiTextEditNode(props, context, mock(), mock(), fontProvider) {
            override fun provideView(context: Context): View {
                return containerSpy
            }
        }
    }

}