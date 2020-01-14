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
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.R
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.id
import com.magicleap.magicscript.label
import com.magicleap.magicscript.reactMapOf
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.shouldBe
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
class UiDropdownListNodeTest {

    private lateinit var context: Context
    private lateinit var providerTypeface: Typeface
    private lateinit var fontProvider: FontProvider
    private lateinit var iconsRepo: IconsRepository

    private lateinit var item1: UiDropdownListItemNode
    private lateinit var item2: UiDropdownListItemNode

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.providerTypeface = Typeface.DEFAULT_BOLD
        this.fontProvider = object : FontProvider {
            override fun provideFont(fontParams: FontParams?): Typeface {
                return providerTypeface
            }
        }
        this.iconsRepo = mock()
        val sampleIcon = context.getDrawable(R.drawable.add)
        whenever(iconsRepo.getIcon(anyString(), anyBoolean())).thenReturn(sampleIcon)

        item1 = createDropdownItem(reactMapOf().id(0).label("item 1"))
        item2 = createDropdownItem(reactMapOf().id(1).label("item 2"))
    }

    @Test
    fun `should be able to select only one item when multi select option disabled`() {
        val props = reactMapOf(UiDropdownListNode.PROP_MULTI_SELECT, false)
        val node = createNode(props)
        node.build()
        node.addContent(item1)
        node.addContent(item2)

        item1.isSelected = true
        item2.isSelected = true

        item1.isSelected shouldBe false
        item2.isSelected shouldBe true
    }

    private fun createNode(props: ReadableMap): UiDropdownListNode {
        return UiDropdownListNode(props, context, mock(), fontProvider, iconsRepo)
    }

    private fun createDropdownItem(props: JavaOnlyMap): UiDropdownListItemNode {
        return UiDropdownListItemNode(props, context, mock(), fontProvider)
    }

}