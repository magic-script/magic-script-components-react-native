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

package com.reactlibrary.scene

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.logMessage

/**
 * It manages nodes registration and attaching them to scene
 */
object UiNodesManager {

    private val rootNode = Node()
    private val nodesById = HashMap<String, TransformNode>()
    private var arReady = false
    private lateinit var scene: Scene

    @Synchronized
    fun onArFragmentReady() {
        arReady = true
        scene.addChild(rootNode)
        nodesById.forEach { (_, node) ->
            if (node.hasRenderable && !node.renderableRequested) {
                node.attachRenderable()
            }
        }
    }

    @JvmStatic
    @Synchronized
    fun registerScene(scene: Scene) {
        this.scene = scene
        if (arReady) {
            scene.addChild(rootNode)
        }
    }

    @JvmStatic
    @Synchronized
    fun findNodeWithId(nodeId: String): Node? {
        return nodesById[nodeId]
    }

    @JvmStatic
    @Synchronized
    fun registerNode(node: TransformNode, nodeId: String) {
        node.name = nodeId
        nodesById[nodeId] = node
        logMessage("register node id= $nodeId, type=${node.javaClass.simpleName}")

        if (arReady && node.hasRenderable && !node.renderableRequested) {
            node.attachRenderable()
        }
    }

    @JvmStatic
    @Synchronized
    fun addNodeToRoot(nodeId: String) {
        val node = nodesById[nodeId]

        if (node == null) {
            logMessage("cannot add node: not found")
            return
        }
        rootNode.addChild(node)
    }

    @JvmStatic
    @Synchronized
    fun addNodeToParent(nodeId: String, parentId: String) {
        val node = nodesById[nodeId]
        val parentNode = nodesById[parentId]

        if (node == null) {
            logMessage("cannot add node: not found")
            return
        }
        if (parentNode == null) {
            logMessage("cannot add node: parent not found")
            return
        }

        parentNode.addContent(node)
    }

    @JvmStatic
    @Synchronized
    fun updateNode(nodeId: String, properties: ReadableMap): Boolean {
        val node = nodesById[nodeId]
        if (node == null) {
            logMessage("cannot update node: not found")
            return false
        }
        node.update(properties)
        return true
    }

    @JvmStatic
    @Synchronized
    fun removeNode(nodeId: String) {
        val node = nodesById[nodeId]

        if (node == null) {
            logMessage("cannot remove node: not found")
            return
        }
        removeFromMap(node)
        detachNode(node)
        logMessage("removed node id=$nodeId, nodes count=${nodesById.size}")
    }

    @JvmStatic
    @Synchronized
    fun clear() {
        nodesById.forEach { (_, node) ->
            detachNode(node)
            node.onDestroy()
        }
        nodesById.clear()
    }

    // removes node with descendants from the nodes map
    private fun removeFromMap(node: Node) {
        node.children.forEach { child ->
            removeFromMap(child)
        }

        val key = nodesById.entries.firstOrNull { it.value == node }?.key
        if (key != null) {
            nodesById.remove(key)
        }

        if (node is TransformNode) {
            node.onDestroy()
        }
    }

    private fun detachNode(node: Node) {
        val parent = node.parent // content node
        val grandparent = parent?.parent
        if (grandparent is TransformNode) {
            grandparent.removeContent(node)
        } else {
            parent?.removeChild(node)
        }
    }

}