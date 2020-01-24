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
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.views.CustomScrollBar
import com.nhaarman.mockitokotlin2.spy
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiScrollBarNodeTest {

    private lateinit var context: Context
    private lateinit var viewSpy: CustomScrollBar
    private lateinit var node: UiScrollBarNode

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.viewSpy = spy(CustomScrollBar(context))
        this.node = UiScrollBarNode(JavaOnlyMap())
        node.build()
    }

    @Test
    fun `should update length and thickness`() {
        val length = 0.6
        val thickness = 0.02
        val props = reactMapOf(
            UiScrollBarNode.PROP_LENGTH, length,
            UiScrollBarNode.PROP_THICKNESS, thickness
        )

        node.update(props)

        node.length shouldEqual length.toFloat()
        node.thickness shouldEqual thickness.toFloat()
    }

    @Test
    fun `should update thumb position`() {
        val thumbPosition = 0.43
        val props = reactMapOf(UiScrollBarNode.PROP_THUMB_POSITION, thumbPosition)

        node.update(props)

        node.thumbPosition shouldEqual thumbPosition.toFloat()
    }

    @Test
    fun `should update thumb size`() {
        val thumbSize = 0.77
        val props = reactMapOf(UiScrollBarNode.PROP_THUMB_SIZE, thumbSize)

        node.update(props)

        node.thumbSize shouldEqual thumbSize.toFloat()
    }

    @Test
    fun `should update orientation`() {
        val props = reactMapOf(
            UiScrollBarNode.PROP_ORIENTATION,
            UiScrollBarNode.ORIENTATION_HORIZONTAL
        )

        node.update(props)

        node.orientation shouldEqual UiScrollBarNode.ORIENTATION_HORIZONTAL
    }

}