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
            if (!node.renderableRequested) {
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
        logMessage("register node: $node")

        if (arReady && !node.renderableRequested) {
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

        parentNode.addChildNode(node)
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
    fun unregisterNode(nodeId: String) {
        val node = nodesById[nodeId]

        if (node == null) {
            logMessage("cannot unregister node: not found")
            return
        }
        nodesById.remove(nodeId)
    }

    @JvmStatic
    @Synchronized
    fun removeNode(nodeId: String) {
        val node = nodesById[nodeId]

        if (node == null) {
            logMessage("cannot remove node: not found")
            return
        }
        node.parent?.removeChild(node)
    }

    @JvmStatic
    @Synchronized
    fun clear() {
        nodesById.forEach { (_, node) ->
            node.parent?.removeChild(node)
        }
        nodesById.clear()
    }

    @JvmStatic
    fun validateScene(): Boolean {
        if (nodesById.isEmpty() && rootNode.children.isEmpty()) {
            logMessage("[UiNodesManager] Nodes tree hierarchy and nodes list are empty.")
            return true
        }

        if (nodesById.isEmpty() || rootNode.children.isEmpty()) {
            logMessage("[UiNodesManager] One nodes container (either nodes tree hierarchy " +
                    "(${rootNode.children.size}))" +
                    " or nodes list (${nodesById.size})) is empty!")
            return true
        }

        val looseNodes = rootNode.children.filter { child ->
            return child.parent != null && nodesById.containsKey(child.name)
        }

        if (looseNodes.isNotEmpty()) {
            logMessage("[UiNodesManager] Found (${looseNodes.size}) loose nodes.")
            return false
        }

        return true
    }


}