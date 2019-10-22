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
import android.widget.RelativeLayout
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.views.CustomScrollView
import com.reactlibrary.utils.Vector2
import com.reactlibrary.utils.onDrawListener
import com.reactlibrary.utils.putDefaultDouble
import kotlinx.android.synthetic.main.scroll_view.view.*

class UiScrollViewNode(
        initProps: ReadableMap,
        context: Context,
        viewRenderableLoader: ViewRenderableLoader) :
        UiNode(initProps, context, viewRenderableLoader, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"

        const val DEFAULT_SCROLLBAR_WIDTH = 0.03F
        const val DEFAULT_WIDTH = 1.0
        const val DEFAULT_HEIGHT = 1.0

        const val LAYOUT_LOOP_DELAY = 100L
        const val Z_ORDER_OFFSET = 1e-5F
    }

    private var width = 0F
    private var height = 0F

    // Non-transform nodes aren't currently supported.
    private var content: TransformNode? = null
    private var contentBounds = Bounding()
    private var scrollRequested = false
    private var requestedContentPosition = Vector2()

    private var looperHandler = Handler(Looper.getMainLooper())

    init {
        // set default properties values
        properties.putDefaultDouble(PROP_WIDTH, DEFAULT_WIDTH)
        properties.putDefaultDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
    }

    // Function by which ViewWrapper delivers intercepted motion events.
    fun onTouchEvent(event: MotionEvent): Boolean {
        val viewBounds = getScrollBounds()
        event.setLocation(
                metersToPx(event.x - viewBounds.left).toFloat(),
                metersToPx(-event.y + viewBounds.top).toFloat())
        return view.onTouchEvent(event)
    }

    fun stopNestedScroll() {
        view.stopNestedScroll()
    }

    // Starting loops and registering listeners
    // only after content was delivered.
    override fun addContent(child: Node) {
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
                val clipBounds = viewBounds.translate(getScrollTranslation())
                content!!.setClipBounds(clipBounds, nativeView = false)

                content!!.localPosition = Vector3(
                        requestedContentPosition.x,
                        requestedContentPosition.y,
                        Z_ORDER_OFFSET) // Moving content up in z-plane, so it'll receive touch events first.
                scrollRequested = false
            }
        }

        val scrollView = view as CustomScrollView
        scrollView.onScrollChangeListener = { position: Vector2 ->
            update(position)
        }

        layoutLoop()
    }

    override fun removeContent(child: Node) {
        super.removeContent(child)
        content = null
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.scroll_view, null)
    }

    override fun provideDesiredSize(): Vector2 {
        return Vector2(width, height)
    }

    override fun setupView() {
        width = this.properties.getDouble(PROP_WIDTH).toFloat()
        height = this.properties.getDouble(PROP_HEIGHT).toFloat()

        val widthPx = metersToPx(width)
        val heightPx = metersToPx(height)

        view.layoutParams = RelativeLayout.LayoutParams(widthPx, heightPx)

        // Overriding scrollbars dimensions from layout xml
        // because we want to set view dimensions in one place and
        // for consistency use meters instead of pixels.
        val scrollView = view as CustomScrollView
        val scrollBarWidthPx = metersToPx(DEFAULT_SCROLLBAR_WIDTH)
        scrollView.h_bar.layoutParams.width = widthPx - scrollBarWidthPx
        scrollView.h_bar.layoutParams.height = scrollBarWidthPx
        scrollView.v_bar.layoutParams.width = scrollBarWidthPx
        scrollView.v_bar.layoutParams.height = heightPx - scrollBarWidthPx
    }

    override fun getScrollTranslation(): Vector2 {
        // When translating clip bounds to node's local coordinate
        // system we use localPositions. However due to ARCore nature
        // localPosition of ScrollViewNode direct descendant can change
        // randomly for some milliseconds after being written to. Thus
        // we can't reliably use it. To solve the problem we add child
        // localPosition to translation - the child node will subtract
        // it in it's setClipBounds method, effectively zeroing it.
        // Then, instead of zeroed child localPosition we use
        // requestedContentPosition.
        val position = content!!.localPosition ?: Vector3()
        return Vector2(
                -requestedContentPosition.x + position.x,
                -requestedContentPosition.y + position.y)
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
                            metersToPx(contentSize.x).toFloat(),
                            metersToPx(contentSize.y).toFloat())
                    update(scrollView.getViewPosition())
                }
            }

            layoutLoop()
        }, LAYOUT_LOOP_DELAY)
    }

    private fun update(viewPosition: Vector2) {
        if (scrollRequested || content == null) {
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

        val clipBounds = viewBounds.translate(getScrollTranslation())
        content!!.setClipBounds(clipBounds, nativeView = true)
        view.invalidate()

        scrollRequested = true
    }

    private fun getScrollBounds(): Bounding {
        return Bounding(
                -width / 2F,
                -height / 2F + DEFAULT_SCROLLBAR_WIDTH,
                width / 2F - DEFAULT_SCROLLBAR_WIDTH,
                height / 2F)
    }
}
