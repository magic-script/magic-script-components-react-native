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

package com.magicleap.magicscript.scene.nodes.base

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.update
import com.magicleap.magicscript.utils.Vector2
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.shouldEqual
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
    private lateinit var viewRenderableLoader: ViewRenderableLoader
    private lateinit var node: UiNode

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        viewRenderableLoader = mock()
        node = object : UiNode(JavaOnlyMap(), context, viewRenderableLoader) {
            override fun provideView(context: Context): View {
                return mock()
            }

            override fun provideDesiredSize(): Vector2 {
                return Vector2(0F, 0F)
            }
        }
        node.build()
    }

    @Test
    fun `should be enabled by default`() {
        val enabledProp = node.getProperty(UiNode.PROP_ENABLED)

        assertEquals(true, enabledProp)
    }

    @Test
    fun `should load renderable when attach requested`() {
        node.attachRenderable()

        verify(viewRenderableLoader).loadRenderable(any(), any())
    }

    @Test
    fun `should notify when property updated`() {
        var called = false
        node.onUpdatedListener = { called = true }

        node.update(TransformNode.PROP_ALIGNMENT, "top-right")

        called shouldEqual true
    }

    @Test
    fun `should notify when destroyed`() {
        var called = false
        node.onDeletedListener = { called = true }

        node.onDestroy()

        called shouldEqual true
    }

    @Test
    fun `should notify after disabling`() {
        var called = false
        node.onDisabledListener = { called = true }

        node.update(UiNode.PROP_ENABLED, false)

        called shouldEqual true
    }

    @Test
    fun `should notify after enabling`() {
        var called = false
        node.onEnabledListener = { called = true }
        node.update(UiNode.PROP_ENABLED, false)

        node.update(UiNode.PROP_ENABLED, true)

        called shouldEqual true
    }

}