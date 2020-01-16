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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.font.FontWeight
import com.magicleap.magicscript.id
import com.magicleap.magicscript.label
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.dropdown.UiDropdownListItemNode
import com.magicleap.magicscript.selected
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiDropdownListItemNodeTest {
    private lateinit var tested: TestableDropdownItem

    private lateinit var context: Context
    private lateinit var normalTypeface: Typeface
    private lateinit var extraBoldTypeface: Typeface
    private lateinit var fontProvider: FontProvider
    private lateinit var viewSpy: TextView

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        normalTypeface = Typeface.DEFAULT
        extraBoldTypeface = Typeface.DEFAULT_BOLD
        viewSpy = spy(TextView(context))
        fontProvider = spy(object : FontProvider {
            override fun provideFont(fontParams: FontParams?): Typeface {
                return if (fontParams?.weight == FontWeight.EXTRA_BOLD) {
                    extraBoldTypeface
                } else {
                    normalTypeface
                }
            }
        })
        val props = reactMapOf().id(0).label("magic item")
        tested = TestableDropdownItem(props, context, mock(), fontProvider)
        tested.build()
    }

    @Test
    fun `should not be selected by default`() {
        tested.isSelected shouldEqual false
    }

    @Test
    fun `should select after click when not selected`() {
        tested.performClick()

        tested.isSelected shouldBe true
    }

    @Test
    fun `should deselect after click when selected`() {
        tested.update(reactMapOf().selected(true))

        tested.performClick()

        tested.isSelected shouldBe false
    }

    @Test
    fun `should use extra bold font for selected item`() {
        Mockito.reset(viewSpy)

        tested.select()

        verify(viewSpy).typeface = Typeface.DEFAULT_BOLD
    }

    inner class TestableDropdownItem(
        initProps: ReadableMap,
        context: Context,
        viewRenderableLoader: ViewRenderableLoader,
        fontProvider: FontProvider
    ) : UiDropdownListItemNode(initProps, context, viewRenderableLoader, fontProvider) {

        override fun provideView(context: Context): View {
            return viewSpy
        }

        fun performClick() {
            onViewClick()
        }
    }

}