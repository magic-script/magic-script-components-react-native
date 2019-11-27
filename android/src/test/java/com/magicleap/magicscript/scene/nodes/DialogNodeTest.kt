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
import android.graphics.drawable.Drawable
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.views.CustomAlertDialogBuilder
import com.magicleap.magicscript.utils.DialogProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DialogNodeTest {

    private lateinit var iconsRepository: IconsRepository
    private lateinit var testDialogProvider: DialogProvider
    private lateinit var context: Context
    private lateinit var dummyDrawable: Drawable
    private lateinit var mockDialogBuilder: CustomAlertDialogBuilder

    @Before
    fun setUp() {
        dummyDrawable = mock()
        mockDialogBuilder = mock()
        iconsRepository = mock()
        testDialogProvider = mock()
        context = ApplicationProvider.getApplicationContext()
        whenever(testDialogProvider.provideCustomAlertDialogBuilder(any())).thenReturn(mockDialogBuilder)
    }

    @Test
    fun `should set confirm icon and text if props are passed`() {
        val props = JavaOnlyMap.of(
                DialogNode.PROP_CONFIRM_ICON, "icon",
                DialogNode.PROP_CONFIRM_TEXT, "text"
        )
        val node = DialogNode(props, context, iconsRepository, testDialogProvider)
        whenever(iconsRepository.getIcon("icon", false)).thenReturn(dummyDrawable)

        node.build()

        verify(mockDialogBuilder).setConfirmationText("text")
        verify(iconsRepository).getIcon("icon", false)
        verify(mockDialogBuilder).setConfirmationIcon(dummyDrawable)
    }

    @Test
    fun `should set cancel icon, text and listener if props are passed`() {
        val props = JavaOnlyMap.of(
                DialogNode.PROP_CANCEL_ICON, "icon",
                DialogNode.PROP_CANCEL_TEXT, "text"
        )
        val node = DialogNode(props, context, iconsRepository, testDialogProvider)
        whenever(iconsRepository.getIcon("icon", false)).thenReturn(dummyDrawable)

        node.build()

        verify(mockDialogBuilder).setCancelText("text")
        verify(iconsRepository).getIcon("icon", false)
        verify(mockDialogBuilder).setCancelIcon(dummyDrawable)
    }

    @Test
    fun `should set title and message if props are passed`() {
        val props = JavaOnlyMap.of(
                DialogNode.PROP_TITLE, "title",
                DialogNode.PROP_TEXT, "text"
        )
        val node = DialogNode(props, context, iconsRepository, testDialogProvider)

        node.build()

        verify(mockDialogBuilder).setTitle("title")
        verify(mockDialogBuilder).setDescription("text")
    }
}