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
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
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

class UiScrollViewNode(initProps: ReadableMap, context: Context, viewRenderableLoader: ViewRenderableLoader) :
        UiNode(initProps, context, viewRenderableLoader, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"

        const val DEFAULT_SCROLLBAR_WIDTH = 0.03F
    }

    private lateinit var child: Node

    private var looperHandler = Handler(Looper.getMainLooper())

    private var childBounds = Bounding()

    private var scrollRequested = false
    private var newChildPosition = PointF()

    init {
        // set default properties values
        properties.putDefaultDouble(PROP_WIDTH, 1.0)
        properties.putDefaultDouble(PROP_HEIGHT, 1.0)
    }

    fun getScrollView(): CustomScrollView {
        return view as CustomScrollView
    }

    override fun addContent(child: Node) {
        super.addContent(child)
        this.child = child
        layoutLoop()
    }

    private fun layoutLoop() {
        looperHandler.postDelayed({

            val newBounds = calculateAbsoluteBoundsOfNode(child)
            if (!Bounding.equalInexact(childBounds, newBounds)) {
                val scrollView = (view as CustomScrollView)
                childBounds = newBounds
                val childSize = childBounds.size()
                scrollView.contentSize = PointF(
                        Utils.metersToPx(childSize.x, context).toFloat(),
                        Utils.metersToPx(childSize.y, context).toFloat())
                update(scrollView.getViewPosition())
            }

            layoutLoop()
        }, 100L)
    }

    override fun provideView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.scroll_view, null)
        val scrollView = view as CustomScrollView
        scrollView.onScrollChangeListener = { position: PointF ->
            update(position)
        }
        val eps = 1e-5F // epsilon
        view.onPreDrawListener {
            if (scrollRequested) {
                child.localPosition = Vector3(
                        newChildPosition.x,
                        newChildPosition.y,
                        eps)
                scrollRequested = false
            }
            true
        }
        return view
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }
    }

    override fun setupView() {
        val widthInMeters = this.properties.getDouble(PROP_WIDTH).toFloat()
        val widthPx = metersToPx(widthInMeters)

        val heightInMeters = this.properties.getDouble(PROP_HEIGHT).toFloat()
        val heightPx = metersToPx(heightInMeters)

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

    private fun update(viewPosition: PointF) {

        // Non-transform Nodes aren't currently supported.
        if (child !is TransformNode) {
            return
        }

        val childBounds = calculateAbsoluteBoundsOfNode(child)
        val viewBounds = calculateAbsoluteBoundsOfNode(this)
        val alignTopLeft = PointF(
                viewBounds.left - childBounds.left,
                viewBounds.top - childBounds.top)

        val childSize = childBounds.size()
        val viewSize = viewBounds.size() - DEFAULT_SCROLLBAR_WIDTH
        val possibleTravel = (childSize - viewSize).coerceAtLeast(0F)
        val travel = PointF(
                -possibleTravel.x * viewPosition.x,
                possibleTravel.y * viewPosition.y)

        newChildPosition = alignTopLeft + travel

        val clipArea = RectF(
                viewBounds.left - newChildPosition.x,
                viewBounds.top - newChildPosition.y,
                viewBounds.right - newChildPosition.x - DEFAULT_SCROLLBAR_WIDTH,
                viewBounds.bottom - newChildPosition.y + DEFAULT_SCROLLBAR_WIDTH)
        (child as TransformNode).setClipBounds(clipArea)

        scrollRequested = true
    }

    private fun calculateAbsoluteBoundsOfNode(node: Node): Bounding {
        val bounds = if (node is TransformNode) {
            node.getBounding()
        } else {
            Utils.calculateBoundsOfNode(node)
        }
        return Bounding(
                bounds.left - node.localPosition.x,
                bounds.bottom - node.localPosition.y,
                bounds.right - node.localPosition.x,
                bounds.top - node.localPosition.y)
    }

    private fun metersToPx(meters: Float): Int {
        return Utils.metersToPx(meters, context)
    }
}
