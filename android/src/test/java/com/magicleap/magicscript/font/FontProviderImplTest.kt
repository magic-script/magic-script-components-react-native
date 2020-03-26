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

package com.magicleap.magicscript.font

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.magicleap.magicscript.font.providers.AndroidFontProvider
import com.magicleap.magicscript.font.providers.FontProviderImpl
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FontProviderImplTest {

    private lateinit var context: Context
    private lateinit var fontProvider: FontProviderImpl

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        val systemFontProvider = AndroidFontProvider()
        fontProvider = FontProviderImpl(context, systemFontProvider)
    }

    @Test
    fun `should return same typeface object for same font params`() {
        val font1 = fontProvider.provideFont(FontStyle.ITALIC, FontWeight.BOLD)
        val font2 = fontProvider.provideFont(FontStyle.ITALIC, FontWeight.BOLD)

        assertTrue(font1 === font2) // comparing references
    }


    @Test
    fun `should return different typeface objects for different weights`() {
        val font1 = fontProvider.provideFont(FontStyle.NORMAL, FontWeight.LIGHT)
        val font2 = fontProvider.provideFont(FontStyle.NORMAL, FontWeight.MEDIUM)

        assertTrue(font1 !== font2) // comparing references
    }

}