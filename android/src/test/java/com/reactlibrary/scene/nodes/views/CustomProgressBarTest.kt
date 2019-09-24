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

package com.reactlibrary.scene.nodes.views

import android.content.Context
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomProgressBarTest {

    private lateinit var context: Context
    private lateinit var progressBar: CustomProgressBar

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.progressBar = spy(CustomProgressBar(context))
    }

    @Test
    fun shouldRedrawAfterSettingValue() {
        progressBar.value = 99F

        verify(progressBar).invalidate()
    }

    @Test
    fun shouldRedrawAfterSettingMinValue() {
        progressBar.min = 0F

        verify(progressBar).invalidate()
    }

    @Test
    fun shouldRedrawAfterSettingMaxValue() {
        progressBar.max = 100F

        verify(progressBar).invalidate()
    }

    @Test
    fun shouldRedrawAfterSettingBeginColor() {
        progressBar.beginColor = Color.RED

        verify(progressBar).invalidate()
    }

    @Test
    fun shouldRedrawAfterSettingEndColor() {
        progressBar.endColor = Color.RED

        verify(progressBar).invalidate()
    }

}