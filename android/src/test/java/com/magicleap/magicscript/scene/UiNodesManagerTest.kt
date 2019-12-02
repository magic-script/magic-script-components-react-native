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

package com.magicleap.magicscript.scene

import com.magicleap.magicscript.NodeBuilder
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UiNodesManagerTest {

    private lateinit var nodesManager: UiNodesManager

    @Before
    fun setUp() {
        nodesManager = UiNodesManager()
    }

    @Test
    fun `should register node`() {
        val node = NodeBuilder().build()
        nodesManager.registerNode(node, "1")

        nodesManager.findNodeWithId("1") shouldEqual node
    }

    @Test
    fun `should remove node`() {
        val node = NodeBuilder().build()
        val id = "1"
        nodesManager.registerNode(node, id)

        nodesManager.removeNode(id)

        nodesManager.findNodeWithId(id) shouldBe null
    }

    @Test
    fun `child should be attached to content node of parent`() {
        val parent = NodeBuilder().build()
        val parentId = "some parent id"
        nodesManager.registerNode(parent, parentId)
        nodesManager.registerNode(NodeBuilder().build(), "1")
        nodesManager.addNodeToParent("1", parentId)

        parent.contentNode.children.size shouldEqual 1
    }

    @Test
    fun `child should be detached from content node of parent when removed`() {
        val parent = NodeBuilder().build()
        val parentId = "some parent id"
        nodesManager.registerNode(parent, parentId)
        nodesManager.registerNode(NodeBuilder().build(), "1")
        nodesManager.addNodeToParent("1", parentId)

        nodesManager.removeNode("1")

        parent.contentNode.children.size shouldEqual 0
    }

    @Test
    fun `should remove children when parent removed`() {
        val parentId = "0"
        nodesManager.registerNode(NodeBuilder().build(), parentId)
        nodesManager.registerNode(NodeBuilder().build(), "1")
        nodesManager.registerNode(NodeBuilder().build(), "2")
        nodesManager.addNodeToParent("1", parentId)
        nodesManager.addNodeToParent("2", parentId)

        nodesManager.removeNode(parentId)

        nodesManager.findNodeWithId("1") shouldBe null
        nodesManager.findNodeWithId("2") shouldBe null
        nodesManager.findNodeWithId(parentId) shouldBe null
    }

    @Test
    fun `should clear all nodes`() {
        nodesManager.registerNode(NodeBuilder().build(), "1")
        nodesManager.registerNode(NodeBuilder().build(), "2")
        nodesManager.registerNode(NodeBuilder().build(), "3")
        nodesManager.addNodeToParent("3", "2")

        nodesManager.clear()

        nodesManager.findNodeWithId("1") shouldBe null
        nodesManager.findNodeWithId("2") shouldBe null
        nodesManager.findNodeWithId("3") shouldBe null
    }

}