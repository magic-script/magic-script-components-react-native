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
 
 package com.reactlibrary.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.props.Padding
import org.robolectric.RobolectricTestRunner
import java.util.Arrays

@RunWith(RobolectricTestRunner::class)
class FlexLinearManagerTest {

    private fun defaultLayoutData() : LayoutData {
        return LayoutData(
            true, 
            Padding(0F, 0F, 0F, 0F), 
            ViewRenderable.HorizontalAlignment.CENTER, 
            ViewRenderable.VerticalAlignment.CENTER
        )
    }

    @Test
    fun `should work for empty children list`() {
        val children: List<Node> = emptyList()
        _layoutChildren(children, mapOf(), defaultLayoutData())
        assertTrue(children.isEmpty())
    }

    @Test
    fun `should calculate records span`() {

        var layoutData = defaultLayoutData()
        layoutData.itemPadding = Padding(1F,1F,1F,1F)
        val bounds = mapOf(
            0 to Bounding(1F,0F,2F,0F),
            1 to Bounding(-1F,0F,2F,0F),
            2 to Bounding(0F,0F,0F,0F)
        )

        val result = calculateSpan(bounds, layoutData)
        assertEquals(5F, result)
    }

    @Test
    fun `should calculate vertical records offset`() {

        var layoutData = defaultLayoutData()
        layoutData.isVertical = true
        layoutData.itemPadding = Padding(1F,1F,1F,1F)
        val bounds = mapOf(
            0 to Bounding(1F,10F,2F,15F),
            1 to Bounding(-1F,-7F,2F,3F),
            2 to Bounding(0F,0F,1F,2F)
        )

        val result = calculateOffset(bounds, layoutData)
        System.err.println(result)
        assertArrayEquals(arrayOf(16F,4F,0F), result)
    }

    @Test
    fun `should calculate horizontal records offset`() {

        var layoutData = defaultLayoutData()
        layoutData.isVertical = false
        layoutData.itemPadding = Padding(1F,1F,1F,1F)
        val bounds = mapOf(
            0 to Bounding(1F,0F,2F,0F),
            1 to Bounding(-1F,0F,2F,0F),
            2 to Bounding(0F,0F,1F,0F)
        )

        val result = calculateOffset(bounds, layoutData)
        assertArrayEquals(arrayOf(0F,3F,8F), result)
    }

    @Test
    fun `should correctly position node`() {

        var layoutData = defaultLayoutData()
        layoutData.itemPadding = Padding(100F, 200F, 300F, 400F)
        layoutData.itemHorizontalAlignment = ViewRenderable.HorizontalAlignment.LEFT
        val nodeBounds = Bounding(10F, 20F, 30F, 40F)
        var node = Node()

        node.localPosition = Vector3(1F, 2F, 3F)
        layoutNode(node, nodeBounds, 1F, 2F, layoutData)

        assertEquals(391F, node.localPosition.x)
        assertEquals(184F, node.localPosition.y)
        assertEquals(3F, node.localPosition.z)
    }
}
