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

package com.magicleap.magicscript.scene.nodes.toggle

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.magicleap.magicscript.R
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.font.FontStyle
import com.magicleap.magicscript.font.FontWeight
import com.magicleap.magicscript.icons.ToggleIconsProviderImpl
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LinearToggleViewManagerTest {

    private lateinit var context: Context
    private lateinit var toggleViewManager: LinearToggleViewManager
    private lateinit var fontProvider: FontProvider
    private lateinit var providerTypeface: Typeface
    private lateinit var textViewSpy: TextView
    private lateinit var imageViewSpy: ImageView
    private lateinit var toggleConfig: ToggleConfig

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        textViewSpy = mock()
        imageViewSpy = mock()

        providerTypeface = Typeface.DEFAULT_BOLD
        fontProvider = object : FontProvider {
            override fun provideFont(fontStyle: FontStyle?, fontWeight: FontWeight?): Typeface {
                return providerTypeface
            }
        }
        toggleViewManager = LinearToggleViewManager(fontProvider, ToggleIconsProviderImpl())

        toggleConfig = ToggleConfig(
            toggleType = UiToggleNode.TYPE_DEFAULT,
            toggleHeight = 0.1F,
            container = mock<LinearLayout>(),
            imageView = imageViewSpy,
            textView = textViewSpy,
            onToggleClickListener = {}
        )

        toggleViewManager.setupToggleView(context, toggleConfig)
    }


    @Test
    fun `text view should use typeface from provider`() {
        verify(textViewSpy).typeface = providerTypeface
    }

    @Test
    fun `text view should have letters spacing`() {
        verify(textViewSpy).letterSpacing = LinearToggleViewManager.CHARACTERS_SPACING
    }

    @Test
    fun `should set text size`() {
        val textSize = 20

        toggleViewManager.setTextSize(textSize)

        verify(textViewSpy).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    @Test
    fun `should set toggle text`() {
        val text = "QWERTY"

        toggleViewManager.setText(text)

        verify(textViewSpy).text = text
    }

    @Test
    fun `should set text color`() {
        val color = 0xFFFFFFFF.toInt()

        toggleViewManager.setTextColor(color)

        verify(textViewSpy).setTextColor(color)
    }

    @Test
    fun `should set checkbox icon when type is checkbox`() {
        val config = toggleConfig.copy(toggleType = UiToggleNode.TYPE_CHECKBOX)
        toggleViewManager.setupToggleView(context, config)

        verify(imageViewSpy).setImageResource(R.drawable.checkbox_off)
    }

    @Test
    fun `should set checked radio icon when type is radio and checked`() {
        val config = toggleConfig.copy(toggleType = UiToggleNode.TYPE_RADIO)
        toggleViewManager.setupToggleView(context, config)
        toggleViewManager.setActive(true)

        verify(imageViewSpy).setImageResource(R.drawable.radio_on)
    }

}