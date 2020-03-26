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

package com.magicleap.magicscript.scene.nodes.dropdown

import android.content.Context
import android.graphics.Typeface
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.*
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.font.FontStyle
import com.magicleap.magicscript.font.FontWeight
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.views.CustomButton
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.itReturns
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBe
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
    private lateinit var viewSpy: CustomButton
    private lateinit var context: Context

    private val listNode: DropdownItemsListNode?
        get() = tested.contentNode.children
            .filterIsInstance<DropdownItemsListNode>()
            .firstOrNull()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        fontProvider = object : FontProvider {
            override fun provideFont(fontStyle: FontStyle?, fontWeight: FontWeight?): Typeface {
                return Typeface.DEFAULT_BOLD
            }
        }
        iconsRepo = mock {
            on { getIcon(anyString(), anyBoolean()) } itReturns context.getDrawable(R.drawable.add)
        }

        item1 = buildDropdownItem("0")
        item2 = buildDropdownItem("1")

        viewSpy = spy(CustomButton(context))
        tested = createNodeWithViewSpy(JavaOnlyMap())
        tested.build()
        tested.addContent(item1)
        tested.addContent(item2)
    }

    @Test
    fun `should not use additional characters spacing`() {
        verify(viewSpy).setCharactersSpacing(0F)
    }

    @Test
    fun `should not use border`() {
        verify(viewSpy).borderEnabled = false
    }

    @Test
    fun `items list should be hidden by default`() {
        listNode?.isVisible shouldBe false
    }

    @Test
    fun `should display items list on click`() {
        tested.performClick()

        listNode?.isVisible shouldBe true
    }

    @Test
    fun `should not notify listener when selection changed by updating property`() {
        var notified = false
        tested.onSelectionChangedListener = { selectedItems ->
            notified = true
        }

        item1.update(reactMapOf().selected(true))

        notified shouldBe false
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

    @Test
    fun `should move content node to top when list becomes visible`() {
        val props = reactMapOf(UiDropdownListNode.PROP_SHOW_LIST, true)

        tested.update(props)

        tested.contentNode.localPosition.z shouldEqual UiDropdownListNode.Z_OFFSET_WHEN_EXPANDED
    }

    @Test
    fun `should move content node back to 0 in z-axis when list becomes hidden`() {
        val propsVisible = reactMapOf(UiDropdownListNode.PROP_SHOW_LIST, true)
        tested.update(propsVisible)

        val propsHidden = reactMapOf(UiDropdownListNode.PROP_SHOW_LIST, false)
        tested.update(propsHidden)

        tested.contentNode.localPosition.z shouldEqual 0f
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiDropdownListNode {
        return object :
            UiDropdownListNode(props, context, mock(), mock(), fontProvider, iconsRepo) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

    private fun buildDropdownItem(id: String, selected: Boolean = false): UiDropdownListItemNode {
        val props = reactMapOf().id(id).label("item $id").selected(selected)
        val item = UiDropdownListItemNode(props, fontProvider)
        item.build()
        return item
    }

}