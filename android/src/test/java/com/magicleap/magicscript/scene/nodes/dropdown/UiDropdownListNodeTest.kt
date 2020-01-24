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
import com.magicleap.magicscript.*
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.IconsRepository
import com.nhaarman.mockitokotlin2.mock
import org.amshove.kluent.*
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

    private lateinit var fontProvider: FontProvider
    private lateinit var iconsRepo: IconsRepository

    private val listNode: DropdownItemsListNode?
        get() = tested.contentNode.children
            .filterIsInstance<DropdownItemsListNode>()
            .firstOrNull()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        fontProvider = object : FontProvider {
            override fun provideFont(fontParams: FontParams?): Typeface {
                return Typeface.DEFAULT_BOLD
            }
        }
        iconsRepo = mock {
            on { getIcon(anyString(), anyBoolean()) } itReturns context.getDrawable(R.drawable.add)
        }

        item1 = buildDropdownItem("0")
        item2 = buildDropdownItem("1")

        tested = UiDropdownListNode(JavaOnlyMap(), context, mock(), fontProvider, iconsRepo)
        tested.build()
        tested.addContent(item1)
        tested.addContent(item2)
    }


    @Test
    fun `should notify listener about all selected items when multi select active`() {
        tested.update(reactMapOf(UiDropdownListNode.PROP_MULTI_SELECT, true))
        val item3 = buildDropdownItem("312", selected = true)
        tested.addContent(item3)
        var selectedItemsList = listOf<UiDropdownListItemNode>()
        tested.onSelectionChangedListener = { selectedItems ->
            selectedItemsList = selectedItems
        }

        item2.update(reactMapOf().selected(true))

        selectedItemsList.size shouldEqual 2
        selectedItemsList shouldContain item2
        selectedItemsList shouldContain item3
    }

    @Test
    fun `should select only the last clicked item when multi select disabled`() {
        tested.update(reactMapOf(UiDropdownListNode.PROP_MULTI_SELECT, false))

        item1.update(reactMapOf().selected(true))
        item2.update(reactMapOf().selected(true))

        item1.selected shouldBe false
        item2.selected shouldBe true
    }

    @Test
    fun `should be able to select multiple items when multi select enabled`() {
        tested.update(reactMapOf(UiDropdownListNode.PROP_MULTI_SELECT, true))

        item1.update(reactMapOf().selected(true))
        item2.update(reactMapOf().selected(true))

        item1.selected shouldBe true
        item2.selected shouldBe true
    }

    @Test
    fun `should hide dropdown list when item selected if multi select option disabled`() {
        val props = reactMapOf(
            UiDropdownListNode.PROP_MULTI_SELECT, false,
            UiDropdownListNode.PROP_SHOW_LIST, true
        )
        tested.update(props)

        item1.update(reactMapOf().selected(true))

        listNode shouldNotBe null
        listNode?.isVisible shouldBe false
    }

    @Test
    fun `should not hide dropdown list when item selected if multi select enabled`() {
        val props = reactMapOf(
            UiDropdownListNode.PROP_MULTI_SELECT, true,
            UiDropdownListNode.PROP_SHOW_LIST, true
        )
        tested.update(props)

        item1.update(reactMapOf().selected(true))

        listNode shouldNotBe null
        listNode?.isVisible shouldBe true
    }

    private fun buildDropdownItem(id: String, selected: Boolean = false): UiDropdownListItemNode {
        val props = reactMapOf().id(id).label("item $id").selected(selected)
        val item = UiDropdownListItemNode(props, fontProvider)
        item.build()
        return item
    }


}