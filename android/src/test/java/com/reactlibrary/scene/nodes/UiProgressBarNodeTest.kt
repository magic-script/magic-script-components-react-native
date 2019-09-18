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

package com.reactlibrary.scene.nodes

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.reactlibrary.scene.nodes.UiProgressBarNode
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 * [JavaOnlyMap] was not available in the initial versions of React
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UiProgressBarNodeTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun shouldHaveDefaultWidth() {
        val node = UiProgressBarNode(JavaOnlyMap(), context)

        val width = node.getProperty(UiProgressBarNode.PROP_WIDTH)

        assertEquals(UiProgressBarNode.DEFAULT_WIDTH, width)
    }

    @Test
    fun shouldHaveDefaultHeight() {
        val node = UiProgressBarNode(JavaOnlyMap(), context)

        val height = node.getProperty(UiProgressBarNode.PROP_HEIGHT)

        assertEquals(UiProgressBarNode.DEFAULT_HEIGHT, height)
    }

}