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

package com.magicleap.magicscript.scene.nodes.views

import android.content.Context
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.magicleap.magicscript.R
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomButtonTest {

    private lateinit var context: Context
    private lateinit var button: CustomButton

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.button = spy(CustomButton(context))
    }

    @Test
    fun `should measure and redraw after setting text`() {
        button.text = "abc"

        verify(button).invalidate()
        verify(button).requestLayout()
    }

    @Test
    fun `should measure and redraw after setting text size`() {
        button.setTextSize(18F)

        verify(button).invalidate()
        verify(button).requestLayout()
    }

    @Test
    fun `should measure and redraw after setting text padding`() {
        button.setTextPadding(10, 10)

        verify(button).invalidate()
        verify(button).requestLayout()
    }

    @Test
    fun `should measure and redraw after setting icon`() {
        val icon = context.getDrawable(R.drawable.arrow_down)

        button.setIcon(icon)

        verify(button).invalidate()
        verify(button).requestLayout()
    }

    @Test
    fun `should measure and redraw after disabling border`() {
        button.borderEnabled = false

        verify(button).invalidate()
        verify(button).requestLayout()
    }

    @Test
    fun `border should be enabled by default`() {
        button.borderEnabled shouldBe true
    }

    @Test
    fun `should redraw after setting roundness factor`() {
        button.roundnessFactor = 0.5f

        verify(button).invalidate()
    }

    @Test
    fun `should redraw after setting text color`() {
        button.setTextColor(Color.GREEN)

        verify(button).invalidate()
    }

}