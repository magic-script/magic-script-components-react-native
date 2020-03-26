/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes.views

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomSliderTest {

    private lateinit var context: Context
    private lateinit var slider: CustomSlider

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.slider = spy(CustomSlider(context))
    }

    @Test
    fun `should redraw after setting value`() {
        slider.value = 0.2F

        verify(slider).invalidate()
    }

    @Test
    fun `should redraw after setting min`() {
        slider.min = 2F

        verify(slider).invalidate()
    }

    @Test
    fun `should redraw after setting max`() {
        slider.max = 4F

        verify(slider).invalidate()
    }


    @Test
    fun `value should be coerced to min if lower than min`() {
        slider.min = 5F
        slider.max = 10F

        slider.value = 1f

        slider.value shouldEqual 5f
    }

    @Test
    fun `value should be coerced to max if bigger than max`() {
        slider.min = 5F
        slider.max = 10F

        slider.value = 12f

        slider.value shouldEqual 10f
    }

    @Test
    fun `should be possible to assign value before min and max`() {
        slider.value = 7f
        slider.min = 5F
        slider.max = 10F

        slider.value shouldEqual 7f
    }

    @Test
    fun `should be possible to assign min before max`() {
        slider.min = 5F
        slider.max = 10F

        slider.min shouldEqual 5f
        slider.max shouldEqual 10f
    }

    @Test
    fun `should be possible to assign max before min`() {
        slider.max = 10F
        slider.min = 5F

        slider.min shouldEqual 5f
        slider.max shouldEqual 10f
    }

    @Test
    fun `min should be coerced to max value if bigger than max`() {
        slider.max = 10F
        slider.min = 50F

        slider.min shouldEqual 10f
    }

    @Test
    fun `max should be coerced to min value if lower than min`() {
        slider.min = 50F
        slider.max = 10F

        slider.max shouldEqual 50f
    }

}