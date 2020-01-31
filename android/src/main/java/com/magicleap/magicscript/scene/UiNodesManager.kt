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

import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReadableMap
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.CubeRenderableBuilderImpl
import com.magicleap.magicscript.scene.nodes.Prism
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.utils.logMessage
import kotlin.math.min
import kotlin.math.pow

/**
 * It manages nodes registration and attaching them to scene
 */
open class UiNodesManager : NodesManager, LifecycleEventListener {

    var planeDetection = false
        set(value) {
            field = value
            if (!value) {
                rootNode.anchor = null
            }
        }

    var showPrisms = false
        set(value) {
            field = value
            prism?.visible = value
        }

    private val rootNode by lazy { AnchorNode() }
    private val nodesById = HashMap<String, TransformNode>()
    private var arReady = false
    private lateinit var scene: Scene
    private var prism: Prism? = null
    private val prismContent by lazy { Node() }
    private var lastCameraPosition: Vector3? = null

    companion object {
        val INSTANCE = UiNodesManager()
    }

    @Synchronized
    fun onArFragmentReady(fragment: CustomArFragment) {
        arReady = true
        prism = Prism(
            JavaOnlyMap(),
            CubeRenderableBuilderImpl(scene.view.context),
            fragment.transformationSystem
        ).apply {
            // disabling default translationController, because it only allows moving on planes
            translationController.isEnabled = false
            scaleController.isEnabled = false
            setParent(rootNode)
            addChild(prismContent)
            attachRenderable()
        }

        rootNode.localPosition = Vector3(0f, 0f, 0f)
        scene.addChild(rootNode)
        nodesById.forEach { (_, node) ->
            if (node.hasRenderable && !node.renderableRequested) {
                node.attachRenderable()
            }
        }
    }

    @Synchronized
    fun onCameraUpdated(cameraPosition: Vector3) {
        if (lastCameraPosition == null) {
            lastCameraPosition = cameraPosition
            return
        }
        prism?.apply {
            if (beingTouched) {
                val camToPrismDiff = Vector3.subtract(rootNode.localPosition, cameraPosition)
                val camToPrismDistance =
                    camToPrismDiff.x.pow(2) + camToPrismDiff.y.pow(2) + camToPrismDiff.z.pow(2)

                // Limiting the movement speed since sometimes the [dist] is near
                // to infinity when moving device (ar core bug?)
                val speed = min(1 + camToPrismDistance, 2f).pow(2)

                val diff = Vector3.subtract(cameraPosition, lastCameraPosition).scaled(speed)
                rootNode.localPosition = Vector3.add(rootNode.localPosition, diff)
            }
            lastCameraPosition = cameraPosition
        }
    }

    @Synchronized
    override fun registerScene(scene: Scene) {
        this.scene = scene
        if (arReady) {
            scene.addChild(rootNode)
        }
    }

    @Synchronized
    override fun onTapArPlane(anchor: Anchor) {
        if (planeDetection) {
            rootNode.anchor = anchor
        }
    }

    @Synchronized
    override fun findNodeWithId(nodeId: String): Node? {
        return nodesById[nodeId]
    }

    @Synchronized
    override fun registerNode(node: TransformNode, nodeId: String) {
        node.name = nodeId
        nodesById[nodeId] = node
        logMessage("register node id= $nodeId, type=${node.javaClass.simpleName}")

        if (arReady && node.hasRenderable && !node.renderableRequested) {
            node.attachRenderable()
        }
    }

    @Synchronized
    override fun addNodeToRoot(nodeId: String) {
        val node = nodesById[nodeId]

        if (node == null) {
            logMessage("cannot add node: not found")
            return
        }
        if (tryAddNodeToAnchor(node)) {
            return
        }
        prismContent.addChild(node)
    }

    @Synchronized
    override fun addNodeToParent(nodeId: String, parentId: String) {
        val node = nodesById[nodeId]
        val parentNode = nodesById[parentId]

        if (node == null) {
            logMessage("cannot add node: not found")
            return
        }
        if (tryAddNodeToAnchor(node)) {
            return
        }
        if (parentNode == null) {
            logMessage("cannot add node: parent not found")
            return
        }
        parentNode.addContent(node)
    }

    private fun tryAddNodeToAnchor(node: TransformNode): Boolean {
        if (node.anchorUuid.isEmpty()) {
            return false
        }
        val anchorNode = scene.findByName(node.anchorUuid)
        return if (anchorNode is AnchorNode) {
            anchorNode.addChild(node)
            true
        } else {
            logMessage("tryAddNodeToAnchor anchorUuid not found: ${node.anchorUuid}")
            false
        }
    }

    @Synchronized
    override fun updateNode(nodeId: String, properties: ReadableMap): Boolean {
        val node = nodesById[nodeId]
        if (node == null) {
            logMessage("cannot update node: not found")
            return false
        }
        node.update(properties)
        return true
    }

    @Synchronized
    override fun removeNode(nodeId: String) {
        val node = nodesById[nodeId]

        if (node == null) {
            logMessage("cannot remove node: not found")
            return
        }
        removeFromMap(node)
        detachNode(node)
        logMessage("removed node id=$nodeId, nodes count=${nodesById.size}")
    }

    @Synchronized
    override fun clear() {
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

    private fun detachNode(node: TransformNode) {
        val parent = node.parent // content node
        val grandparent = parent?.parent
        if (grandparent is TransformNode) {
            grandparent.removeContent(node)
        } else {
            parent?.removeChild(node)
        }
    }

}