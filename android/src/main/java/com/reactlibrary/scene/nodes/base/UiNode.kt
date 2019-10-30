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

package com.reactlibrary.scene.nodes.base

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.ar.RenderableResult
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.views.ViewWrapper
import com.reactlibrary.utils.*
import java.lang.Float.max
import java.lang.Float.min

/**
 * Base node that represents UI controls that contain a native Android view [ViewRenderable]
 */
abstract class UiNode(
        initProps: ReadableMap,
        protected val context: Context,
        private val viewRenderableLoader: ViewRenderableLoader,
        useContentNodeAlignment: Boolean = false
) : TransformNode(initProps, true, useContentNodeAlignment) {

    companion object {
        // properties
        const val PROP_ENABLED = "enabled"

        const val WRAP_CONTENT_DIMENSION = 0.0F // width or height that grow to fit content
        private const val REBUILD_CHECK_DELAY = 30L
    }

    /**
     * Node width and height based on [view] size (in meters)
     *
     * Note that the size is known only after the node is built
     * (after all properties have been applied)
     */
    var size = Vector2(0F, 0F)
        private set

    var clickListener: (() -> Unit)? = null

    /**
     * A view attached to the node
     */
    protected lateinit var view: View
    private lateinit var viewWrapper: ViewWrapper

    private val handler = Handler(Looper.getMainLooper())
    private var shouldRebuild = false
    private var loadingView = false
    private var rebuildLoopStarted = false

    /**
     * Desired node width and height in meters or equal to [WRAP_CONTENT_DIMENSION]
     * A dimension equal to [WRAP_CONTENT_DIMENSION] means unspecified size that can grow.
     */
    private var desiredSize = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)

    init {
        // set default values of properties
        properties.putDefaultBoolean(PROP_ENABLED, true)
    }

    /**
     * Should be called when the size of the node may have changed,
     * so we need to rebuild the native view (renderable)
     * (resizing the current view does not work - ARCore bug?)
     */
    fun setNeedsRebuild() {
        if (updatingProperties) {
            shouldRebuild = true
        }
    }

    open fun disallowInterceptTouchEvent(): Boolean {
        return false
    }

    /**
     * Initializes the view instance and builds the node by calling [applyProperties]
     * with all initial properties
     */
    override fun build() {
        initView()
        setup()
        if (!rebuildLoopStarted) {
            rebuildLoop()
            rebuildLoopStarted = true
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setEnabled(props)
    }

    override fun loadRenderable() {
        attachView()
    }

    /**
     * Should return the node bounds based on a measured native view size.
     *
     * Not using the collision shape based size because ARCore calculates it "in steps",
     * so at the beginning it's equal to 1m x 1m, which is usually incorrect and cause
     * layouts artifacts
     */
    override fun getContentBounding(): Bounding {
        val centerX = contentNode.localPosition.x
        val centerY = contentNode.localPosition.y

        val scaleX = contentNode.localScale.x
        val scaleY = contentNode.localScale.y

        val offsetX = -horizontalAlignment.centerOffset * size.x
        val offsetY = -verticalAlignment.centerOffset * size.y

        val left = centerX * scaleX - (size.x * scaleX) / 2 + offsetX
        val right = centerX * scaleX + (size.x * scaleX) / 2 + offsetX
        val top = centerY * scaleY + (size.y * scaleY) / 2 + offsetY
        val bottom = centerY * scaleY - (size.y * scaleY) / 2 + offsetY
        return Bounding(left, bottom, right, top)
    }

    /**
     * Translation to native view local coordinate system.
     */
    override fun getScrollTranslation(): Vector2 {
        val pivot = getPivot()
        return Vector2(
                pivot.x - localPosition.x - contentNode.localPosition.x,
                -pivot.y - localPosition.y - contentNode.localPosition.y)
    }

    override fun setClipBounds(clipBounds: Bounding, clipNativeView: Boolean) {
        if (!clipNativeView) {
            return
        }

        // Clipping view.
        val localBounds = clipBounds.translate(getScrollTranslation())
        view.clipBounds = Rect(
                metersToPx(localBounds.left),
                -metersToPx(localBounds.top),
                metersToPx(localBounds.right),
                -metersToPx(localBounds.bottom))

        // Clipping content node collision shape.
        val contentNodePosition = Vector2(
                -localPosition.x - contentNode.localPosition.x,
                -localPosition.y - contentNode.localPosition.y
        )

        val nodeCollisionShape = Bounding(0F, 0F, size.x, size.y)
                .translate(-getPivot())
        val clipCollisionShape = clipBounds
                .translate(contentNodePosition)

        var intersection = Bounding(
                max(nodeCollisionShape.left, clipCollisionShape.left),
                max(nodeCollisionShape.bottom, clipCollisionShape.bottom),
                min(nodeCollisionShape.right, clipCollisionShape.right),
                min(nodeCollisionShape.top, clipCollisionShape.top)
        )
        if (intersection.left > intersection.right || intersection.bottom > intersection.top) {
            intersection = Bounding()
        }

        contentNode.collisionShape = Box(
                intersection.size().toVector3(),
                intersection.center().toVector3())
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) //stop the loop
    }

    protected abstract fun provideView(context: Context): View

    protected abstract fun provideDesiredSize(): Vector2

    protected open fun onViewClick() {}

    /**
     * Should setup the [view] instance (e.g. register listeners) before it gets
     * attached to the node.
     */
    protected open fun setupView() {
        desiredSize = provideDesiredSize()
        val width = desiredSize.x
        val height = desiredSize.y

        val widthPx = if (width == WRAP_CONTENT_DIMENSION) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            Utils.metersToPx(width, context)
        }

        val heightPx = if (height == WRAP_CONTENT_DIMENSION) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            Utils.metersToPx(height, context)
        }
        // we have to set layout params before attaching view to the node
        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

    override fun applyAlignment() {
        if (useContentNodeAlignment) {
            super.applyAlignment()
        } else {
            setNeedsRebuild() // need to re-attach the renderable
        }
    }

    protected fun metersToPx(meters: Float): Int {
        return Utils.metersToPx(meters, context)
    }

    // build calls applyProperties, so we need to initialize the view before
    private fun initView() {
        viewWrapper = ViewWrapper(context, this)
        this.view = provideView(context)
        this.view.setOnClickListener {
            onViewClick()
            clickListener?.invoke()
        }
    }

    private fun setup() {
        setupView()
        applyProperties(properties)
        // calculating the real size after all properties have been set,
        // because some properties may have changed the view size
        // when using wrap content
        val maxWidth = desiredSize.x
        val maxHeight = desiredSize.y
        size = view.getSizeInMeters(context, maxWidth, maxHeight)
    }

    private fun attachView() {
        loadingView = true

        viewWrapper.addView(view)

        val alignHorizontal = if (useContentNodeAlignment) Alignment.HorizontalAlignment.CENTER else horizontalAlignment
        val alignVertical = if (useContentNodeAlignment) Alignment.VerticalAlignment.CENTER else verticalAlignment
        val config = ViewRenderableLoader.Config(
                view = viewWrapper,
                horizontalAlignment = alignHorizontal,
                verticalAlignment = alignVertical
        )
        viewRenderableLoader.loadRenderable(config) { result ->
            if (result is RenderableResult.Success) {
                contentNode.renderable = result.renderable
            }
            loadingView = false
        }
    }

    /**
     * Using a handler loop instead of onUpdate to allow for node rebuild,
     * even if it's not attached to the scene yet (e.g. dropdown list items
     * that are attached only after click).
     */
    private fun rebuildLoop() {
        if (shouldRebuild && !loadingView) {
            if (renderableRequested) {
                // init a new view, apply all properties and re-attach the view
                build()
                attachView()
            } else {
                // only setup, because the view has not been attached yet
                setup()
            }
            shouldRebuild = false
            logMessage("node rebuild, hash:{${this.hashCode()}}")
        }

        handler.postDelayed({ rebuildLoop() }, REBUILD_CHECK_DELAY)
    }

    private fun setEnabled(props: Bundle) {
        if (props.containsKey(PROP_ENABLED)) {
            view.isEnabled = props.getBoolean(PROP_ENABLED)
        }
    }

    private fun getPivot(): Vector2 {
        return Vector2(
                size.x * (0.5F + horizontalAlignment.centerOffset),
                size.y * (0.5F - verticalAlignment.centerOffset))
    }
}
