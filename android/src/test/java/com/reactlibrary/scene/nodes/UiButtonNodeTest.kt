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
import android.graphics.Typeface
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.nhaarman.mockitokotlin2.*
import com.reactlibrary.R
import com.reactlibrary.font.FontParams
import com.reactlibrary.font.FontProvider
import com.reactlibrary.icons.IconsRepository
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.views.CustomButton
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.Vector2
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiButtonNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: CustomButton
    private lateinit var providerTypeface: Typeface
    private lateinit var fontProvider: FontProvider
    private lateinit var iconsRepo: IconsRepository

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.viewSpy = spy(CustomButton(context))
        this.providerTypeface = Typeface.DEFAULT_BOLD
        this.fontProvider = object : FontProvider {
            override fun provideFont(fontParams: FontParams?): Typeface {
                return providerTypeface
            }
        }
        this.iconsRepo = mock()
        val sampleIcon = context.getDrawable(R.drawable.add)
        whenever(iconsRepo.getIcon(anyString(), anyBoolean())).thenReturn(sampleIcon)
    }

    @Test
    fun shouldUseTypefaceFromProvider() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        node.build()

        verify(viewSpy).setTypeface(providerTypeface)
    }

    @Test
    fun shouldHaveDefaultTextSize() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val textSize = node.getProperty(UiButtonNode.PROP_TEXT_SIZE)

        assertEquals(UiButtonNode.DEFAULT_TEXT_SIZE, textSize)
    }

    @Test
    fun shouldHaveDefaultRoundness() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val roundness = node.getProperty(UiButtonNode.PROP_ROUNDNESS)

        assertEquals(UiButtonNode.DEFAULT_ROUNDNESS, roundness)
    }

    @Test
    fun shouldApplyTextWhenTextPropertyPresent() {
        val text = "ABC"
        val props = JavaOnlyMap.of(UiButtonNode.PROP_TEXT, text)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).text = text
    }

    @Test
    fun shouldApplyTextSizeWhenTextSizePropertyPresent() {
        val textSize = 0.05F
        val props = JavaOnlyMap.of(UiButtonNode.PROP_TEXT_SIZE, textSize)
        val node = createNodeWithViewSpy(props)
        val textSizeInPixels = Utils.metersToFontPx(textSize, context).toFloat()

        node.build()

        verify(viewSpy).setTextSize(textSizeInPixels)
    }

    @Test
    fun shouldApplyTextColorWhenColorPropertyPresent() {
        val textColor = JavaOnlyArray.of(1.0, 1.0, 1.0, 1.0)
        val props = JavaOnlyMap.of(UiButtonNode.PROP_TEXT_COLOR, textColor)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).setTextColor(0xFFFFFFFF.toInt())
    }

    @Test
    fun shouldApplyRoundnessWhenRoundnessPropertyPresent() {
        val roundness = 0.2
        val props = JavaOnlyMap.of(UiButtonNode.PROP_ROUNDNESS, roundness)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).roundnessFactor = roundness.toFloat()
    }

    @Test
    fun shouldSetIconWhenIconPropertyPresent() {
        val props = JavaOnlyMap.of(UiButtonNode.PROP_ICON, "magic-icon")
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).setIcon(any())
    }

    @Test
    fun shouldSetIconColorWhenIconColorPropertyPresent() {
        val color = JavaOnlyArray.of(0.1, 0.2, 0.3, 0.4)
        val props = JavaOnlyMap.of(
                UiButtonNode.PROP_ICON, "magic-icon",
                UiButtonNode.PROP_ICON_COLOR, color
        )
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).setIconColor(any())
    }

    @Test
    fun shouldSetIconSizeWhenIconSizePropertyPresent() {
        val sizeInMeters = 0.1
        val iconSize = JavaOnlyArray.of(sizeInMeters, sizeInMeters)
        val props = JavaOnlyMap.of(
                UiButtonNode.PROP_ICON, "magic-icon",
                UiButtonNode.PROP_ICON_SIZE, iconSize
        )
        val node = createNodeWithViewSpy(props)

        node.build()

        val sizeInPixels = Utils.metersToPx(sizeInMeters.toFloat(), context).toFloat()
        verify(viewSpy).iconSize = Vector2(sizeInPixels, sizeInPixels)
    }

    @Test
    fun shouldNotChangeHardcodedAlignment() {
        val props = JavaOnlyMap.of(TransformNode.PROP_ALIGNMENT, "bottom-right")
        val node = createNodeWithViewSpy(props)

        node.build()

        assertEquals(Alignment.HorizontalAlignment.CENTER, node.horizontalAlignment)
        assertEquals(Alignment.VerticalAlignment.CENTER, node.verticalAlignment)
    }

    @Test
    fun shouldReturnCorrectBounds() {
        val props = JavaOnlyMap.of(
                TransformNode.PROP_LOCAL_POSITION, JavaOnlyArray.of(-1.0, 1.0, 0.0),
                UiButtonNode.PROP_WIDTH, 2.0, UiButtonNode.PROP_HEIGHT, 1.0
        )
        val node = createNodeWithViewSpy(props)
        val expectedBounds = Bounding(-2F, 0.5F, 0F, 1.5F)

        node.build()

        assertTrue(Bounding.equalInexact(expectedBounds, node.getBounding()))
    }

    @Test
    fun shouldReturnCorrectBoundsWhenScaled() {
        val scale = JavaOnlyArray.of(0.5, 0.5, 0.5)
        val props = JavaOnlyMap.of(
                UiButtonNode.PROP_WIDTH, 2.0, UiButtonNode.PROP_HEIGHT, 1.0,
                TransformNode.PROP_LOCAL_SCALE, scale
        )
        val node = createNodeWithViewSpy(props)
        val expectedBounds = Bounding(-0.5F, -0.25F, 0.5F, 0.25F)

        node.build()

        assertTrue(Bounding.equalInexact(expectedBounds, node.getBounding()))
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiButtonNode {
        return object : UiButtonNode(props, context, mock(), fontProvider, iconsRepo) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

}