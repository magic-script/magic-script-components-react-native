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
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.R
import com.reactlibrary.utils.logMessage

/**
 * Base node that represents UI controls that contain a native Android view [ViewRenderable]
 */
abstract class UiNode(
        initProps: ReadableMap,
        protected val context: Context,
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

    private var shouldRebuild = false
    private var loadingView = false

    /**
     * Initializes the view instance and builds the node by calling [applyProperties]
     * with all initial properties
     */
    override fun build() {
        initView()
        addChild(contentNode)
        applyProperties(properties)
        setViewSize()
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

    protected open fun setViewSize() {
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
        this.view = provideView(context)
        this.view.setOnClickListener {
            onViewClick()
            clickListener?.invoke()
        }
        // build calls applyProperties, so we need to initialize the view before
    }

    private fun attachView() {
        loadingView = true
        val builder = ViewRenderable
                .builder()
                .setSource(context, R.raw.android_view) // using custom material to disable back side
                .setView(context, view)

        if (useContentNodeAlignment) { // use default renderable alignment
            builder.setHorizontalAlignment(ViewRenderable.HorizontalAlignment.CENTER)
            builder.setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
        } else {
            val horizontalAlign = ViewRenderable.HorizontalAlignment.valueOf(horizontalAlignment.name)
            val verticalAlign = ViewRenderable.VerticalAlignment.valueOf(verticalAlignment.name)
            builder.setHorizontalAlignment(horizontalAlign)
            builder.setVerticalAlignment(verticalAlign)
        }

        builder.build()
                .thenAccept { renderable ->
                    renderable.isShadowReceiver = false
                    renderable.isShadowCaster = false
                    contentNode.renderable = renderable
                    loadingView = false
                }
                .exceptionally { throwable ->
                    loadingView = false
                    logMessage("error loading ViewRenderable: $throwable")
                    null
                }
    }

    private fun setEnabled(props: Bundle) {
        if (props.containsKey(PROP_ENABLED)) {
            view.isEnabled = props.getBoolean(PROP_ENABLED)
        }
    }

}