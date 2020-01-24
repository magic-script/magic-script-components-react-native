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

package com.magicleap.magicscript.scene.nodes.dropdown

import android.content.Context
import android.graphics.Typeface
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.font.FontWeight
import com.magicleap.magicscript.id
import com.magicleap.magicscript.label
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.selected
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiDropdownListItemNodeTest {

    private lateinit var tested: UiDropdownListItemNode

    private lateinit var context: Context
    private lateinit var normalTypeface: Typeface
    private lateinit var extraBoldTypeface: Typeface
    private lateinit var fontProvider: FontProvider

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        normalTypeface = mock()
        extraBoldTypeface = mock()
        fontProvider = spy(object : FontProvider {
            override fun provideFont(fontParams: FontParams?): Typeface {
                return if (fontParams?.weight == FontWeight.EXTRA_BOLD) {
                    extraBoldTypeface
                } else {
                    normalTypeface
                }
            }
        })
        tested = UiDropdownListItemNode(reactMapOf(), fontProvider)
        tested.build()
    }

    @Test
    fun `should not be selected by default`() {
        tested.selected shouldEqual false
    }

    @Test
    fun `number of characters should not be limited by default`() {
        tested.maxCharacters shouldEqual UiDropdownListItemNode.MAX_CHARACTERS_UNLIMITED
    }

    @Test
    fun `id should not be 0 when not specified`() {
        tested.id shouldEqual 0
    }

    @Test
    fun `should apply id property`() {
        tested.update(reactMapOf().id("19"))

        tested.id shouldEqual 19
    }

    @Test
    fun `should apply label property`() {
        tested.update(reactMapOf().label("option 1"))

        tested.label shouldEqual "option 1"
    }

    @Test
    fun `should use extra bold typeface when selected`() {
        tested.selected = true

        tested.typeface shouldEqual extraBoldTypeface
    }

    @Test
    fun `should use normal typeface when not selected`() {
        tested.selected = false

        tested.typeface shouldEqual normalTypeface
    }

    @Test
    fun `should notify listener when updated the selected property`() {
        var selectRequested = false
        tested.onSelectionChangeRequest = { select ->
            selectRequested = select
        }

        tested.update(reactMapOf().selected(true))

        selectRequested shouldBe true
    }

}