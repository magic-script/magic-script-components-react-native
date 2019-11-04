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
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.props.AABB
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.views.CustomScrollView
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils.Companion.metersToPx
import com.reactlibrary.utils.Vector2
import com.reactlibrary.utils.onDrawListener
import com.reactlibrary.utils.putDefaultString

class UiScrollViewNode(
        initProps: ReadableMap,
        context: Context,
        viewRenderableLoader: ViewRenderableLoader) :
        UiNode(initProps, context, viewRenderableLoader, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_SCROLL_BOUNDS = "scrollBounds"
        const val PROP_SCROLL_DIRECTION = "scrollDirection"

        const val DEFAULT_SCROLL_DIRECTION = "vertical"

        const val LAYOUT_LOOP_DELAY = 50L
        const val Z_ORDER_OFFSET = 1e-5F
    }

    private var scrollBounds = AABB(Vector3.zero(), Vector3.one())

    // Non-transform nodes aren't currently supported.
    private var content: TransformNode? = null
    private var contentBounds = Bounding()

    private var hBar: UiScrollBarNode? = null
    private var vBar: UiScrollBarNode? = null

    private var scrollRequested = false
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
        return CustomScrollView(context)
    }

    override fun provideDesiredSize(): Vector2 {
        return Vector2(
                scrollBounds.getWidth(),
                scrollBounds.getHeight())
    }

    override fun setupView() {
        val propBounds = PropertiesReader.readAABB(this.properties, PROP_SCROLL_BOUNDS)
        if (propBounds != null) {
            scrollBounds = propBounds
        }

        val widthPx = metersToPx(scrollBounds.getWidth(), context)
        val heightPx = metersToPx(scrollBounds.getHeight(), context)

        view.layoutParams = RelativeLayout.LayoutParams(widthPx, heightPx)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_SCROLL_BOUNDS)) {
            setNeedsRebuild()
        }

        setScrollDirection(props)
    }

    // Starting loops and registering listeners
    // only after content was delivered.
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
        view.onDrawListener {
            if (scrollRequested) {
                val viewBounds = getScrollBounds()
                val clipBounds = viewBounds.translate(-getContentPosition())
                content!!.setClipBounds(clipBounds, clipNativeView = false)

                content!!.localPosition = Vector3(
                        requestedContentPosition.x,
                        requestedContentPosition.y,
                        Z_ORDER_OFFSET  // Moving content up in z-plane, so it'll receive touch events first.
                )
                scrollRequested = false
            }
        }

        val scrollView = view as CustomScrollView
        scrollView.onScrollChangeListener = { position: Vector2 ->
            update(position)
        }
    }

    override fun removeContent(child: Node) {
        super.removeContent(child)
        when (child) {
            content -> content = null
            hBar -> {
                hBar = null
                (view as CustomScrollView).hBar = null
            }
            vBar -> {
                vBar = null
                (view as CustomScrollView).vBar = null
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
                localPosition.x + requestedContentPosition.x - position.x,
                localPosition.y + requestedContentPosition.y - position.y
        )
    }

    private fun addScrollBar(scrollBarNode: UiScrollBarNode) {
        val bar = scrollBarNode.getCustomScrollBar()
        val scrollView = view as CustomScrollView
        if (bar.isVertical) {
            if (vBar == null) {
                super.addContent(scrollBarNode)
                vBar = scrollBarNode
                val posX = (scrollBarNode.size.x - size.x) / -2F
                scrollBarNode.localPosition = Vector3(posX, 0F, 0F)
                bar.layoutParams.height = metersToPx(size.y, context)
                scrollView.vBar = bar
            }
        } else {
            if (hBar == null) {
                super.addContent(scrollBarNode)
                hBar = scrollBarNode
                val posY = (scrollBarNode.size.y - size.y) / 2F
                scrollBarNode.localPosition = Vector3(0F, posY, 0F)
                bar.layoutParams.width = metersToPx(size.x, context)
                scrollView.hBar = bar
            }
        }
    }

    private fun layoutLoop() {
        looperHandler.postDelayed({
            if (content != null) {
                val newBounds = content!!.getContentBounding()
                if (!Bounding.equalInexact(contentBounds, newBounds)) {
                    val scrollView = (view as CustomScrollView)
                    contentBounds = newBounds
                    val contentSize = contentBounds.size()
                    scrollView.contentSize = Vector2(
                            metersToPx(contentSize.x, context).toFloat(),
                            metersToPx(contentSize.y, context).toFloat())
                    update(scrollView.position, true)
                }
            }

            layoutLoop()
        }, LAYOUT_LOOP_DELAY)
    }

    private fun update(viewPosition: Vector2, forceUpdate: Boolean = false) {
        if (content == null) {
            return
        }
        if (scrollRequested && !forceUpdate) {
            return
        }

        val contentBounds = content!!.getContentBounding()
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
        content!!.setClipBounds(clipBounds, clipNativeView = true)
        view.invalidate()

        scrollRequested = true
    }

    private fun getScrollBounds(): Bounding {
        return Bounding(
                -size.x / 2F,
                -size.y / 2F + (hBar?.size?.y ?: 0F),
                size.x / 2F - (vBar?.size?.x ?: 0F),
                size.y / 2F)
    }

    private fun setScrollDirection(props: Bundle) {
        val value = props.getString(PROP_SCROLL_DIRECTION)
        if (value != null) {
            (view as CustomScrollView).scrollDirection = value
        }
    }
}
