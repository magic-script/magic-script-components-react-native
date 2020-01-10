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

package com.magicleap.magicscript.scene.nodes.base

import android.os.Bundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Matrix
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.*
import kotlin.properties.Delegates

/**
 * Base node.
 * It's characterised by [properties] bundle based on initial properties.
 * Properties can be added or changed using the [update] function.
 *
 * @param initProps the initial properties of the node
 * @param hasRenderable indicates whether the node will have a renderable (view, model, etc)
 * @param useContentNodeAlignment whether to use the [contentNode] for alignment (usually it should
 * be true for nodes that don't use renderable alignment in ViewRenderble.Builder)
 */
abstract class TransformNode(
    initProps: ReadableMap,
    val hasRenderable: Boolean,
    protected val useContentNodeAlignment: Boolean
) : Node() {

    companion object {

        const val PROP_LOCAL_POSITION = "localPosition"
        const val PROP_LOCAL_SCALE = "localScale"
        const val PROP_LOCAL_ROTATION = "localRotation"
        const val PROP_LOCAL_TRANSFORM = "localTransform"
        const val PROP_ALIGNMENT = "alignment"

        /**
         * Indicates how often we measure bounding and (if necessary)
         * refresh alignment. Used only when [useContentNodeAlignment].
         */
        private const val ALIGNMENT_INTERVAL = 0.05F // in seconds
    }

    var onUpdatedListener: (() -> Unit)? = null
    var onDeletedListener: (() -> Unit)? = null

    /**
     * Used as content node for alignment purpose.
     * Renderable and / or child nodes should be added to it.
     */
    val contentNode = Node()

    /**
     * Alignment used to position a Renderable or [contentNode]
     */
    var horizontalAlignment: Alignment.HorizontalAlignment = Alignment.HorizontalAlignment.CENTER
        private set

    var verticalAlignment: Alignment.VerticalAlignment = Alignment.VerticalAlignment.CENTER
        private set

    /**
     * Returns true if already started loading the renderable, otherwise false
     * (loading a renderable is an asynchronous operation)
     */
    var renderableRequested = false
        private set

    /**
     * All node's properties (packed to Bundle to avoid "already consumed"
     * exceptions when reading from [ReadableMap])
     */
    protected val properties = Arguments.toBundle(initProps) ?: Bundle()

    var isVisible: Boolean by Delegates.observable(true) { prop, old, new ->
        onVisibilityChanged(new)
    }

    protected var updatingProperties = false
        private set

    private var bounding = Bounding(0F, 0F, 0F, 0F) // default

    private var timeSinceLastAlignment = 0F

    init {
        addChild(contentNode)
        logMessage("initial properties = ${this.properties}")
    }

    /**
     * Builds the node by calling [applyProperties] with all initial properties
     */
    open fun build() {
        applyProperties(properties)
    }

    /**
     * Attaches a renderable (view, model) to the node
     * Must be called after the ARCore resources have been initialized
     * @see: https://github.com/google-ar/sceneform-android-sdk/issues/574
     */
    fun attachRenderable() {
        loadRenderable()
        renderableRequested = true
    }

    /**
     * Updates properties of the node.
     * Should be called after [build]
     *
     * @param props properties to change or new properties to apply
     */
    fun update(props: ReadableMap) {
        updatingProperties = true
        val propsToUpdate = Arguments.toBundle(props) ?: Bundle()
        this.properties.putAll(propsToUpdate) // save new props

        logMessage("updating properties: $propsToUpdate")
        applyProperties(propsToUpdate)
        updatingProperties = false

        onUpdatedListener?.invoke()
    }

    /**
     * Adds [child] to [contentNode]
     *
     * To manage alignment correctly, we should use this method instead of
     * attaching a child directly.
     */
    open fun addContent(child: TransformNode) {
        if (!isVisible) {
            child.hide()
        }
        contentNode.addChild(child)
    }

    open fun removeContent(child: TransformNode) {
        contentNode.removeChild(child)
    }

    /**
     * Returns 2D bounding of the node (the minimum rectangle
     * that includes the node).
     */
    fun getBounding(): Bounding {
        val contentBounds = getContentBounding()

        // bounding vertices
        val p1 = Vector3(contentBounds.left, contentBounds.top, localPosition.z)
            .rotatedBy(localRotation)
        val p2 = Vector3(contentBounds.left, contentBounds.bottom, localPosition.z)
            .rotatedBy(localRotation)
        val p3 = Vector3(contentBounds.right, contentBounds.bottom, localPosition.z)
            .rotatedBy(localRotation)
        val p4 = Vector3(contentBounds.right, contentBounds.top, localPosition.z)
            .rotatedBy(localRotation)

        val minimumBounds = Utils.findMinimumBounding(listOf(p1, p2, p3, p4))

        return Bounding(
            left = minimumBounds.left * localScale.x + localPosition.x,
            bottom = minimumBounds.bottom * localScale.y + localPosition.y,
            right = minimumBounds.right * localScale.x + localPosition.x,
            top = minimumBounds.top * localScale.y + localPosition.y
        )
    }

    /**
     * Should return 2D bounding of the [contentNode] (relative to
     * the parent node position).
     */
    open fun getContentBounding(): Bounding {
        return Utils.calculateBoundsOfNode(contentNode)
    }

    /**
     * Returns the value for a given [propertyName]
     */
    fun getProperty(propertyName: String): Any? {
        return properties.get(propertyName)
    }

    /**
     * Should clear all node's resources (if any)
     */
    open fun onDestroy() {
        onDeletedListener?.invoke()
    }

    /**
     * Should pause any media related to the node
     */
    open fun onPause() {

    }

    /**
     * Should resume any media related to the node
     */
    open fun onResume() {

    }

    /**
     * Should return position of the content (relative to this node's parent)
     */
    open fun getContentPosition(): Vector2 {
        val position = localPosition + contentNode.localPosition
        return Vector2(position.x, position.y)
    }

    /**
     * Should hide this part of node that is outside [clipBounds]
     */
    open fun setClipBounds(clipBounds: Bounding) {}

    /**
     * Called on AR core's [Node.onUpdate] method invocation.
     * We use custom onUpdate function in order to make it testable.
     */
    open fun onUpdate(deltaSeconds: Float) {
        if (!useContentNodeAlignment) {
            return
        }

        timeSinceLastAlignment += deltaSeconds
        if (timeSinceLastAlignment >= ALIGNMENT_INTERVAL) {
            timeSinceLastAlignment = 0F
            val currentBounding = getBounding()
            if (!Bounding.equalInexact(currentBounding, bounding)) {
                // Refreshing alignment from a loop, because:
                // - we don't know node size at beginning,
                // - node size may have changed,
                // - immediately after attaching a renderable the collision shape
                // of a node returns wrong size (default) as it's probably calculated
                // asynchronously
                applyAlignment()
            }
            bounding = currentBounding
        }
    }

    open fun hide() {
        isVisible = false
    }

    open fun show() {
        isVisible = true
    }

    open fun onVisibilityChanged(visibility: Boolean) {
        contentNode.children
            .filterIsInstance<TransformNode>()
            .forEach { child ->
                if (visibility) {
                    child.show()
                } else {
                    child.hide()
                }
            }
    }

    /**
     * Forces node update. For tests purposes.
     *
     * @param deltaSeconds seconds elapsed since last update
     * You can pass any value to simulate time elapsed since the last frame
     * and test behavior of ARCore's [Node.onUpdate] dependent code.
     */
    fun forceUpdate(deltaSeconds: Float) {
        onUpdate(deltaSeconds)
    }

    // Using custom onUpdate function in order to make it testable.
    final override fun onUpdate(frameTime: FrameTime) {
        super.onUpdate(frameTime)
        this.onUpdate(frameTime.deltaSeconds)
    }

    /**
     * Applies the properties to the node
     * @param props properties to apply
     */
    protected open fun applyProperties(props: Bundle) {
        setLocalPosition(props)
        setLocalScale(props)
        setLocalRotation(props)
        setLocalTransform(props)
        setAlignment(props)
    }

    /**
     * If the node contains a renderable, it should be loaded
     * and assigned in this method
     */
    protected open fun loadRenderable() {}

    /**
     * Applies alignment to the node. Call it to notify that alignment
     * should be adjusted, e.g. after adding a child to a layout.
     *
     * This default implementation sets alignment using the [contentNode],
     * but when the node contains a [ViewRenderable] you can set the alignment
     * using [ViewRenderable.Builder] and override this method so that
     * it only reloads the view with the new alignment setting.
     */
    protected open fun applyAlignment() {
        val bounds = getBounding()

        val nodeWidth = bounds.right - bounds.left
        val nodeHeight = bounds.top - bounds.bottom
        val boundsCenterX = bounds.left + nodeWidth / 2
        val pivotOffsetX = localPosition.x - boundsCenterX // aligning according to center
        val boundsCenterY = bounds.top - nodeHeight / 2
        val pivotOffsetY = localPosition.y - boundsCenterY  // aligning according to center

        // calculating x position for content
        val x = when (horizontalAlignment) {
            Alignment.HorizontalAlignment.LEFT -> {
                contentNode.localPosition.x + nodeWidth / 2 + pivotOffsetX
            }

            Alignment.HorizontalAlignment.CENTER -> {
                contentNode.localPosition.x + pivotOffsetX
            }

            Alignment.HorizontalAlignment.RIGHT -> {
                contentNode.localPosition.x - nodeWidth / 2 + pivotOffsetX
            }
        }

        // calculating y position for content
        val y = when (verticalAlignment) {
            Alignment.VerticalAlignment.TOP -> {
                contentNode.localPosition.y - nodeHeight / 2 + pivotOffsetY
            }

            Alignment.VerticalAlignment.CENTER -> {
                contentNode.localPosition.y + pivotOffsetY
            }

            Alignment.VerticalAlignment.BOTTOM -> {
                contentNode.localPosition.y + nodeHeight / 2 + pivotOffsetY
            }
        }

        contentNode.localPosition = Vector3(x, y, contentNode.localPosition.z)
    }


    private fun setLocalPosition(props: Bundle) {
        val registeredParent = parent?.parent // first parent is a content node
        if (registeredParent is Layoutable) {
            // position is managed by a parent, so we should not change it
            return
        }

        val localPosition = props.read<Vector3>(PROP_LOCAL_POSITION)
        if (localPosition != null) {
            this.localPosition = localPosition
        }
    }

    protected open fun setLocalScale(props: Bundle) {
        val localScale = props.read<Vector3>(PROP_LOCAL_SCALE)
        if (localScale != null) {
            this.localScale = localScale
        }
    }

    private fun setLocalRotation(props: Bundle) {
        val quaternionData = props.getSerializable(PROP_LOCAL_ROTATION)
        if (quaternionData != null && quaternionData is ArrayList<*>) {
            quaternionData as ArrayList<Double>
            if (quaternionData.size == 4) {
                val x = quaternionData[0].toFloat()
                val y = quaternionData[1].toFloat()
                val z = quaternionData[2].toFloat()
                val w = quaternionData[3].toFloat()
                this.localRotation = Quaternion(x, y, z, w) // Quaternion.axisAngle
            }
        }
    }

    private fun setLocalTransform(props: Bundle) {
        val transformMatrix = props.read<Matrix>(PROP_LOCAL_TRANSFORM)
        if (transformMatrix != null) {
            val translation = Vector3()
            val scale = Vector3()
            val quaternion = Quaternion()
            val rotationVector = Vector3()

            transformMatrix.decomposeTranslation(translation)
            transformMatrix.decomposeScale(scale)
            transformMatrix.decomposeRotation(rotationVector, quaternion)

            this.localPosition = translation
            this.localScale = scale
            this.localRotation = quaternion
        }
    }

    protected open fun setAlignment(props: Bundle) {
        val alignment = props.read<Alignment>(PROP_ALIGNMENT)
        if (alignment != null) {
            verticalAlignment = alignment.vertical
            horizontalAlignment = alignment.horizontal
            if (useContentNodeAlignment) {
                applyAlignment()
            } else if (hasRenderable && renderableRequested) {
                applyAlignment()
            }
        }
    }
}