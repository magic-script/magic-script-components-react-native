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
import android.graphics.PointF
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
import com.reactlibrary.utils.*
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
    }

    private var width = 0F
    private var height = 0F

    // Non-transform nodes aren't currently supported.
    private var content: TransformNode? = null
    private var contentBounds = Bounding()

    private var scrollRequested = false
    private var requestedContentPosition = PointF()

    private var looperHandler = Handler(Looper.getMainLooper())

    init {
        // set default properties values
        properties.putDefaultDouble(PROP_WIDTH, 1.0)
        properties.putDefaultDouble(PROP_HEIGHT, 1.0)
    }

    // Starting loops and registering listeners
    // only after content was delivered.
    override fun addContent(child: Node) {
        super.addContent(child)
        if (child !is TransformNode) {
            return
        }

        this.content = child

        view.onDrawListener {
            val eps = 1e-5F // epsilon
            if (scrollRequested) {
                content!!.localPosition = Vector3(
                        requestedContentPosition.x,
                        requestedContentPosition.y,
                        eps) // Moving content up in z-plane, so it'll receive touch events first.
                scrollRequested = false
            }
        }

        val scrollView = view as CustomScrollView
        scrollView.onScrollChangeListener = { position: PointF ->
            update(position)
        }

        layoutLoop()
    }

    private fun layoutLoop() {
        looperHandler.postDelayed({

            val newBounds = content!!.getContentBounding()
            if (!Bounding.equalInexact(contentBounds, newBounds)) {
                val scrollView = (view as CustomScrollView)
                contentBounds = newBounds
                val contentSize = contentBounds.size()
                scrollView.contentSize = PointF(
                        metersToPx(contentSize.x),
                        metersToPx(contentSize.y))
                update(scrollView.getViewPosition())
            }

            layoutLoop()
        }, 100L)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.scroll_view, null)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }
    }

    override fun setupView() {
        width = this.properties.getDouble(PROP_WIDTH).toFloat()
        height = this.properties.getDouble(PROP_HEIGHT).toFloat()

        val widthPx = metersToPx(width).toInt()
        val heightPx = metersToPx(height).toInt()

        view.layoutParams = RelativeLayout.LayoutParams(widthPx, heightPx)

        // Overriding scrollbars dimensions from layout xml
        // because we want to set view dimensions in one place and
        // for consistency use meters instead of pixels.
        val scrollView = view as CustomScrollView
        val scrollBarWidthPx = metersToPx(DEFAULT_SCROLLBAR_WIDTH).toInt()
        scrollView.h_bar.layoutParams.width = widthPx - scrollBarWidthPx
        scrollView.h_bar.layoutParams.height = scrollBarWidthPx
        scrollView.v_bar.layoutParams.width = scrollBarWidthPx
        scrollView.v_bar.layoutParams.height = heightPx - scrollBarWidthPx
    }

    fun stopNestedScroll() {
        view.stopNestedScroll()
    }

    // Function by which ViewWrapper delivers intercepted motion events.
    fun onTouchEvent(event: MotionEvent): Boolean {

        val viewBounds = getScrollBounds()
        event.setLocation(
                metersToPx(event.getX() - viewBounds.left),
                metersToPx(-event.getY() + viewBounds.top))
        return view.onTouchEvent(event)
    }

    override fun getScrollTranslation(): PointF {
        // When translating clip bounds to node's local cooridanate
        // system we use localPositions. Hovewer due to ARCore nature
        // localPosition of ScrollViewNode direct descendant can change
        // randomly for some milliseconds after beeing writen to. Thus
        // we can't reliably use it. To solve the problem we add child
        // localPosition to translation - the child node will subtract
        // it in it's setClipBounds method, effectively zeroing it.
        // Then, instead of zeroed child localPosition we use
        // requestedContentPosition.
        return PointF(
                -requestedContentPosition.x + content!!.localPosition.x,
                -requestedContentPosition.y + content!!.localPosition.y)
    }

    private fun update(viewPosition: PointF) {
        if (scrollRequested) {
            return
        }

        val contentBounds = content!!.getContentBounding()
        val viewBounds = getScrollBounds()
        val alignTopLeft = PointF(
                viewBounds.left - contentBounds.left,
                viewBounds.top - contentBounds.top)

        val contentSize = contentBounds.size()
        val viewSize = viewBounds.size()
        val possibleTravel = (contentSize - viewSize).coerceAtLeast(0F)
        val travel = PointF(
                -possibleTravel.x * viewPosition.x,
                possibleTravel.y * viewPosition.y)

        requestedContentPosition = alignTopLeft + travel

        val clipBounds = Bounding(
                viewBounds.left + getScrollTranslation().x,
                viewBounds.bottom + getScrollTranslation().y,
                viewBounds.right + getScrollTranslation().x,
                viewBounds.top + getScrollTranslation().y)
        content!!.setClipBounds(clipBounds)
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

    private fun metersToPx(meters: Float): Float {
        return Utils.metersToPxFloat(meters, context)
    }
}
