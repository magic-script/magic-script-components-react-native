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

package com.magicleap.magicscript.utils

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.DatePicker
import com.google.ar.sceneform.math.Vector3
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

        val dec_31_2000 = Calendar.getInstance().apply {
            timeInMillis = 0
            set(Calendar.MONTH, getActualMaximum(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.YEAR, 2000)
        }.timeInMillis
        val dec_31_2030 = Calendar.getInstance().apply {
            timeInMillis = 0
            set(Calendar.MONTH, getActualMaximum(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.YEAR, 2030)
        }.timeInMillis

        tested.updateMinMaxYear(2000, 2030)
        verify(datePicker).minDate = dec_31_2000
        verify(datePicker).maxDate = dec_31_2030

    }

    @Test
    fun `should return hour from Date`() {
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
        }.time

        date.getHour() shouldEqual 14
    }

    @Test
    fun `should return minutes from Date`() {
        val date = Calendar.getInstance().apply {
            set(Calendar.MINUTE, 55)
        }.time

        date.getMinute() shouldEqual 55
    }

    @Test
    fun `containsAny should return true if bundle contain any passed key`() {
        val bundle = Bundle().apply {
            putDouble("width", 1.0)
            putString("alignment", "top-left")
        }

        bundle.containsAny("alignment", "some") shouldEqual true
    }

    @Test
    fun `containsAny should return false if bundle does not contain any passed key`() {
        val bundle = Bundle().apply {
            putDouble("abcdef", 0.1)
            putString("prop2", "some value")
        }

        bundle.containsAny("abc", "some") shouldEqual false
    }

    @Test
    fun `should calculate sum of floats`() {
        val numbers = listOf(1.2f, 2.8f, 3f)

        val sum = numbers.sumByFloat { it }

        sum shouldEqual 7f
    }

    @Test
    fun `sum by float should return 0 for empty list`() {
        val numbers = listOf<Float>()

        val sum = numbers.sumByFloat { it }

        sum shouldEqual 0f
    }

    @Test
    fun `should add Vector3`() {
        val vector1 = Vector3(1f, 2f, 3f)
        val vector2 = Vector3(2f, 3f, 4f)

        vector1 + vector2 shouldEqual Vector3.add(vector1, vector2)
    }

    @Test
    fun `should substract Vector3`() {
        val vector1 = Vector3(1f, 2f, 3f)
        val vector2 = Vector3(2f, 3f, 4f)

        vector1 - vector2 shouldEqual Vector3.subtract(vector1, vector2)
    }

    @Test
    fun `should div Vector3`() {
        val vector1 = Vector3(1f, 2f, 3f)
        val scale = 2f

        vector1 / scale shouldEqual Vector3(0.5f, 1f, 1.5f)
    }

    @Test
    fun `should put default Boolean`() {
        val bundle = Bundle()

        bundle.putDefault("key", true)

        bundle.getBoolean("key") shouldEqual true
    }


    @Test
    fun `put default Boolean shouldn't override value`() {
        val bundle = Bundle().apply {
            putBoolean("key", true)
        }

        bundle.putDefault("key", false)

        bundle.getBoolean("key") shouldEqual true
    }

    @Test
    fun `should put default Double`() {
        val bundle = Bundle()

        bundle.putDefault("key", 1.0)

        bundle.getDouble("key") shouldEqual 1.0
    }


    @Test
    fun `put default Double shouldn't override value`() {
        val bundle = Bundle().apply {
            putDouble("key", 1.0)
        }

        bundle.putDefault("key", 2.0)

        bundle.getDouble("key") shouldEqual 1.0
    }

    @Test
    fun `should put default String`() {
        val bundle = Bundle()

        bundle.putDefault("key", "test")

        bundle.getString("key") shouldEqual "test"
    }


    @Test
    fun `put default String shouldn't override value`() {
        val bundle = Bundle().apply {
            putString("key", "test")
        }

        bundle.putDefault("key", "test2")

        bundle.getString("key") shouldEqual "test"
    }

    @Test
    fun `should put default Serializable`() {
        val bundle = Bundle()

        bundle.putDefault("key", arrayListOf(1.0, 2.0))

        bundle.getSerializable("key") as ArrayList<Double> shouldEqual arrayListOf(1.0, 2.0)
    }


    @Test
    fun `put default Serializable shouldn't override value`() {
        val bundle = Bundle().apply {
            putSerializable("key", arrayListOf(1.0, 2.0))
        }

        bundle.putDefault("key", arrayListOf(3.0, 4.0))

        bundle.getSerializable("key") as ArrayList<Double> shouldEqual arrayListOf(1.0, 2.0)
    }

    @Test
    fun `limited function should limit charters and add dots`() {
        val tested = "ABCDEFG"
        tested.limited(3) shouldEqual "ABC…"
    }

    @Test
    fun `containsAll should return false if any argument is missing`() {
        val bundle = Bundle().apply {
            putString("key", "test")
            putString("key2", "test")
            putString("key3", "test")
        }

        bundle.containsAll("key", "key2", "key3", "key4") shouldEqual false
    }

    @Test
    fun `containsAll should return true if contains all args`() {
        val bundle = Bundle().apply {
            putString("key", "test")
            putString("key2", "test")
            putString("key3", "test")
        }

        bundle.containsAll("key", "key2") shouldEqual true
    }

}
