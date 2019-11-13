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

package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.views.CustomScrollView
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils.Companion.metersToPx
import com.reactlibrary.utils.Utils.Companion.pxToMeters
import com.reactlibrary.utils.Vector2
import com.reactlibrary.utils.putDefaultString
import kotlin.math.max
import kotlin.math.min

open class UiScrollViewNode(
        initProps: ReadableMap,
        context: Context,
        viewRenderableLoader: ViewRenderableLoader) :
        UiNode(initProps, context, viewRenderableLoader, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_SCROLL_BOUNDS = "scrollBounds"
        const val PROP_SCROLL_DIRECTION = "scrollDirection"

        const val DEFAULT_WIDTH = 1.0F
        const val DEFAULT_HEIGHT = 1.0F
        const val DEFAULT_SCROLL_DIRECTION = "vertical"

        const val BAR_THICKNESS_RATIO = 0.03F // relative to min (width, height)
        const val BAR_MINIMUM_THICKNESS = 0.05F // in meters
        const val HIDDEN_BAR_THICKNESS = 0

        const val LAYOUT_LOOP_DELAY = 50L
        const val Z_ORDER_OFFSET = 1e-5F
    }

    var onScrollChangeListener: ((position: Vector2) -> Unit)? = null

    protected var onContentSizeChangedListener: ((contentSize: Vector2) -> Unit)? = null

    protected var vBarNode: UiScrollBarNode? = null
        private set

    protected var hBarNode: UiScrollBarNode? = null
        private set

    private var content: TransformNode? = null
    private var contentBounds = Bounding()

    private var requestedContentPosition = Vector2()
    private var looperHandler = Handler(Looper.getMainLooper())

    init {
        properties.putDefaultString(PROP_SCROLL_DIRECTION, DEFAULT_SCROLL_DIRECTION)
        layoutLoop()
    }

    // Function by which ViewWrapper delivers intercepted motion events.
    fun onTouchEvent(event: MotionEvent): Boolean {
        val viewBounds = getScrollBounds()
        event.setLocation(
                metersToPx(event.x - viewBounds.left, context).toFloat(),
                metersToPx(-event.y + viewBounds.top, context).toFloat())
        return view.onTouchEvent(event)
    }

    fun stopNestedScroll() {
        view.stopNestedScroll()
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.scroll_view, null)
    }

    override fun provideDesiredSize(): Vector2 {
        val propBounds = PropertiesReader.readAABB(properties, PROP_SCROLL_BOUNDS)
        return if (propBounds != null) {
            Vector2(propBounds.getWidth(), propBounds.getHeight())
        } else {
            Vector2(DEFAULT_WIDTH, DEFAULT_HEIGHT)
        }
    }

    override fun setupView() {
        super.setupView()

        if (vBarNode != null) {
            val thickness = calculateBarThickness(size)
            val thicknessPx = metersToPx(thickness, context)
            (view as CustomScrollView).vBar?.setThickness(thicknessPx)
        }

        if (hBarNode != null) {
            val thickness = calculateBarThickness(size)
            val thicknessPx = metersToPx(thickness, context)
            (view as CustomScrollView).hBar?.setThickness(thicknessPx)
        }

        val scrollView = view as CustomScrollView
        scrollView.onScrollChangeListener = { position: Vector2 ->
            update(position)
            this.onScrollChangeListener?.invoke(position)
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_SCROLL_BOUNDS)) {
            setNeedsRebuild()
        }

        setScrollDirection(props)
    }

    override fun addContent(child: Node) {
        if (child is UiScrollBarNode) {
            addScrollBar(child)
            return
        }

        if (content != null) {
            return
        }
        super.addContent(child)

        if (child !is TransformNode) {
            return
        }
        this.content = child
    }

    override fun removeContent(child: Node) {
        super.removeContent(child)
        when (child) {
            content -> content = null
            hBarNode -> {
                (view as CustomScrollView).hBar?.setThickness(HIDDEN_BAR_THICKNESS)
                hBarNode = null
            }
            vBarNode -> {
                (view as CustomScrollView).vBar?.setThickness(HIDDEN_BAR_THICKNESS)
                vBarNode = null
            }
        }
    }

    override fun getContentPosition(): Vector2 {
        // When translating clip bounds to node's local coordinate
        // system we use localPositions. However due to ARCore nature
        // localPosition of ScrollViewNode direct descendant can change
        // randomly for some milliseconds after being written to. Thus
        // we can't reliably use it. To solve the problem we add child
        // localPosition to translation - the child node will subtract
        // it in it's setClipBounds method, effectively zeroing it.
        // Then, instead of zeroed child localPosition we use
        // requestedContentPosition.
        val position = content?.localPosition ?: Vector3()
        return Vector2(
                requestedContentPosition.x - position.x,
                requestedContentPosition.y - position.y
        )
    }

    override fun setAlignment(props: Bundle) {
        // Alignment cannot be changed for scroll view according to Lumin.
        // It is hardcoded to center-center
    }

    override fun onDestroy() {
        super.onDestroy()
        // stop the layout loop
        looperHandler.removeCallbacksAndMessages(null)
    }

    protected fun calculateBarThickness(containerSize: Vector2): Float {
        val parentBasedThickness = min(containerSize.x, containerSize.y) * BAR_THICKNESS_RATIO
        return max(parentBasedThickness, BAR_MINIMUM_THICKNESS)
    }

    private fun addScrollBar(scrollBarNode: UiScrollBarNode) {
        val thickness = calculateBarThickness(size)
        val thicknessPx = metersToPx(thickness, context)

        if (scrollBarNode.orientation == UiScrollBarNode.ORIENTATION_VERTICAL) {
            if (vBarNode == null) {
                super.addContent(scrollBarNode)
                vBarNode = scrollBarNode
                (view as CustomScrollView).vBar?.setThickness(thicknessPx)
            }
        } else {
            if (hBarNode == null) {
                super.addContent(scrollBarNode)
                hBarNode = scrollBarNode
                (view as CustomScrollView).hBar?.setThickness(thicknessPx)
            }
        }
    }

    private fun layoutLoop() {
        looperHandler.postDelayed({
            content?.let { it ->
                // check if content bounds changed
                val newContentBounds = it.getContentBounding()
                if (!Bounding.equalInexact(contentBounds, newContentBounds)) {
                    this.contentBounds = newContentBounds
                    onContentSizeChangedListener?.invoke(newContentBounds.size())
                }
                layout()
            }
            layoutLoop()
        }, LAYOUT_LOOP_DELAY)
    }

    private fun layout() {
        val scrollView = (view as CustomScrollView)
        val contentSize = contentBounds.size()
        scrollView.contentSize = Vector2(
                metersToPx(contentSize.x, context).toFloat(),
                metersToPx(contentSize.y, context).toFloat())
        update(scrollView.position)
    }

    private fun update(viewPosition: Vector2) {
        content?.let { content ->
            val contentBounds = content.getContentBounding()
            val viewBounds = getScrollBounds()
            val alignTopLeft = Vector2(
                    viewBounds.left - contentBounds.left,
                    viewBounds.top - contentBounds.top)

            val contentSize = contentBounds.size()
            val viewSize = viewBounds.size()
            val possibleTravel = (contentSize - viewSize).coerceAtLeast(0F)
            val travel = Vector2(
                    -possibleTravel.x * viewPosition.x,
                    possibleTravel.y * viewPosition.y)

            requestedContentPosition = alignTopLeft + travel

            val clipBounds = viewBounds.translate(-getContentPosition())
            content.setClipBounds(clipBounds)

            // Moving content up in z-plane, so it'll receive touch events first.
            content.localPosition = Vector3(
                    requestedContentPosition.x,
                    requestedContentPosition.y,
                    Z_ORDER_OFFSET)

        }
    }

    private fun getScrollBounds(): Bounding {
        val hBarHeightPx = (view as CustomScrollView).hBar?.height ?: 0
        val vBarWidthPx = (view as CustomScrollView).vBar?.width ?: 0

        val hBarHeightMeters = pxToMeters(hBarHeightPx, context)
        val vBarWidthPxMeters = pxToMeters(vBarWidthPx, context)

        return Bounding(
                -size.x / 2F,
                -size.y / 2F + hBarHeightMeters,
                size.x / 2F - vBarWidthPxMeters,
                size.y / 2F)
    }

    private fun setScrollDirection(props: Bundle) {
        val value = props.getString(PROP_SCROLL_DIRECTION)
        if (value != null) {
            (view as CustomScrollView).scrollDirection = value
        }
    }


}