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

import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.*
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

    @Test
    fun `if ArFragment is not ready should not add root to scene`() {
        val scene = spy<Scene>()

        nodesManager.registerScene(scene)

        scene.children.shouldBeEmpty()
    }

    @Test
    fun `if ArFragment is ready should add root to scene`() {
        val scene = spy<Scene>()

        nodesManager.registerScene(scene)

        nodesManager.onArFragmentReady()

        scene.children.isNotEmpty()
    }

    @Test
    fun `should update anchor onTapArPlane when planeDetection is true`() {
        val anchor = mock<Anchor>()
        val scene = spy<Scene>()

        nodesManager.planeDetection = true
        nodesManager.registerScene(scene)
        nodesManager.onArFragmentReady()

        nodesManager.onTapArPlane(anchor)

        (scene.children[0] as AnchorNode).anchor shouldBe anchor
    }

    @Test
    fun `should not update anchor onTapArPlane when planeDetection is false`() {
        val anchor = mock<Anchor>()
        val scene = spy<Scene>()

        nodesManager.planeDetection = false
        nodesManager.registerScene(scene)
        nodesManager.onArFragmentReady()

        nodesManager.onTapArPlane(anchor)

        (scene.children[0] as AnchorNode).anchor shouldNotBe anchor
    }

    @Test
    fun `should not attachRenderable if ArFragment is not ready`() {
        val node1 = mock<TransformNode> {
            on { hasRenderable }.doReturn(true)
        }

        nodesManager.registerNode(node1, "1")

        verify(node1, never()).attachRenderable()
    }

    @Test
    fun `should attachRenderable for all nodes with renderable when ArFragmentReady`() {
        val node1 = mock<TransformNode> {
            on { hasRenderable }.doReturn(true)
            nodesManager.registerNode(this.mock, "1")
        }
        val node2 = mock<TransformNode> {
            on { hasRenderable }.doReturn(true)
            nodesManager.registerNode(this.mock, "2")
        }
        val nodeWithoutRenderable = mock<TransformNode> {
            on { hasRenderable } itReturns false
            nodesManager.registerNode(this.mock, "3")
        }
        val scene = mock<Scene>()

        nodesManager.registerScene(scene)

        nodesManager.onArFragmentReady()

        verify(node1).attachRenderable()
        verify(node2).attachRenderable()
        verify(nodeWithoutRenderable, never()).attachRenderable()
    }

    @Test
    fun `rootNode shouldn't have register nodes at the beginning`() {
        val scene = spy<Scene>()

        nodesManager.registerScene(scene)
        nodesManager.onArFragmentReady()

        val root = scene.children[0]
        root.children.shouldBeEmpty()
    }

    @Test
    fun `should addNodeToRoot`() {
        val scene = spy<Scene>()

        nodesManager.registerScene(scene)
        nodesManager.onArFragmentReady()
        nodesManager.registerNode(mock(), "1")

        nodesManager.addNodeToRoot("1")

        val root = scene.children[0]
        root.children.size shouldEqual 1
    }

    @Test
    fun `should updateNode properties`() {
        val node = mock<TransformNode>()
        val props = JavaOnlyMap.of()

        nodesManager.registerNode(node, "1")

        nodesManager.updateNode("1", props)

        verify(node).update(props)
    }

    @Test
    fun `should notify all nodes when onHostResume`() {
        val node1 = mock<TransformNode>()
        val node2 = mock<TransformNode>()
        val node3 = mock<TransformNode>()

        nodesManager.registerNode(node1, "1")
        nodesManager.registerNode(node2, "2")
        nodesManager.registerNode(node3, "3")

        nodesManager.onHostResume()

        verify(node1).onResume()
        verify(node2).onResume()
        verify(node3).onResume()
    }

    @Test
    fun `should notify all nodes when onHostPause`() {
        val node1 = mock<TransformNode>()
        val node2 = mock<TransformNode>()
        val node3 = mock<TransformNode>()

        nodesManager.registerNode(node1, "1")
        nodesManager.registerNode(node2, "2")
        nodesManager.registerNode(node3, "3")

        nodesManager.onHostPause()

        verify(node1).onPause()
        verify(node2).onPause()
        verify(node3).onPause()
    }
}