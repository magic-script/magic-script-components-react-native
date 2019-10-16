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
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.ar.RenderableResult
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.ViewWrapper
import com.reactlibrary.scene.nodes.UiImageNode
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.utils.*

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
    }

    var clickListener: (() -> Unit)? = null

    /**
     * A view attached to the node
     */
    protected lateinit var view: View
    private lateinit var viewWrapper: ViewWrapper

    private var shouldRebuild = false
    private var loadingView = false

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
        addChild(contentNode)
        applyProperties(properties)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setEnabled(props)
    }

    override fun loadRenderable() {
        attachView()
    }

    override fun onUpdate(frameTime: FrameTime) {
        super.onUpdate(frameTime)
        if (shouldRebuild && !loadingView) {
            build() // init a new view and apply all properties
            attachView()
            shouldRebuild = false
            logMessage("node rebuild, hash:{${this.hashCode()}}")
        }
    }

    /**
     * Should be called when the size of the node may have changed,
     * so we need to rebuild the native view (renderable)
     * (resizing the current view does not work - ARCore bug?)
     */
    fun setNeedsRebuild() {
        // no rebuilding if the renderable has not been requested yet
        // because ArCore may not be initialized yet
        if (renderableRequested) {
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
                contentNode.renderable = result.renderable
                loadingView = false
            } else {
                loadingView = false
            }
        }

    }

    override fun setClipBounds(clipBounds: RectF) {
        val pivot = getBounding().size() / 2F
        val clipBoundsPx = Rect(
                Utils.metersToPx(clipBounds.left + pivot.x, context),
                -Utils.metersToPx(clipBounds.top - pivot.y, context),
                Utils.metersToPx(clipBounds.right + pivot.x, context),
                -Utils.metersToPx(clipBounds.bottom - pivot.y, context))
        view.clipBounds = clipBoundsPx
    }

    private fun setEnabled(props: Bundle) {
        if (props.containsKey(PROP_ENABLED)) {
            view.isEnabled = props.getBoolean(PROP_ENABLED)
        }
    }
}
