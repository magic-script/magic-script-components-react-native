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

package com.reactlibrary.scene.nodes.base

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiNodeTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun shouldBeEnabledByDefault() {
        val node = object : UiNode(JavaOnlyMap(), context, mock()) {
            override fun provideView(context: Context): View {
                return mock()
            }
        }

        val enabledProp = node.getProperty(UiNode.PROP_ENABLED)

        assertEquals(true, enabledProp)
    }


}