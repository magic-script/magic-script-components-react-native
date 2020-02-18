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
package com.magicleap.magicscript.scene.nodes

import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.performClick
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.base.UiDateTimePickerBaseNode
import com.magicleap.magicscript.scene.nodes.views.DialogProviderImpl
import com.magicleap.magicscript.scene.nodes.views.NotifiableTimePickerDialog
import com.magicleap.magicscript.utils.VerySimpleDateFormat
import com.nhaarman.mockitokotlin2.*
import kotlinx.android.synthetic.main.date_time_picker.view.*
import org.amshove.kluent.shouldEqual
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class UiTimePickerNodeTest {
    val timePickerDialog = mock<NotifiableTimePickerDialog>(defaultAnswer = Mockito.RETURNS_MOCKS)
    val datePickerDialogProvider = mock<DialogProviderImpl>().apply {
        whenever(provideTimePickerDialog(any(), any(), any())).doReturn(timePickerDialog)
    }
    var tested: TestableUiTimePickerNode = TestableUiTimePickerNode(datePickerDialogProvider)

    @After
    fun validate() {
        Mockito.validateMockitoUsage()
    }

    @Test
    fun `should apply default properties`() {
        tested.getProperty(UiDateTimePickerBaseNode.PROP_LABEL_SIDE) shouldEqual UiDateTimePickerBaseNode.LABEL_SIDE_TOP
        tested.getProperty(UiTimePickerNode.PROP_TIME_FORMAT) shouldEqual UiTimePickerNode.TIME_FORMAT_DEFAULT
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

    @Test
    fun `time should update hint on date text`() {
        val timeFormat = "HH:MM"
        tested.update(reactMapOf(UiTimePickerNode.PROP_TIME_FORMAT, timeFormat))

        verify(tested.dateText).hint = timeFormat
    }

    @Test
    fun `on view click should create dialog with default time`() {
        tested.update(
            reactMapOf(
                UiTimePickerNode.PROP_DEFAULT_TIME, "14:10:00",
                UiTimePickerNode.PROP_TIME_FORMAT, UiTimePickerNode.TIME_FORMAT_DEFAULT
            )
        )

        tested.performClick()

        verify(datePickerDialogProvider).provideTimePickerDialog(any(), any(), any())
    }

    @Test
    fun `if date and defaultDate is not set should apply current date in dialog`() {
        tested.update(
            reactMapOf(
                UiTimePickerNode.PROP_TIME_FORMAT,
                UiTimePickerNode.TIME_FORMAT_DEFAULT
            )
        )

        tested.performClick()
        val calendar = Calendar.getInstance()

        verify(timePickerDialog).updateTime(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
    }

    @Test
    fun `if defaultDate is set should apply it to dialog`() {
        val textTime = "14:15:00"
        tested.update(
            reactMapOf(
                UiTimePickerNode.PROP_DEFAULT_TIME, textTime,
                UiTimePickerNode.PROP_TIME_FORMAT, UiTimePickerNode.TIME_FORMAT_DEFAULT
            )
        )

        tested.performClick()

        verify(timePickerDialog).updateTime(14, 15)
    }

    @Test
    fun `if date is set should apply it to dialog`() {
        val textTime = "14:16:00"
        tested.update(
            reactMapOf(
                UiTimePickerNode.PROP_DEFAULT_TIME, "14:15:00",
                UiTimePickerNode.PROP_TIME_FORMAT, UiTimePickerNode.TIME_FORMAT_DEFAULT,
                UiTimePickerNode.PROP_TIME, textTime
            )
        )

        tested.performClick()

        verify(timePickerDialog).updateTime(14, 16)
    }

    @Test
    fun `should update timeValue when date set`() {
        tested.update(
            reactMapOf(
                UiTimePickerNode.PROP_DEFAULT_TIME, "14:15:00",
                UiTimePickerNode.PROP_TIME_FORMAT, UiTimePickerNode.TIME_FORMAT_DEFAULT
            )
        )

        tested.performClick()
        tested.onTimeChanged = mock()
        tested.provideDialogOnTimeSetListener().onTimeSet(mock(), 15, 16)
        val dateFormat =
            VerySimpleDateFormat(
                UiTimePickerNode.TIME_FORMAT_DEFAULT,
                Locale.getDefault()
            )

        verify(tested.dateText).setText(eq(dateFormat.format(15, 16)), any())
    }

    class TestableUiTimePickerNode(
        datePickerDialogProvider: DialogProviderImpl
    ) : UiTimePickerNode(
        JavaOnlyMap(),
        ApplicationProvider.getApplicationContext(),
        mock(),
        mock(),
        datePickerDialogProvider
    ) {

        val titleText: TextView = mock()
        val dateText: EditText = mock()
        val mainView = mock<LinearLayout>().also {
            this.view = it
            whenever(it.title).doReturn(titleText)
            whenever(it.value).doReturn(dateText)
        }

        override fun provideView(context: Context): View {
            return mainView
        }

        fun provideDialogOnTimeSetListener(): TimePickerDialog.OnTimeSetListener {
            return onTimeSetListener
        }

        override fun provideActivityContext() = context
    }
}