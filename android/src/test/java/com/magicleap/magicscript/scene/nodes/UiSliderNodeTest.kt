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
import com.magicleap.magicscript.reactMapOf
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.magicleap.magicscript.scene.nodes.views.CustomSlider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiSliderNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: CustomSlider

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.viewSpy = spy(CustomSlider(context))
    }

    @Test
    fun shouldApplyValueWhenValuePropertyPresent() {
        val value = 0.99
        val props = reactMapOf(UiSliderNode.PROP_VALUE, value)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).value = value.toFloat()
    }

    @Test
    fun shouldApplyMinValueWhenMinPropertyPresent() {
        val minValue = 1.0
        val props = reactMapOf(UiSliderNode.PROP_MIN, minValue)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).min = minValue.toFloat()
    }

    @Test
    fun shouldApplyMaxValueWhenMaxPropertyPresent() {
        val maxValue = 1.5
        val props = reactMapOf(UiSliderNode.PROP_MAX, maxValue)
        val node = createNodeWithViewSpy(props)

        node.build()

        verify(viewSpy).max = maxValue.toFloat()
    }

    private fun createNodeWithViewSpy(props: ReadableMap): UiSliderNode {
        return object : UiSliderNode(props, context, mock()) {
            override fun provideView(context: Context): View {
                return viewSpy
            }
        }
    }

}