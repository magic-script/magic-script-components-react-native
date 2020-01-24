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

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.magicleap.magicscript.ar.RenderableResult
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.views.ViewWrapper
import com.magicleap.magicscript.utils.*
import com.magicleap.magicscript.utils.Utils.Companion.metersToPx
import kotlin.math.max
import kotlin.math.min

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
        const val LONG_PRESS_TIME = 0.5f // in seconds
        private const val REBUILD_CHECK_DELAY = 30L
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

    private lateinit var viewWrapper: ViewWrapper
    private val handler = Handler(Looper.getMainLooper())
    private var shouldRebuild = false
    private var loadingView = false
    private var rebuildLoopStarted = false
    private var renderableCopy: Renderable? = null

    private var touching = false
    private var touchTime = 0f

    /**
     * Desired node width and height in meters or equal to [WRAP_CONTENT_DIMENSION]
     * A dimension equal to [WRAP_CONTENT_DIMENSION] means unspecified size that can grow.
     */
    private var desiredSize = Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)

    /**
     * Set default clipping (all renderable visible).
     * Values are relative to model width and height. Origin (0, 0) is at bottom-center.
     */
    private var materialClipBounding = Bounding(-0.5f, 0.0f, 0.5f, 1.0f)

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

    /*
     * Setting clip bounds at a shader level (shader is contained inside material
     * from which android_view.sfb is built)
     *
     * https://google.github.io/filament/Materials.md.html
     * By default getPosition() returns NDC - Normalized Device Coordinates;
     * for "vertexDomain" : object it means that coordinates are normalized relative
     * to a model size (0 - 1), origin (0, 0) is at bottom-center.
     */
    override fun setClipBounds(clipBounds: Bounding) {
        // clipping a texture
        materialClipBounding = Utils.calculateMaterialClipping(clipBounds, getBounding())
        applyMaterialClipping()

        // clipping collision shape (regarding click events)
        val pivotCenterOffset = Vector2(
            -horizontalAlignment.centerOffset * size.x,
            -verticalAlignment.centerOffset * size.y
        )

        val nodeCollisionShape = Bounding(
            -size.x / 2 * localScale.x,
            -size.y / 2 * localScale.y,
            size.x / 2 * localScale.x,
            size.y / 2 * localScale.y
        ).translate(pivotCenterOffset)

        val clipCollisionShape = clipBounds.translate(-getContentPosition())

        var intersection = Bounding(
            max(nodeCollisionShape.left, clipCollisionShape.left),
            max(nodeCollisionShape.bottom, clipCollisionShape.bottom),
            min(nodeCollisionShape.right, clipCollisionShape.right),
            min(nodeCollisionShape.top, clipCollisionShape.top)
        )
        if (intersection.left > intersection.right || intersection.bottom > intersection.top) {
            intersection = Bounding()
        }

        // collision shape is not aware of scale, we need to scale to original position
        val sizeX = if (localScale.x > 0) intersection.size().x / localScale.x else 0F
        val sizeY = if (localScale.y > 0) intersection.size().y / localScale.y else 0F
        val collisionShapeSize = Vector3(sizeX, sizeY, 0F)

        val centerX = if (localScale.x > 0) intersection.center().x / localScale.x else 0F
        val centerY = if (localScale.y > 0) intersection.center().y / localScale.y else 0F
        val collisionShapeCenter = Vector3(centerX, centerY, 0F)

        val collisionShape = Box(collisionShapeSize, collisionShapeCenter)

        contentNode.collisionShape = collisionShape
    }

    override fun onVisibilityChanged(visibility: Boolean) {
        super.onVisibilityChanged(visibility)
        if (visibility) {
            contentNode.renderable = renderableCopy
            applyMaterialClipping()
        } else {
            contentNode.renderable = null
        }
    }

    override fun onUpdate(deltaSeconds: Float) {
        super.onUpdate(deltaSeconds)
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
        applyMaterialClipping()
    }

    override fun applyAlignment() {
        if (useContentNodeAlignment) {
            super.applyAlignment()
        } else {
            setNeedsRebuild() // need to re-attach the renderable
        }
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

        viewWrapper.addView(view)

        val alignHorizontal =
            if (useContentNodeAlignment) Alignment.HorizontalAlignment.CENTER else horizontalAlignment
        val alignVertical =
            if (useContentNodeAlignment) Alignment.VerticalAlignment.CENTER else verticalAlignment
        val config = ViewRenderableLoader.Config(
            view = viewWrapper,
            horizontalAlignment = alignHorizontal,
            verticalAlignment = alignVertical
        )
        viewRenderableLoader.loadRenderable(config) { result ->
            loadingView = false
            if (result is RenderableResult.Success) {
                onViewLoaded(result.renderable)
            }
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

    private fun applyMaterialClipping() {
        contentNode.renderable?.material?.let { material ->
            Utils.applyMaterialClipping(material, materialClipBounding)
        }
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
}
