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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.scene.nodes.base.ReactNode
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.views.CustomScrollBar
import com.magicleap.magicscript.scene.nodes.views.CustomScrollView
import com.magicleap.magicscript.utils.*
import com.magicleap.magicscript.utils.Utils.Companion.metersToPx
import com.magicleap.magicscript.utils.Utils.Companion.pxToMeters
import kotlin.math.max
import kotlin.math.min

open class UiScrollViewNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper
) : UiNode(
    initProps,
    context,
    viewRenderableLoader,
    nodeClipper,
    useContentNodeAlignment = false
) {

    companion object {
        // properties
        const val PROP_SCROLL_BOUNDS = "scrollBounds"
        const val PROP_SCROLL_DIRECTION = "scrollDirection"
        const val PROP_SCROLLBAR_VISIBILITY = "scrollBarVisibility"
        const val PROP_SCROLL_VALUE = "scrollValue"
        const val PROP_SCROLL_OFFSET = "scrollOffset"

        const val DEFAULT_WIDTH = 1.0F
        const val DEFAULT_HEIGHT = 1.0F
        const val DEFAULT_THICKNESS = 1.0F

        const val SCROLL_DIRECTION_VERTICAL = "vertical"
        const val SCROLL_DIRECTION_HORIZONTAL = "horizontal"

        const val BAR_THICKNESS_RATIO = 0.03F // relative to min (width, height)
        const val BAR_MINIMUM_THICKNESS = 0.05F // in meters
        const val HIDDEN_BAR_THICKNESS = 0

        const val LAYOUT_LOOP_DELAY = 50L
        const val Z_OFFSET = 1e-5F
    }

    var onScrollChangeListener: ((position: Float) -> Unit)? = null

    protected var onContentSizeChangedListener: ((contentSize: Vector3) -> Unit)? = null

    protected var vBarNode: UiScrollBarNode? = null
        private set

    protected var hBarNode: UiScrollBarNode? = null
        private set

    private var content: TransformNode? = null
    private var contentBounds = AABB()

    private var requestedContentPosition = Vector2()
    private var looperHandler = Handler(Looper.getMainLooper())
    private var scrollOffset = Vector3.zero()

    init {
        properties.putDefault(PROP_SCROLL_DIRECTION, SCROLL_DIRECTION_VERTICAL)
        layoutLoop()
    }

    // Function by which ViewWrapper delivers intercepted motion events.
    fun onTouchEvent(event: MotionEvent): Boolean {
        val viewBounds = getScrollBounds().toBounding2d()
        event.setLocation(
            metersToPx(event.x - viewBounds.left, context).toFloat(),
            metersToPx(-event.y + viewBounds.top, context).toFloat()
        )
        return view.onTouchEvent(event)
    }

    fun stopNestedScroll() {
        view.stopNestedScroll()
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.scroll_view, null)
    }

    override fun provideDesiredSize(): Vector2 {
        val propBounds = properties.read<AABB>(PROP_SCROLL_BOUNDS)
        return if (propBounds != null) {
            val size = propBounds.size()
            Vector2(size.x, size.y)
        } else {
            Vector2(DEFAULT_WIDTH, DEFAULT_HEIGHT)
        }
    }

    override fun setupView() {
        super.setupView()

        vBarNode?.apply { setupScrollBar(this) }
        hBarNode?.apply { setupScrollBar(this) }

        val scrollView = view as CustomScrollView
        scrollView.onScrollChangeListener = { position: Vector2 ->
            update(position)
            val scrollDirection = properties.read<String>(PROP_SCROLL_DIRECTION)
            if (scrollDirection == SCROLL_DIRECTION_VERTICAL) {
                this.onScrollChangeListener?.invoke(position.y)
            } else {
                this.onScrollChangeListener?.invoke(position.x)
            }
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_SCROLL_BOUNDS)) {
            setNeedsRebuild()
        }

        setScrollDirection(props)
        setScrollbarVisibility(props)
        setScrollValue(props)
        setScrollOffset(props)
    }

    override fun addContent(child: ReactNode) {
        super.addContent(child)

        if (child !is TransformNode) {
            return
        }

        if (child is UiScrollBarNode) {
            if (child.orientation == UiScrollBarNode.ORIENTATION_VERTICAL) {
                if (vBarNode == null) {
                    vBarNode = child
                    setupScrollBar(child)
                }
            } else {
                if (hBarNode == null) {
                    hBarNode = child
                    setupScrollBar(child)
                }
            }
            return
        }

        if (content == null) {
            this.content = child
            applyContentClipping()
        } else {
            child.hide()
            logMessage("ScrollView should have only one direct child", true)
        }
    }

    override fun removeContent(child: ReactNode) {
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

    override fun setAlignment(props: Bundle) {
        // Alignment cannot be changed for scroll view according to Lumin.
        // It is hardcoded to center-center
    }

    override fun clipChildren() {
        applyContentClipping()
    }

    override fun onDestroy() {
        super.onDestroy()
        // stop the layout loop
        looperHandler.removeCallbacksAndMessages(null)
    }

    protected fun calculateBarThickness(containerWidth: Float, containerHeight: Float): Float {
        val parentBasedThickness = min(containerWidth, containerHeight) * BAR_THICKNESS_RATIO
        return max(parentBasedThickness, BAR_MINIMUM_THICKNESS)
    }

    private fun setupScrollBar(scrollBarNode: UiScrollBarNode) {
        val thickness = calculateBarThickness(size.x, size.y)
        val thicknessPx = metersToPx(thickness, context)
        val barToSetup: CustomScrollBar? =
            if (scrollBarNode.orientation == UiScrollBarNode.ORIENTATION_VERTICAL) {
                (view as CustomScrollView).vBar
            } else {
                (view as CustomScrollView).hBar
            }

        barToSetup?.apply {
            useAutoThumbSize = scrollBarNode.thumbSize == UiScrollBarNode.THUMB_SIZE_AUTO.toFloat()
            thumbSize = scrollBarNode.thumbSize
            setThickness(thicknessPx)
        }
    }

    private fun layoutLoop() {
        looperHandler.postDelayed({
            content?.let { it ->
                // check if content bounds changed
                val newContentBounds = it.getContentBounding()
                if (!contentBounds.equalInexact(newContentBounds)) {
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
            metersToPx(contentSize.y, context).toFloat()
        )

        update(scrollView.position)
    }

    private fun update(viewPosition: Vector2) {
        content?.let { content ->
            val contentBounds = content.getContentBounding().toBounding2d()
            val viewBounds = getScrollBounds().toBounding2d()
            val alignTopLeft = Vector2(
                viewBounds.left - contentBounds.left,
                viewBounds.top - contentBounds.top
            )

            val contentSize = contentBounds.size()
            val viewSize = viewBounds.size()
            val possibleTravel = (contentSize - viewSize).coerceAtLeast(0F)
            val travel = Vector2(
                -possibleTravel.x * viewPosition.x,
                possibleTravel.y * viewPosition.y
            )

            requestedContentPosition = alignTopLeft + travel

            // Moving content up in z-plane, so it'll receive touch events first
            val position = Vector3(
                requestedContentPosition.x,
                requestedContentPosition.y,
                Z_OFFSET
            ) + scrollOffset

            if (!position.equalInexact(content.localPosition, epsilon = 1e-5f)) {
                content.localPosition = position
                applyContentClipping()
            }
        }
    }

    private fun applyContentClipping() {
        content?.let { content ->
            // When translating clip bounds to node's local coordinate
            // system we use localPositions. However due to ARCore nature
            // localPosition of ScrollViewNode direct descendant can change
            // randomly for some milliseconds after being written to. Thus
            // we can't reliably use it. To solve the problem we add child
            // localPosition to translation - the child node will subtract
            // it in it's setClipBounds method, effectively zeroing it.
            // Then, instead of zeroed child localPosition we use
            // requestedContentPosition.

            val position = content.localPosition
            val contentPosition = Vector3(
                requestedContentPosition.x - position.x,
                requestedContentPosition.y - position.y,
                position.z
            )

            val contentClipping = getScrollBounds().translated(-contentPosition)
            val clipTranslation = -Vector3(
                localPosition.x + contentPosition.x,
                localPosition.y + contentPosition.y,
                localPosition.z + contentPosition.z
            )
            val parentClipping = clipBounds?.translated(clipTranslation)

            val parentAwareClipping = if (parentClipping == null) {
                contentClipping
            } else {
                val scale = localScale
                val scaleX = if (scale.x > 0) 1 / scale.x else 0f
                val scaleY = if (scale.y > 0) 1 / scale.y else 0f
                val scaleZ = if (scale.z > 0) 1 / scale.z else 0f
                val parentClippingScaled = parentClipping.scaled(scaleX, scaleY, scaleZ)
                contentClipping.intersection(parentClippingScaled)
            }
            content.clipBounds = parentAwareClipping
        }
    }

    private fun getScrollBounds(): AABB {
        val hBarHeightPx = (view as CustomScrollView).hBar?.height ?: 0
        val vBarWidthPx = (view as CustomScrollView).vBar?.width ?: 0
        val hBarHeightMeters = pxToMeters(hBarHeightPx, context)
        val vBarWidthPxMeters = pxToMeters(vBarWidthPx, context)

        val xMin = -size.x / 2F
        val xMax = size.x / 2F - vBarWidthPxMeters

        val yMin = -size.y / 2F + hBarHeightMeters
        val yMax = size.y / 2F

        val propBounds = properties.read<AABB>(PROP_SCROLL_BOUNDS)
        val sizeZ = propBounds?.size()?.z ?: DEFAULT_THICKNESS

        val zMin = -sizeZ / 2
        val zMax = sizeZ / 2

        return AABB(min = Vector3(xMin, yMin, zMin), max = Vector3(xMax, yMax, zMax))
    }

    private fun setScrollDirection(props: Bundle) {
        val value = props.getString(PROP_SCROLL_DIRECTION)
        if (value != null) {
            (view as CustomScrollView).scrollDirection = value
        }
    }

    private fun setScrollbarVisibility(props: Bundle) {
        val visibility = props.read<String>(PROP_SCROLLBAR_VISIBILITY) ?: return
        (view as CustomScrollView).scrollBarsVisibility = visibility
    }

    private fun setScrollValue(props: Bundle) {
        val scrollValue = props.read<Double>(PROP_SCROLL_VALUE) ?: return
        val clampedValue = scrollValue.coerceIn(0.0, 1.0).toFloat()

        when (properties.read<String>(PROP_SCROLL_DIRECTION)) {
            SCROLL_DIRECTION_VERTICAL -> {
                (view as CustomScrollView).position = Vector2(0f, clampedValue)
            }
            SCROLL_DIRECTION_HORIZONTAL -> {
                (view as CustomScrollView).position = Vector2(clampedValue, 0f)
            }
        }
    }

    private fun setScrollOffset(props: Bundle) {
        val scrollOffset = props.read<Vector3>(PROP_SCROLL_OFFSET) ?: return
        this.scrollOffset = scrollOffset
    }

}
