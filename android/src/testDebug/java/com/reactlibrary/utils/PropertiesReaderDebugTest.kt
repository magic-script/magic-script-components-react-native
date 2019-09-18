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

package com.reactlibrary.utils

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import com.reactlibrary.BuildConfig
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PropertiesReaderDebugTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun `should return uri equivalent for path inside bundle if debug mode`() {
        assertTrue(BuildConfig.DEBUG)
        val path = "http://localhost/sample-image.jpg"
        val pathBundle = Bundle()
        pathBundle.putString("uri", path)
        val propsBundle = Bundle()
        val prop = "imagePath"
        propsBundle.putBundle(prop, pathBundle)
        val expected = Uri.parse(path)

        val uri = PropertiesReader.readImagePath(propsBundle, prop, context)

        assertTrue(expected == uri)
    }

}