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
package com.magicleap.magicscript.scene.nodes.base

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.views.DialogProvider
import com.magicleap.magicscript.scene.nodes.views.DialogProviderImpl
import com.nhaarman.mockitokotlin2.*
import kotlinx.android.synthetic.main.date_time_picker.view.*
import org.amshove.kluent.shouldEqual
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UiDateTimePickerBaseNodeTest {
    private val datePicker: DatePicker = mock()
    private val datePickerDialog =
        mock<DatePickerDialog>(defaultAnswer = Mockito.RETURNS_MOCKS).also {
            whenever(it.datePicker).thenReturn(datePicker)
        }
    private val datePickerDialogProvider = mock<DialogProvider>().apply {
        whenever(provideDatePickerDialog()).doReturn(datePickerDialog)
    }
    var tested: TestableUiDateTimePickerBaseNode = TestableUiDateTimePickerBaseNode()

    @After
    fun validate() {
        Mockito.validateMockitoUsage()
    }

    @Test
    fun `should apply default properties`() {
        tested.getProperty(UiDateTimePickerBaseNode.PROP_LABEL_SIDE) shouldEqual UiDateTimePickerBaseNode.LABEL_SIDE_TOP
    }

    @Test
    fun `should apply label text`() {
        val label = "test test test"
        tested.update(reactMapOf(UiDateTimePickerBaseNode.PROP_LABEL, label))

        verify(tested.titleText).text = label
    }

    @Test
    fun `should set vertical orientation when label side is top`() {
        tested.update(
            reactMapOf(
                UiDateTimePickerBaseNode.PROP_LABEL_SIDE,
                UiDateTimePickerBaseNode.LABEL_SIDE_TOP
            )
        )

        verify(tested.mainView).orientation = LinearLayout.VERTICAL
    }

    @Test
    fun `should set horizontal orientation when label side is left`() {
        tested.update(
            reactMapOf(
                UiDateTimePickerBaseNode.PROP_LABEL_SIDE,
                UiDateTimePickerBaseNode.LABEL_SIDE_LEFT
            )
        )

        verify(tested.mainView).orientation = LinearLayout.HORIZONTAL
    }

    class TestableUiDateTimePickerBaseNode() : UiDateTimePickerBaseNode(
        JavaOnlyMap(),
        ApplicationProvider.getApplicationContext(),
        mock(),
        mock()
    ) {

        val titleText: TextView = mock()
        val mainView = mock<LinearLayout>().also {
            this.view = it
            whenever(it.title).doReturn(titleText)
        }

        override fun provideView(context: Context): View {
            return mainView
        }
    }
}