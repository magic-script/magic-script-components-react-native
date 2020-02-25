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

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.performClick
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.UiDatePickerNode.Companion.DATE_FORMAT_DEFAULT
import com.magicleap.magicscript.scene.nodes.UiDatePickerNode.Companion.PROP_DATE
import com.magicleap.magicscript.scene.nodes.UiDatePickerNode.Companion.PROP_DATE_FORMAT
import com.magicleap.magicscript.scene.nodes.UiDatePickerNode.Companion.PROP_DEFAULT_DATE
import com.magicleap.magicscript.scene.nodes.UiDatePickerNode.Companion.PROP_YEAM_MAX
import com.magicleap.magicscript.scene.nodes.UiDatePickerNode.Companion.PROP_YEAR_MIN
import com.magicleap.magicscript.scene.nodes.base.UiDateTimePickerBaseNode.Companion.LABEL_SIDE_LEFT
import com.magicleap.magicscript.scene.nodes.base.UiDateTimePickerBaseNode.Companion.LABEL_SIDE_TOP
import com.magicleap.magicscript.scene.nodes.base.UiDateTimePickerBaseNode.Companion.PROP_LABEL
import com.magicleap.magicscript.scene.nodes.base.UiDateTimePickerBaseNode.Companion.PROP_LABEL_SIDE
import com.magicleap.magicscript.scene.nodes.views.DialogProviderImpl
import com.magicleap.magicscript.utils.VerySimpleDateFormat
import com.magicleap.magicscript.utils.updateDate
import com.nhaarman.mockitokotlin2.*
import kotlinx.android.synthetic.main.date_time_picker.view.*
import org.amshove.kluent.shouldEqual
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_MOCKS
import org.mockito.Mockito.validateMockitoUsage
import org.robolectric.RobolectricTestRunner
import java.text.SimpleDateFormat
import java.util.*


@RunWith(RobolectricTestRunner::class)
class UiDatePickerNodeTest {

    private val datePicker: DatePicker = mock()
    private val datePickerDialog = mock<DatePickerDialog>(defaultAnswer = RETURNS_MOCKS).also {
        whenever(it.datePicker).thenReturn(datePicker)
    }
    private val datePickerDialogProvider = mock<DialogProviderImpl>().apply {
        whenever(provideDatePickerDialog(any())).doReturn(datePickerDialog)
    }
    var tested: TestableUiDatePickerNode = TestableUiDatePickerNode(datePickerDialogProvider)

    @After
    fun validate() {
        validateMockitoUsage()
    }

    @Test
    fun `should apply default properties`() {
        tested.getProperty(PROP_LABEL_SIDE) shouldEqual LABEL_SIDE_TOP
        tested.getProperty(PROP_YEAR_MIN) shouldEqual -1.0
        tested.getProperty(PROP_YEAM_MAX) shouldEqual -1.0
        tested.getProperty(PROP_DATE_FORMAT) shouldEqual DATE_FORMAT_DEFAULT
    }

    @Test
    fun `should apply label text`() {
        val label = "test test test"
        tested.update(reactMapOf(PROP_LABEL, label))

        verify(tested.titleText).text = label
    }

    @Test
    fun `should set vertical orientation when label side is top`() {
        tested.update(reactMapOf(PROP_LABEL_SIDE, LABEL_SIDE_TOP))

        verify(tested.mainView).orientation = LinearLayout.VERTICAL
    }

    @Test
    fun `should set horizontal orientation when label side is left`() {
        tested.update(reactMapOf(PROP_LABEL_SIDE, LABEL_SIDE_LEFT))

        verify(tested.mainView).orientation = LinearLayout.HORIZONTAL
    }

    @Test
    fun `dateFormat should update hint on date text`() {
        val dateFormat = "DD/YYYY"
        tested.update(reactMapOf(PROP_DATE_FORMAT, dateFormat))

        verify(tested.dateText).hint = dateFormat
    }

    @Test
    fun `on view click should create dialog with default date`() {
        tested.update(
            reactMapOf(
                PROP_DEFAULT_DATE, "13/12/2011",
                PROP_DATE_FORMAT, DATE_FORMAT_DEFAULT
            )
        )

        tested.performClick()

        verify(datePickerDialogProvider).provideDatePickerDialog(any())
    }

    @Test
    fun `on view click should should attach listeners to dialog`() {
        tested.update(
            reactMapOf(
                PROP_DEFAULT_DATE, "13/12/2011",
                PROP_DATE_FORMAT, DATE_FORMAT_DEFAULT
            )
        )

        tested.performClick()

        verify(datePickerDialog).setOnDateSetListener(any())
        verify(datePicker).setOnDateChangedListener(any())
    }

    @Test
    fun `if date and defaultDate is not set should apply current date in dialog`() {
        tested.update(reactMapOf(PROP_DATE_FORMAT, DATE_FORMAT_DEFAULT))

        tested.performClick()

        verify(datePickerDialog).updateDate(Date())
    }

    @Test
    fun `if defaultDate is set should apply it to dialog`() {
        val textDate = "13/12/2011"
        tested.update(
            reactMapOf(
                PROP_DEFAULT_DATE, textDate,
                PROP_DATE_FORMAT, DATE_FORMAT_DEFAULT
            )
        )

        val date = SimpleDateFormat(DATE_FORMAT_DEFAULT).parse(textDate)

        tested.performClick()

        verify(datePickerDialog).updateDate(date)
    }

    @Test
    fun `if date is set should apply it to dialog`() {
        val textDate = "10/11/2012"
        tested.update(
            reactMapOf(
                PROP_DEFAULT_DATE, "13/12/2011",
                PROP_DATE_FORMAT, DATE_FORMAT_DEFAULT,
                PROP_DATE, textDate
            )
        )

        val date = SimpleDateFormat(DATE_FORMAT_DEFAULT).parse(textDate)

        tested.performClick()

        verify(datePickerDialog).updateDate(date)
    }

    @Test
    fun `should apply min and max year`() {
        val minYear = 2010
        val maxYear = 2030
        tested.update(
            reactMapOf(
                PROP_DEFAULT_DATE, "13/12/2011",
                PROP_DATE_FORMAT, DATE_FORMAT_DEFAULT,
                PROP_YEAR_MIN, minYear,
                PROP_YEAM_MAX, maxYear
            )
        )

        tested.performClick()

        verify(datePicker).minDate = any()
        verify(datePicker).maxDate = any()
    }

    @Test
    fun `should update dateValue when date set`() {
        tested.update(
            reactMapOf(
                PROP_DEFAULT_DATE, "13/12/2011",
                PROP_DATE_FORMAT, DATE_FORMAT_DEFAULT
            )
        )

        tested.performClick()
        tested.onDateConfirmed = mock()
        tested.provideDialogOnDateSetListener().invoke(datePicker, 2019, Calendar.NOVEMBER, 12)
        val dateFormat = VerySimpleDateFormat(
            DATE_FORMAT_DEFAULT,
            Locale.getDefault()
        )

        verify(tested.dateText).setText(eq(dateFormat.format(2019, 11, 12)), any())
    }

    class TestableUiDatePickerNode(
        datePickerDialogProvider: DialogProviderImpl
    ) : UiDatePickerNode(
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

        fun provideDialogOnDateSetListener() = onDateSetListener

        override fun provideActivityContext() = context
    }
}