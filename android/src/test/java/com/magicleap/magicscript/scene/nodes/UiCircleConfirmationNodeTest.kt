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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.createActionDownEvent
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.views.CircleConfirmationView
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import org.amshove.kluent.shouldEqual
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import kotlin.math.PI

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiCircleConfirmationNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: CircleConfirmationView
    private val epsilon = 1e-5F

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.viewSpy = spy(CircleConfirmationView(context))
    }

    @Test
    fun `should have default radius`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())

        val radius = node.getProperty(UiCircleConfirmationNode.PROP_RADIUS)

        radius shouldEqual UiCircleConfirmationNode.DEFAULT_RADIUS
    }

    @Test
    fun `should set correct height for given circle radius`() {
        val props = reactMapOf(UiCircleConfirmationNode.PROP_RADIUS, 0.22)
        val node = createNodeWithViewSpy(props)
        node.build()

        val height = node.getBounding().size().y

        // height = 2 * circle radius
        assertEquals(0.44F, height, epsilon)
    }

    @Test
    fun `should notify correct progress angle when half of required time passed`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()
        var progress = 0f
        node.onConfirmationUpdatedListener = { it ->
            progress = it
        }

        // simulate click and time passed
        val event = viewSpy.createActionDownEvent()
        shadowOf(viewSpy).onTouchListener.onTouch(viewSpy, event)
        node.forceUpdate(UiCircleConfirmationNode.TIME_TO_COMPLETE / 2)

        assertEquals(PI.toFloat(), progress, epsilon)
    }

    @Test
    fun `should notify the confirmation`() {
        val node = createNodeWithViewSpy(JavaOnlyMap())
        node.build()
        var notified = false
        node.onConfirmationCompletedListener = {
            notified = true
        }

        // simulate click and time passed
        val event = viewSpy.createActionDownEvent()
        shadowOf(viewSpy).onTouchListener.onTouch(viewSpy, event)
        node.forceUpdate(UiCircleConfirmationNode.TIME_TO_COMPLETE)

        notified shouldEqual true
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiCircleConfirmationNode {
        return object : UiCircleConfirmationNode(props, context, mock()) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

}