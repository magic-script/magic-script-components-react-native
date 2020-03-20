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

package com.magicleap.magicscript.scene

import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.AnchorNode
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.scene.nodes.base.ReactNode
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.utils.logMessage

/**
 * It manages nodes registration and attaching them to scene
 */
class UiNodesManager(private val arResourcesProvider: ArResourcesProvider) : NodesManager,
    LifecycleEventListener {

    private var reactScene: ReactScene? = null
    private val nodesById = HashMap<String, ReactNode>()

    @Synchronized
    override fun findNodeWithId(nodeId: String): ReactNode? {
        return nodesById[nodeId]
    }

    @Synchronized
    override fun registerNode(node: ReactNode, nodeId: String) {
        // node.name = nodeId
        nodesById[nodeId] = node
        logMessage("register node id= $nodeId, type=${node.javaClass.simpleName}")

        if (node is ReactScene) {
            this.reactScene = node
        }
    }

    /**
     * This function should receive a nodeId of Scene object but
     * for compatibility with XR client we temporarily allow adding nodes
     * to Anchor Nodes.
     */
    @Synchronized
    override fun addNodeToRoot(nodeId: String) {
        val node = nodesById[nodeId]

        if (node == null) {
            logMessage("cannot add node with id = $nodeId to root: not found", warn = true)
            return
        }

        tryAddNodeToAnchor(node)
    }

    @Synchronized
    override fun addNodeToParent(nodeId: String, parentId: String) {
        val node = nodesById[nodeId]
        val parentNode = nodesById[parentId]

        if (node == null) {
            logMessage("cannot add node: not found", warn = true)
            return
        }
        if (tryAddNodeToAnchor(node)) {
            return
        }
        if (parentNode == null) {
            logMessage("cannot add node: parent not found", warn = true)
            return
        }
        parentNode.addContent(node)
    }

    private fun tryAddNodeToAnchor(node: ReactNode): Boolean {
        if (node !is TransformNode) {
            return false
        }

        if (node.anchorUuid.isEmpty()) {
            return false
        }

        val scene = arResourcesProvider.getArScene()
        if (scene == null) {
            logMessage("tryAddNodeToAnchor ar scene not initialized", warn = true)
            return false
        }

        val anchorNode = scene.findByName(node.anchorUuid)
        return if (anchorNode is AnchorNode) {
            anchorNode.addChild(node)
            true
        } else {
            logMessage("tryAddNodeToAnchor anchorUuid not found: ${node.anchorUuid}", warn = true)
            false
        }
    }

    @Synchronized
    override fun updateNode(nodeId: String, properties: ReadableMap): Boolean {
        val node = nodesById[nodeId]
        if (node == null) {
            logMessage("cannot update node: not found", warn = true)
            return false
        }
        node.update(properties)
        return true
    }

    @Synchronized
    override fun removeNode(nodeId: String) {
        val node = nodesById[nodeId]

        if (node == null) {
            logMessage("cannot remove node: not found", warn = true)
            return
        }
        removeFromMap(node)
        detachNode(node)
        logMessage("removed node id=$nodeId, nodes count=${nodesById.size}")
    }

    @Synchronized
    override fun clear() {
        logMessage("clear")
        nodesById.forEach { (_, node) ->
            detachNode(node)
            node.onDestroy()
        }
        nodesById.clear()
    }

    override fun onHostResume() {
        nodesById.forEach { it.value.onResume() }
    }

    override fun onHostPause() {
        nodesById.forEach { it.value.onPause() }
    }

    override fun onHostDestroy() {
        // no-op
    }

    // removes node with descendants from the nodes map
    private fun removeFromMap(node: ReactNode) {
        node.reactChildren.forEach { child ->
            removeFromMap(child)
        }

        val key = nodesById.entries.firstOrNull { it.value == node }?.key
        if (key != null) {
            nodesById.remove(key)
        }

        node.onDestroy()
    }

    private fun detachNode(node: ReactNode) {
        val parent = node.reactParent
        parent?.removeContent(node)
    }

}