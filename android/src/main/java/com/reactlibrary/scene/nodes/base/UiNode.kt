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
import android.graphics.PointF
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
import com.reactlibrary.scene.ViewWrapper
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage
import com.reactlibrary.utils.putDefaultBoolean

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

        private const val REBUILD_CHECK_DELAY = 30L
        private const val COLLISION_SHAPE_DELAY = 120L
    }

    var clickListener: (() -> Unit)? = null

    /**
     * A view attached to the node
     */
    protected lateinit var view: View
    private lateinit var viewWrapper: ViewWrapper

    protected var validCollisionShape = false
        private set

    private var shouldRebuild = false
    private var loadingView = false

    private var rebuildLoopStarted = false
    private val handler = Handler(Looper.getMainLooper())

    init {
        // set default values of properties
        properties.putDefaultBoolean(PROP_ENABLED, true)
    }

    /**
     * Initializes the view instance and builds the node by calling [applyProperties]
     * with all initial properties
     */
    override fun build() {
        initView()
        setupView()
        applyProperties(properties)
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

    // Returning the bounds only after it's valid, because the ARCore calculates
    // collision shape "in steps" and at the beginning it's equal to 1m x 1m,
    // which is usually incorrect and cause layouts artifacts.
    override fun getContentBounding(): Bounding {
        val offsetX = contentNode.localPosition.x
        val offsetY = contentNode.localPosition.y

        val collShape = contentNode.collisionShape
        if (collShape is Box && validCollisionShape) {
            return Utils.calculateBoundsOfNode(contentNode)
        }
        return Bounding(offsetX, offsetY, offsetX, offsetY)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) //stop the loop
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

    protected abstract fun provideView(context: Context): View

    protected open fun onViewClick() {}

    /**
     * Should setup the [view] instance (size, listeners, etc) before it gets
     * attached to the node.
     */
    protected open fun setupView() {
        // default dimensions
        val widthPx = ViewGroup.LayoutParams.WRAP_CONTENT
        val heightPx = ViewGroup.LayoutParams.WRAP_CONTENT

        // the size should be set before attaching view to the node
        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

    override fun applyAlignment() {
        if (useContentNodeAlignment) {
            super.applyAlignment()
        } else {
            setNeedsRebuild() // need to re-attach the renderable
        }
    }

    private fun initView() {
        viewWrapper = ViewWrapper(context, this)
        this.view = provideView(context)
        this.view.setOnClickListener {
            onViewClick()
            clickListener?.invoke()
        }
        // build calls applyProperties, so we need to initialize the view before
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
                validCollisionShape = false
                contentNode.renderable = result.renderable
                view.postDelayed({
                    validCollisionShape = true
                }, COLLISION_SHAPE_DELAY)
            }
            loadingView = false
        }
    }

    // Translation to native View coordinate system.
    override fun getScrollTranslation(): PointF {
        val size = getBounding().size()
        val pivot = PointF(
                size.x / 2F,
                size.y / -2F)
        return PointF(
                pivot.x - localPosition.x,
                pivot.y - localPosition.y)
    }

    override fun setClipBounds(clipBounds: Bounding) {
        val clipBoundsPx = Rect(
                Utils.metersToPx(clipBounds.left + getScrollTranslation().x, context),
                -Utils.metersToPx(clipBounds.top + getScrollTranslation().y, context),
                Utils.metersToPx(clipBounds.right + getScrollTranslation().x, context),
                -Utils.metersToPx(clipBounds.bottom + getScrollTranslation().y, context))
        view.clipBounds = clipBoundsPx
    }

    /**
     * Using a handler loop instead of onUpdate to allow for node rebuild,
     * even if it's not attached to the scene yet (e.g. dropdown list items
     * that are attached only after click).
     */
    private fun rebuildLoop() {
        if (shouldRebuild && !loadingView) {
            if (renderableRequested) {
                // init a new view and apply all properties
                build()
                attachView()
            } else {
                // not reloading the view if the renderable has not been requested yet
                // because ArCore may not be initialized yet
                setupView()
                applyProperties(properties)
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
}
