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

package com.reactlibrary.utils

import android.app.DatePickerDialog
import android.graphics.Color
import android.widget.DatePicker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class ExtensionsKtTest {

    @Test
    fun `should return array of double colors R,G,B,A`() {
        val color = Color.parseColor("#BF9CACBC")

        color.toJsColorArray().run {
            size shouldEqual 4
            get(0) shouldEqual Color.red(color).toDouble() / 255
            get(1) shouldEqual Color.green(color).toDouble() / 255
            get(2) shouldEqual Color.blue(color).toDouble() / 255
            get(3) shouldEqual Color.alpha(color).toDouble() / 255
        }
    }

    @Test
    fun `DatePickerDialog should update date from Date`() {
        val tested = mock<DatePickerDialog>(defaultAnswer = Mockito.RETURNS_MOCKS)

        val nov8_2019 = Date(1573205438928)

        tested.updateDate(nov8_2019)
        verify(tested).updateDate(2019, Calendar.NOVEMBER, 8)
    }

    @Test
    fun `DatePickerDialog should update min and max year`() {
        val datePicker: DatePicker = mock()
        val tested = mock<DatePickerDialog>(defaultAnswer = Mockito.RETURNS_MOCKS).also {
            whenever(it.datePicker).thenReturn(datePicker)
        }

        val dec_31_2000 = 978220800000L
        val dec_31_2030 = 1924905600000L

        tested.updateMinMaxYear(2000, 2030)
        verify(datePicker).minDate = dec_31_2000
        verify(datePicker).maxDate = dec_31_2030

    }
}
