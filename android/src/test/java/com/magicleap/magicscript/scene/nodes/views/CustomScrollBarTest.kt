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
import androidx.test.core.app.ApplicationProvider
import com.magicleap.magicscript.createActionDownEvent
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomScrollBarTest {

    private lateinit var context: Context
    private lateinit var scrollBar: CustomScrollBar

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.scrollBar = spy(CustomScrollBar(context))
    }

    @Test
    fun `touch should not change thumb position by default`() {
        scrollBar.onTouchEvent(scrollBar.createActionDownEvent())

        verify(scrollBar, never()).thumbPosition = any()
    }

    @Test
    fun `touch should set thumb position when interactive`() {
        scrollBar.interactive = true
        scrollBar.onTouchEvent(scrollBar.createActionDownEvent())

        verify(scrollBar).thumbPosition = any()
    }

    @Test
    fun `should redraw after setting thumb position`() {
        scrollBar.thumbPosition = 99F

        verify(scrollBar).invalidate()
    }

    @Test
    fun `should redraw after setting thumb size`() {
        scrollBar.thumbSize = 99F

        verify(scrollBar).invalidate()
    }

}
