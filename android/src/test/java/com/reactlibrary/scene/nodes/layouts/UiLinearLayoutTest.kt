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

package com.reactlibrary.scene.nodes

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.reactlibrary.scene.nodes.layouts.UiLinearLayout
import com.reactlibrary.scene.nodes.layouts.manager.LinearLayoutManager
import junit.framework.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * To represent node's properties map in tests we use [JavaOnlyMap] which
 * does not require native React's resources.
 */
@RunWith(RobolectricTestRunner::class)
class UiLinearLayoutTest {

    private lateinit var linearLayoutManager: LinearLayoutManager

    @Before
    fun setUp() {
        linearLayoutManager = mock()
    }

    @Test
    fun shouldApplyVerticalOrientationByDefault() {
        val node = UiLinearLayout(JavaOnlyMap(), linearLayoutManager)
        node.build()

        verify(linearLayoutManager).isVertical = true
        verify(linearLayoutManager, atLeastOnce()).layoutChildren(any(), any())
        assertFalse(node.redrawRequested) // redraw already happened
    }

    @Test
    fun shouldApplyItemPaddingWhenItemPaddingPropertyPresent() {
        val padding = JavaOnlyArray.of(1.5, 2.0, 1.5, 0.0)
        val props = JavaOnlyMap.of(UiLinearLayout.PROP_DEFAULT_ITEM_PADDING, padding)
        val node = UiLinearLayout(props, linearLayoutManager)
        node.build()

        verify(linearLayoutManager).itemPadding = any()
        verify(linearLayoutManager, atLeastOnce()).layoutChildren(any(), any())
    }

}