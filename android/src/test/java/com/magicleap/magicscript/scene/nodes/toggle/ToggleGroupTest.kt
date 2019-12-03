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

package com.magicleap.magicscript.scene.nodes.toggle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.reactMapOf
import com.nhaarman.mockitokotlin2.mock
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
class ToggleGroupTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `only one toggle should be active when multiple selection not allowed`() {
        val node = buildGroupNode(
                reactMapOf(ToggleGroupNode.PROP_ALLOW_MULTIPLE_ON, false)
        )
        val toggle1 = buildToggleNode(reactMapOf(UiToggleNode.PROP_CHECKED, false))
        val toggle2 = buildToggleNode(reactMapOf(UiToggleNode.PROP_CHECKED, true))
        val toggle3 = buildToggleNode(reactMapOf(UiToggleNode.PROP_CHECKED, true))
        val togglesList = listOf(toggle1, toggle2, toggle3)
        node.addToggles(togglesList)
        toggle1.update(reactMapOf(UiToggleNode.PROP_CHECKED, true))

        val numberOfActiveToggles = countActiveToggles(togglesList)

        numberOfActiveToggles shouldEqual 1
    }

    @Test
    fun `every toggle can be active when multiple selection is allowed`() {
        val node = buildGroupNode(
                reactMapOf(ToggleGroupNode.PROP_ALLOW_MULTIPLE_ON, true)
        )
        val toggle1 = buildToggleNode(reactMapOf(UiToggleNode.PROP_CHECKED, true))
        val toggle2 = buildToggleNode(reactMapOf(UiToggleNode.PROP_CHECKED, true))
        val togglesList = listOf(toggle1, toggle2)
        node.addToggles(togglesList)

        val activeTogglesNumber = countActiveToggles(togglesList)

        activeTogglesNumber shouldEqual 2
    }

    @Test
    fun `all toggles should be initially off when forced by group`() {
        val node = buildGroupNode(reactMapOf(
                ToggleGroupNode.PROP_FORCE_ALL_OFF, true
        ))
        val toggle1 = buildToggleNode(reactMapOf(UiToggleNode.PROP_CHECKED, true))
        val toggle2 = buildToggleNode(reactMapOf(UiToggleNode.PROP_CHECKED, true))
        val togglesList = listOf(toggle1, toggle2)
        node.addToggles(togglesList)

        val activeTogglesNumber = countActiveToggles(togglesList)

        activeTogglesNumber shouldEqual 0
    }

    private fun buildGroupNode(props: ReadableMap): ToggleGroupNode {
        val group = ToggleGroupNode(props)
        group.build()
        return group
    }

    private fun buildToggleNode(props: ReadableMap): UiToggleNode {
        val toggle = UiToggleNode(props, context, mock(), mock())
        toggle.build()
        return toggle
    }

    private fun ToggleGroupNode.addToggles(toggles: List<UiToggleNode>) {
        toggles.forEach {
            this.addContent(it)
        }
    }

    private fun countActiveToggles(toggles: List<UiToggleNode>): Int {
        return toggles.count { it.isOn }
    }
}