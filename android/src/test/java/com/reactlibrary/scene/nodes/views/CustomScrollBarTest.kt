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
import android.graphics.Canvas
import android.view.MotionEvent
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
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
    fun onDrawShouldCallDrawRoundRect() {
        val onDraw = scrollBar.javaClass.getDeclaredMethod("onDraw", Canvas::class.java)
        val canvas: Canvas = mock()
        onDraw.isAccessible = true
        onDraw.invoke(scrollBar, canvas)
        verify(canvas, times(2)).drawRoundRect(any(), any(), any(), any())
    }

    @Test
    fun touchCallbackShouldSetThumbPosition() {
        val event: MotionEvent = mock()
        whenever(event.getActionMasked()).thenReturn(MotionEvent.ACTION_DOWN)
        scrollBar.touchCallback(event)
        verify(scrollBar).thumbPosition = any()
    }

    @Test
    fun shouldRedrawAfterSettingThumbPosition() {
        scrollBar.thumbPosition = 99F
        verify(scrollBar).invalidate()
    }

    @Test
    fun shouldRedrawAfterSettingThumbSize() {
        scrollBar.thumbSize = 99F
        verify(scrollBar).invalidate()
    }

}