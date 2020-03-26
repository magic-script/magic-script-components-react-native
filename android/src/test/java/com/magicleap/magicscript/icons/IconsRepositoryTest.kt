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

package com.magicleap.magicscript.icons

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.magicleap.magicscript.R
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IconsRepositoryTest {

    private lateinit var context: Context
    private lateinit var defaultIconsProvider: DefaultIconsProvider
    private lateinit var externalIconsProvider: ExternalIconsProvider
    private lateinit var iconsRepository: IconsRepositoryImpl

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        defaultIconsProvider = mock()
        externalIconsProvider = mock()
        iconsRepository = IconsRepositoryImpl(defaultIconsProvider, externalIconsProvider)
    }

    @Test
    fun `should return icon from external provider when available in both providers`() {
        val externalIcon = context.getDrawable(R.drawable.add)
        val defaultIcon = context.getDrawable(R.drawable.address_book)
        whenever(externalIconsProvider.provideIcon(anyString())).thenReturn(externalIcon)
        whenever(defaultIconsProvider.provideIcon(anyString())).thenReturn(defaultIcon)

        val icon = iconsRepository.getIcon("sample-icon")

        assertEquals(externalIcon, icon)
    }

    @Test
    fun `should return icon from default provider when not found in external`() {
        whenever(externalIconsProvider.provideIcon(anyString())).thenReturn(null)
        val defaultIcon = context.getDrawable(R.drawable.add)
        whenever(defaultIconsProvider.provideIcon(anyString())).thenReturn(defaultIcon)

        val icon = iconsRepository.getIcon("sample-icon")

        assertEquals(defaultIcon, icon)
    }

}