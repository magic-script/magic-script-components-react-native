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
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
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
    val useContentNodeAlignment: Boolean
) : TransformAwareNode() {

    companion object {

        const val PROP_LOCAL_POSITION = "localPosition"
        const val PROP_LOCAL_SCALE = "localScale"
        const val PROP_LOCAL_ROTATION = "localRotation"
        const val PROP_LOCAL_TRANSFORM = "localTransform"
        const val PROP_ALIGNMENT = "alignment"
        const val PROP_VISIBLE = "visible"
        const val PROP_ANCHOR_UUID = "anchorUuid"

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
    val contentNode = TransformAwareNode()

    var anchorUuid: String = ""
        private set

    /**
     * Alignment used to position a Renderable or [contentNode]
     */
    var horizontalAlignment: Alignment.Horizontal = Alignment.Horizontal.CENTER
        private set

    var verticalAlignment: Alignment.Vertical = Alignment.Vertical.CENTER
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

    /**
     *  If set, the node should hide this part of itself that is outside [clipBounds]
     */
    open var clipBounds: AABB? = null
        set(value) {
            field = value
            clipChildren()
        }

    var isVisible: Boolean by Delegates.observable(true) { prop, old, new ->
        onVisibilityChanged(new)
        visibilityListeners.forEach { it.invoke(new) }
    }

    protected var updatingProperties = false
        private set

    private var bounding = AABB() // default

    private var timeSinceLastAlignment = 0F

    private var visibilityListeners = mutableListOf<VisibilityChangedListener>()

    init {
        addChild(contentNode)
        contentNode.addOnLocalTransformChangedListener(object : LocalTransformListener {
            override fun onTransformed() {
                onTransformedLocally()
            }
        })
        logMessage("initial properties = ${this.properties}")
    }

    /**
     * Builds the node by calling [applyProperties] with all initial properties
     */
    open fun build() {
        applyProperties(properties)
        if (useContentNodeAlignment) {
            applyAlignment()
        }
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
     * To manage alignment correctly, we should use this method instead of [Node.addChild]
     */
    open fun addContent(child: TransformNode) {
        if (!isVisible) {
            child.hide()
        }
        contentNode.addChild(child)
        clipChildren()
    }

    open fun removeContent(child: TransformNode) {
        contentNode.removeChild(child)
    }

    /**
     * Called when local position, scale or rotation has been set
     * for the node or for [contentNode]
     */
    open fun onTransformedLocally() {
        clipChildren()
    }

    /**
     * Returns 2D bounding of the node (the minimum rectangle
     * that includes the node).
     */
    fun getBounding(): AABB {
        val contentBounds = getContentBounding()

        val minEdgeRotated = contentBounds.min.rotatedBy(localRotation)
        val maxEdgeRotated = contentBounds.max.rotatedBy(localRotation)

        val minimumBounds = Utils.findMinimumBounding(listOf(minEdgeRotated, maxEdgeRotated))

        val minEdge = Vector3(
            minimumBounds.min.x * localScale.x + localPosition.x,
            minimumBounds.min.y * localScale.y + localPosition.y,
            minimumBounds.min.z * localScale.z + localPosition.z
        )

        val maxEdge = Vector3(
            minimumBounds.max.x * localScale.x + localPosition.x,
            minimumBounds.max.y * localScale.y + localPosition.y,
            minimumBounds.max.z * localScale.z + localPosition.z
        )

        return AABB(minEdge, maxEdge)
    }

    /**
     * Should return 2D bounding of the [contentNode] (relative to
     * the parent node position).
     */
    open fun getContentBounding(): AABB {
        return Utils.calculateBoundsOfNode(contentNode, contentNode.collisionShape)
    }

    /**
     * Should return position of the content (relative to this node's parent)
     */
    fun getContentPosition(): Vector3 {
        return localPosition + contentNode.localPosition
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
            if (!currentBounding.equalInexact(bounding)) {
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

    fun hide() {
        isVisible = false
    }

    fun show() {
        isVisible = true
    }

    fun addVisibilityListener(listener: VisibilityChangedListener) {
        visibilityListeners.add(listener)
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

    // Using custom onUpdate function in order to make it testable.
    final override fun onUpdate(frameTime: FrameTime) {
        super.onUpdate(frameTime)
        this.onUpdate(frameTime.deltaSeconds)
    }

    final override fun onLocalTransformChanged() {
        super.onLocalTransformChanged()
        onTransformedLocally()
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
        setVisibility(props)
        setAnchorUuid(props)
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
        Utils.applyContentNodeAlignment(this)
    }

    private fun setAnchorUuid(props: Bundle) {
        val anchorUuid = props.read<String>(PROP_ANCHOR_UUID)
        if (anchorUuid != null) {
            this.anchorUuid = anchorUuid
        }
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

    private fun setVisibility(props: Bundle) {
        val visible = props.read<Boolean>(PROP_VISIBLE) ?: return
        this.isVisible = visible
    }

    protected open fun clipChildren() {
        clipBounds?.let { Utils.clipChildren(this, it) }
    }

    interface VisibilityChangedListener {
        fun invoke(visible: Boolean)
    }

    class Test(val node: TransformNode) {
        /**
         * Forces node update. For tests purposes.
         *
         * @param deltaSeconds seconds elapsed since last update
         * You can pass any value to simulate time elapsed since the last frame
         * and test behavior of ARCore's [Node.onUpdate] dependent code.
         */
        fun forceUpdate(deltaSeconds: Float) {
            node.onUpdate(deltaSeconds)
        }
    }

}