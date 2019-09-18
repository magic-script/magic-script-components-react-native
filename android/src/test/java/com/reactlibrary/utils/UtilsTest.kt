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

package com.reactlibrary.utils

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.props.Bounding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UtilsTest {

    @Test
    fun shouldReturnBasicBoundingWhenCollisionShapeIsNotBox() {
        val node = Node()
        node.localPosition = Vector3(1f, 1f, 1f)

        val bounding = Utils.calculateBoundsOfNode(node)

        assertNotNull(bounding)
        assertEquals(node.localPosition.x, bounding.left)
        assertEquals(node.localPosition.x, bounding.right)
        assertEquals(node.localPosition.y, bounding.top)
        assertEquals(node.localPosition.y, bounding.bottom)
    }

    @Test
    fun shouldReturnBoundingWithWidestAndHighestAreaOfAllNodes() {
        val testNode1 = Node()
        val testNode2 = Node()
        val testNode3 = Node()
        testNode1.localPosition = Vector3(1f, 2f, 3f)
        testNode2.localPosition = Vector3(10f, 20f, 30f)
        testNode3.localPosition = Vector3(100f, 200f, 300f)

        val bounding = Utils.calculateSumBounds(listOf(testNode1, testNode2, testNode3))

        assertNotNull(bounding)
        assertEquals(1f, bounding.left)
        assertEquals(100f, bounding.right)
        assertEquals(2f, bounding.bottom)
        assertEquals(200f, bounding.top)
    }

    @Test
    fun shouldReturnEmptyBoundingWhenListOfNodesIsEmpty() {
        val bounding = Utils.calculateSumBounds(emptyList())

        assertEquals(Bounding(0f, 0f, 0f, 0f), bounding)
    }

    @Test
    fun shouldReturnFirstNodeBoundingIfListContainsOnlyOneNode() {
        val testNode = Node()
        testNode.localPosition = Vector3(1f, 1f, 1f)

        val bounding = Utils.calculateSumBounds(listOf(testNode))

        assertEquals(Bounding(1f, 1f, 1f, 1f), bounding)
    }

}