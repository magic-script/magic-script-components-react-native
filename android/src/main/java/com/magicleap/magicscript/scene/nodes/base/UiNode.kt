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

package com.magicleap.magicscript.scene.nodes.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.views.ViewWrapper
import com.magicleap.magicscript.utils.*
import com.magicleap.magicscript.utils.Utils.Companion.metersToPx

/**
 * Base node that represents UI controls that contain a native Android view [ViewRenderable]
 */
abstract class UiNode(
    initProps: ReadableMap,
    protected val context: Context,
    private val viewRenderableLoader: ViewRenderableLoader,
    private val nodeClipper: Clipper,
    useContentNodeAlignment: Boolean = false
) : TransformNode(initProps, useContentNodeAlignment) {

    companion object {
        // properties
        const val PROP_ENABLED = "enabled"

        const val WRAP_CONTENT_DIMENSION = 0.0F // width or height that grow to fit content
        const val LONG_PRESS_TIME = 0.5f // in seconds
    }

    /**
     * Node width and height based on [view] size (in meters).
     * It does not include the scale.
     *
     * Note that the size is known only after the node is built
     * (after all properties have been applied)
     */
    var size = Vector2(0F, 0F)
        private set

    var onClickListener: (() -> Unit)? = null
    var onPressListener: (() -> Unit)? = null
    var onLongPressListener: (() -> Unit)? = null
    var onReleaseListener: (() -> Unit)? = null
    var onFocusGainedListener: (() -> Unit)? = null
    var onFocusLostListener: (() -> Unit)? = null
    var onEnabledListener: (() -> Unit)? = null
    var onDisabledListener: (() -> Unit)? = null

    /**
     * A view attached to the node
     */
    protected lateinit var view: View

    override var clipBounds: AABB?
        get() = super.clipBounds
        set(value) {
            super.clipBounds = value
            applyClipBounds()
        }

    private lateinit var viewWrapper: ViewWrapper
    private val handler = Handler(Looper.getMainLooper())
    private var shouldRebuild = false
    private var loadingView = false
    private var renderableCopy: Renderable? = null

    private var touching = false
    private var touchTime = 0f

    private var renderableLoadRequest: ViewRenderableLoader.LoadRequest? = null

    /**
     * Desired node width and height in meters or equal to [WRAP_CONTENT_DIMENSION]
     * A dimension equal to [WRAP_CONTENT_DIMENSION] means unspecified size that can grow.
     */
    private var desiredSize = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)

    init {
        // set default values of properties
        properties.putDefault(PROP_ENABLED, true)
    }

    /**
     * Should be called when the size of the node may have changed,
     * so we need to rebuild the native view (renderable)
     *
     * @param force whether to force the rebuild (set this to true when rebuild is required
     * beyond the [applyProperties] execution).
     * Use this flag with caution in particular during [applyProperties] execution
     * (the rebuild may loop)
     */
    fun setNeedsRebuild(force: Boolean = false) {
        if (updatingProperties || force) {
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
        attachView()

        if (useContentNodeAlignment) {
            applyAlignment()
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setEnabled(props)
    }

    /**
     * Should return the node bounds based on a measured native view size.
     *
     * Not using the collision shape based size because ARCore calculates it "in steps",
     * so at the beginning it's equal to 1m x 1m, which is usually incorrect and cause
     * layouts artifacts
     */
    override fun getContentBounding(): AABB {
        val centerX = contentNode.localPosition.x
        val centerY = contentNode.localPosition.y

        val scaleX = contentNode.localScale.x
        val scaleY = contentNode.localScale.y

        val offsetX = if (useContentNodeAlignment) {
            0f
        } else {
            -horizontalAlignment.centerOffset * size.x
        }

        val offsetY = if (useContentNodeAlignment) {
            0f
        } else {
            -verticalAlignment.centerOffset * size.y
        }

        val xMin = centerX * scaleX - (size.x * scaleX) / 2 + offsetX
        val xMax = centerX * scaleX + (size.x * scaleX) / 2 + offsetX
        val yMin = centerY * scaleY - (size.y * scaleY) / 2 + offsetY
        val yMax = centerY * scaleY + (size.y * scaleY) / 2 + offsetY

        return AABB(min = Vector3(xMin, yMin, 0f), max = Vector3(xMax, yMax, 0f))
    }

    override fun onVisibilityChanged(visibility: Boolean) {
        super.onVisibilityChanged(visibility)
        if (visibility) {
            contentNode.renderable = renderableCopy
            applyClipBounds()
        } else {
            contentNode.renderable = null
            contentNode.collisionShape = null
        }
    }

    override fun onUpdate(deltaSeconds: Float) {
        super.onUpdate(deltaSeconds)

        if (shouldRebuild && !loadingView) {
            build()
            shouldRebuild = false
            logMessage("node rebuild, hash:{${this.hashCode()}}")
        }

        if (touching) {
            touchTime += deltaSeconds
            if (touchTime >= LONG_PRESS_TIME) {
                onLongPressListener?.invoke()
                touchTime = 0f
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) //stop the loop
        renderableLoadRequest?.let {
            viewRenderableLoader.cancel(it)
        }
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
            metersToPx(width, context)
        }

        val heightPx = if (height == WRAP_CONTENT_DIMENSION) {
            ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            metersToPx(height, context)
        }
        // we have to set layout params before attaching view to the node
        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)

        setupViewListeners()
    }

    protected open fun onViewLoaded(viewRenderable: Renderable) {
        if (isVisible) {
            contentNode.renderable = viewRenderable
        }
        renderableCopy = viewRenderable
        applyClipBounds()
    }

    override fun applyAlignment() {
        if (useContentNodeAlignment) {
            Utils.applyContentNodeAlignment(this)
        } else {
            setNeedsRebuild() // need to re-attach the renderable
        }
    }

    override fun onTransformedLocally() {
        super.onTransformedLocally()
        applyClipBounds()
    }

    // build calls applyProperties, so we need to initialize the view before
    private fun initView() {
        viewWrapper = ViewWrapper(context, this)
        this.view = provideView(context)
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

        val viewParent = view.parent
        if (viewParent is ViewGroup) {
            viewParent.removeView(view)
        }
        viewWrapper.addView(view)

        // cancel previous load request if exists
        this.renderableLoadRequest?.let {
            viewRenderableLoader.cancel(it)
        }

        val alignHorizontal =
            if (useContentNodeAlignment) Alignment.Horizontal.CENTER else horizontalAlignment
        val alignVertical =
            if (useContentNodeAlignment) Alignment.Vertical.CENTER else verticalAlignment

        this.renderableLoadRequest = ViewRenderableLoader.LoadRequest(
            view = viewWrapper,
            horizontalAlignment = alignHorizontal,
            verticalAlignment = alignVertical
        ) { result ->
            loadingView = false
            if (result is DataResult.Success) {
                onViewLoaded(result.data)
            }
        }.also {
            viewRenderableLoader.loadRenderable(it)
        }
    }

    private fun applyClipBounds() {
        nodeClipper.applyClipBounds(this, clipBounds)
    }

    private fun setupViewListeners() {
        if (view is AdapterView<*>) {
            return
        }
        view.setOnClickListener {
            onViewClick()
            onClickListener?.invoke()
        }

        view.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    touching = true
                    if (onPressListener == null) {
                        return@setOnTouchListener false
                    }
                    onPressListener?.invoke()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    touching = false
                    touchTime = 0f
                    if (onReleaseListener == null) {
                        return@setOnTouchListener false
                    }
                    onReleaseListener?.invoke()
                    true
                }
                else -> false
            }
        }
    }

    private fun setEnabled(props: Bundle) {
        if (props.containsKey(PROP_ENABLED)) {
            val enabled = props.getBoolean(PROP_ENABLED)
            view.isEnabled = enabled
            if (enabled) {
                onEnabledListener?.invoke()
            } else {
                onDisabledListener?.invoke()
            }
        }
    }

    class Test(val uiNode: UiNode) {
        fun performClick() {
            uiNode.onViewClick()
        }
    }

}
