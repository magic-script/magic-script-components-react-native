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

package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.nhaarman.mockitokotlin2.mock
import com.magicleap.magicscript.scene.nodes.layouts.UiLinearLayout
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LinearLayoutManagerTest {

    private lateinit var linearLayout: UiLinearLayout
    private lateinit var linearManager: LinearLayoutManager

    @Before
    fun setUp() {
        this.linearLayout = mock()
        this.linearManager = LinearLayoutManagerImpl()
        linearManager.itemHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
        linearManager.itemVerticalAlignment = Alignment.VerticalAlignment.TOP
        linearManager.itemPadding = Padding(1F, 1F, 1F, 1F)
    }

    @Test
    fun `should work for empty children list`() {
        val children: List<TransformNode> = emptyList()

        linearManager.layoutChildren(children, mapOf())

        assertTrue(children.isEmpty())
    }

    @Test
    fun `should position child node`() {
        val children: List<TransformNode> = listOf(NodeBuilder().build())
        val bound = Bounding(0F, 0F, 1F, 1F)
        val bounds: Map<Int, Bounding> = mapOf(0 to bound, 1 to bound)

        linearManager.layoutChildren(children, bounds)

        assertNotEquals(Vector3(0F, 0F, 0F), children[0].localPosition)
    }
}
