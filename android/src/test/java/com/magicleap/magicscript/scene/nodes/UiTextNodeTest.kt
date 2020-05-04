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
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.font.FontStyle
import com.magicleap.magicscript.font.FontWeight
import com.magicleap.magicscript.reactArrayOf
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.shouldEqualInexact
import com.magicleap.magicscript.utils.Utils
import com.nhaarman.mockitokotlin2.*
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
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
    private lateinit var fontProvider: FontProvider
    private lateinit var providerTypeface: Typeface

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.viewSpy = spy(TextView(context))
        this.providerTypeface = Typeface.DEFAULT_BOLD
        this.fontProvider = object : FontProvider {
            override fun provideFont(fontStyle: FontStyle?, fontWeight: FontWeight?): Typeface {
                return providerTypeface
            }
        }
    }

    @Test
    fun `should use typeface from provider`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        node.build()

        verify(viewSpy).typeface = providerTypeface
    }

    @Test
    fun `should have default text size`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val textSize = node.getProperty(UiTextNode.PROP_TEXT_SIZE)

        assertEquals(UiTextNode.DEFAULT_TEXT_SIZE, textSize)
    }

    @Test
    fun `should have default alignment`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val alignment = node.getProperty(TransformNode.PROP_ALIGNMENT)

        assertEquals(UiTextNode.DEFAULT_ALIGNMENT, alignment)
    }

    @Test
    fun `should apply text when text property present`() {
        val text = "ABC"
        val props = reactMapOf(UiTextNode.PROP_TEXT, text)
        val node = createNodeWithViewSpy(props)

        node.build()

        assertEquals(text, viewSpy.text)
    }

    @Test
    fun `should apply text size when text size property present`() {
        val textSize = 0.15F
        val props = reactMapOf(UiTextNode.PROP_TEXT_SIZE, textSize)
        val node = createNodeWithViewSpy(props)
        val textSizeInPixels = Utils.metersToFontPx(textSize, context).toFloat()

        node.build()

        verify(viewSpy).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPixels)
    }

    @Test
    fun `should apply single line when wrap property is false`() {
        val boundsData = reactMapOf(
            UiTextNode.PROP_BOUNDS_SIZE, reactArrayOf(0.5, 0.0),
            UiTextNode.PROP_WRAP, false
        )
        val props = reactMapOf(UiTextNode.PROP_BOUNDS_SIZE, boundsData)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy, atLeastOnce()).setSingleLine(true)
    }

    @Test
    fun `should not set single line when bounds size not specified`() {
        val props = reactMapOf(UiTextNode.PROP_TEXT, "abc")
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy, never()).setSingleLine()
        verify(viewSpy, never()).setSingleLine(true)
    }

    @Test
    fun `should apply text alignment when property present`() {
        val textAlignment = "right"
        val props = reactMapOf(UiTextNode.PROP_TEXT_ALIGNMENT, textAlignment)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).gravity = Gravity.RIGHT
    }

    @Test
    fun `should apply text color when color property present`() {
        val textColor = reactArrayOf(0.0, 0.0, 0.0, 0.0)
        val props = reactMapOf(UiTextNode.PROP_TEXT_COLOR, textColor)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).setTextColor(0)
    }

    @Test
    fun `should apply capital letters when allCaps property is true`() {
        val node = createNodeWithViewSpy(reactMapOf("allCaps", true))

        node.build()

        verify(viewSpy).isAllCaps = true
    }

    @Test
    fun `should apply characters spacing when spacing property present`() {
        val spacing = 0.1 // 'EM' units
        val props = reactMapOf(UiTextNode.PROP_CHARACTERS_SPACING, spacing)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).letterSpacing = spacing.toFloat()
    }

    @Test
    fun `should apply line spacing when lineSpacing property present`() {
        val spacing = 2.0 // multiplier
        val props = reactMapOf(UiTextNode.PROP_LINE_SPACING, spacing)
        val node = createNodeWithViewSpy(props)

        node.build()

        viewSpy.lineSpacingMultiplier shouldEqual spacing.toFloat()
    }

    @Test
    fun `should return proper bounds`() {
        val boundsSize = reactMapOf(UiTextNode.PROP_BOUNDS_SIZE, reactArrayOf(0.8, 0.4))
        val props = reactMapOf(
            UiTextNode.PROP_BOUNDS_SIZE, boundsSize,
            TransformNode.PROP_ALIGNMENT, "top-left"
        )
        val node = createNodeWithViewSpy(props)
        node.build()

        val bounding = node.getBounding()

        bounding.min shouldEqualInexact Vector3(0f, -0.4f, 0f)
        bounding.max shouldEqualInexact Vector3(0.8f, 0f, 0f)
    }

    @Test
    fun `should update bounds when text changed`() {
        val node = createNodeWithViewSpy(reactMapOf(UiTextNode.PROP_TEXT, "abc"))
        node.build()
        val initialBounds = node.getBounding()

        node.update(reactMapOf(UiTextNode.PROP_TEXT, "longer text"))
        node.build() // rebuild to update bounds

        initialBounds.equalInexact(node.getBounding()) shouldBe false
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiTextNode {
        return object : UiTextNode(props, context, mock(), mock(), fontProvider) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

}