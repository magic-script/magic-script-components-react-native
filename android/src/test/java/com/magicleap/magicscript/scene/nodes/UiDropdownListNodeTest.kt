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
import com.magicleap.magicscript.*
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.IconsRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
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

    private lateinit var tested: UiDropdownListNode
    private lateinit var item1: UiDropdownListItemNode
    private lateinit var item2: UiDropdownListItemNode

    private lateinit var context: Context
    private lateinit var providerTypeface: Typeface
    private lateinit var fontProvider: FontProvider
    private lateinit var iconsRepo: IconsRepository

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

        item1 = buildDropdownItem(reactMapOf().id(0).label("item 1"))
        item2 = buildDropdownItem(reactMapOf().id(1).label("item 2"))

        tested = UiDropdownListNode(JavaOnlyMap(), context, mock(), fontProvider, iconsRepo)
        tested.build()
        tested.addContent(item1)
        tested.addContent(item2)
    }


    @Test
    fun `should notify listener about all selected items when multi select active`() {
        tested.update(reactMapOf(UiDropdownListNode.PROP_MULTI_SELECT, true))
        val item3 = buildDropdownItem(
            reactMapOf().id(312).label("item 3").selected(true)
        )
        tested.addContent(item3)
        var selectedItemsList = listOf<UiDropdownListItemNode>()
        tested.onSelectionChangedListener = { selectedItems ->
            selectedItemsList = selectedItems
        }

        item2.select()

        selectedItemsList.size shouldEqual 2
        selectedItemsList shouldContain item2
        selectedItemsList shouldContain item3
    }

    @Test
    fun `should select only the last clicked item when multi select disabled`() {
        tested.update(reactMapOf(UiDropdownListNode.PROP_MULTI_SELECT, false))

        item1.select()
        item2.select()

        item1.isSelected shouldBe false
        item2.isSelected shouldBe true
    }

    @Test
    fun `should be able to select multiple items when multi select enabled`() {
        tested.update(reactMapOf(UiDropdownListNode.PROP_MULTI_SELECT, true))

        item1.select()
        item2.select()

        item1.isSelected shouldBe true
        item2.isSelected shouldBe true
    }

    @Test
    fun `should hide dropdown list when item selected if multi select disabled`() {
        val props = reactMapOf(
            UiDropdownListNode.PROP_MULTI_SELECT, false,
            UiDropdownListNode.PROP_SHOW_LIST, true
        )
        tested.update(props)
        var listVisible = true
        tested.onListVisibilityChanged = { isVisible ->
            listVisible = isVisible
        }

        item1.select()

        listVisible shouldBe false
    }

    @Test
    fun `should not hide dropdown list when item selected if multi select enabled`() {
        val props = reactMapOf(
            UiDropdownListNode.PROP_MULTI_SELECT, true,
            UiDropdownListNode.PROP_SHOW_LIST, true
        )
        tested.update(props)
        var listVisible = true
        tested.onListVisibilityChanged = { isVisible ->
            listVisible = isVisible
        }

        item1.select()

        listVisible shouldBe true
    }

    private fun buildDropdownItem(props: JavaOnlyMap): UiDropdownListItemNode {
        val item = UiDropdownListItemNode(props, context, mock(), fontProvider)
        item.build()
        return item
    }

}