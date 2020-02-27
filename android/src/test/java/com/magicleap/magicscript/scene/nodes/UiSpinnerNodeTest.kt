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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.reactMapOf
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.views.CustomSpinner
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
class UiSpinnerNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: CustomSpinner

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.viewSpy = spy(CustomSpinner(context))
    }

    @Test
    fun `should have default height`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val height = node.getProperty(UiSpinnerNode.PROP_HEIGHT)

        assertEquals(UiSpinnerNode.DEFAULT_HEIGHT, height)
    }

    @Test
    fun `should have default determinate flag`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val isDeterminate = node.getProperty(UiSpinnerNode.PROP_DETERMINATE)

        assertEquals(UiSpinnerNode.DEFAULT_DETERMINATE, isDeterminate)
    }

    @Test
    fun `should apply progress when determinate`() {
        val value = 0.3
        val props = reactMapOf(
            UiSpinnerNode.PROP_VALUE, value,
            UiSpinnerNode.PROP_DETERMINATE, true
        )
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).type = CustomSpinner.Type.DETERMINATE
        verify(viewSpy).value = value.toFloat()
    }

    @Test
    fun `should not apply progress when not determinate`() {
        val value = 0.6
        val props = reactMapOf(
            UiSpinnerNode.PROP_VALUE, value,
            UiSpinnerNode.PROP_DETERMINATE, false
        )
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).type = CustomSpinner.Type.INDETERMINATE
        verify(viewSpy, never()).value = value.toFloat()
    }

    @Test
    fun `should not change hardcoded alignment`() {
        val props = reactMapOf(TransformNode.PROP_ALIGNMENT, "top-left")
        val node = createNodeWithViewSpy(props)

        node.build()

        assertEquals(Alignment.Horizontal.CENTER, node.horizontalAlignment)
        assertEquals(Alignment.Vertical.CENTER, node.verticalAlignment)
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiSpinnerNode {
        return object : UiSpinnerNode(props, context, mock(), mock()) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

}