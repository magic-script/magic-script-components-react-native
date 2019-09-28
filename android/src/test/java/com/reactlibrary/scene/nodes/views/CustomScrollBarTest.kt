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
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomScrollBarTest {

    private var mockCanvas: Canvas = mock()
    private lateinit var context: Context
    private lateinit var scrollBar: CustomScrollBar

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.scrollBar = spy(CustomScrollBar(context))
    }

    @Test
    fun shouldRedrawAfterSettingValue() {

        val onDraw = scrollBar.javaClass.getDeclaredMethod("onDraw", Canvas::class.java)
        onDraw.isAccessible = true
        val result = onDraw.invoke(scrollBar, mockCanvas)
        // verify(saveAccountInteractor).save(account)
        // System.err.println(result)


        // scrollBar.value = 99F

        // @NonNull RectF rect, float rx, float ry, @NonNull Paint paint

        verify(mockCanvas, times(2)).drawRoundRect(any<RectF>(), any<Float>(), any<Float>(), any<Paint>())
    }

}