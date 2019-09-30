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

package com.reactlibrary.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.reactlibrary.font.FontStyle
import com.reactlibrary.font.FontWeight
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FontProviderTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun `should return same typeface object for same font params`() {
        val fontWeight = FontWeight.BOLD
        val fontStyle = FontStyle.ITALIC

        val font1 = FontProvider.provideFont(context, fontWeight, fontStyle)
        val font2 = FontProvider.provideFont(context, fontWeight, fontStyle)

        assertTrue(font1 === font2) // comparing references
    }


    @Test
    fun `should return different typeface objects for different font params`() {
        val font1 = FontProvider.provideFont(context, FontWeight.REGULAR, FontStyle.NORMAL)
        val font2 = FontProvider.provideFont(context, FontWeight.BOLD, FontStyle.ITALIC)

        assertTrue(font1 !== font2) // comparing references
    }

}