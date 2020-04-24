/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.base.ReactNode
import com.magicleap.magicscript.utils.logMessage

/**
 * It manages nodes registration and attaching them to scene
 */
class UiNodesManager : NodesManager, LifecycleEventListener {

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

    @Synchronized
    override fun addNodeToRoot(nodeId: String) {
        val node = nodesById[nodeId]

        if (node == null) {
            logMessage("cannot add node with id = $nodeId to root: not found", warn = true)
            return
        }
    }

    @Synchronized
    override fun addNodeToParent(nodeId: String, parentId: String) {
        val node = nodesById[nodeId]
        val parentNode = nodesById[parentId]

        if (node == null) {
            logMessage("cannot add node: not found", warn = true)
            return
        }
        if (parentNode == null) {
            logMessage("cannot add node: parent not found", warn = true)
            return
        }
        parentNode.addContent(node)
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
        Handler(Looper.getMainLooper()).post {
            logMessage("clear, nodeCount = ${nodesById.size}")
            nodesById.forEach { (_, node) ->
                detachNode(node)
                node.onDestroy()
            }
            nodesById.clear()
        }
    }

    override fun onHostResume() {
        nodesById.forEach { it.value.onHostResume() }
    }

    override fun onHostPause() {
        nodesById.forEach { it.value.onHostPause() }
    }

    override fun onHostDestroy() {
        nodesById.forEach { it.value.onHostDestroy() }
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