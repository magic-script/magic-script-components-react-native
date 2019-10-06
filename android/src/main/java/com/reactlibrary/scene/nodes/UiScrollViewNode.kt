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

import com.reactlibrary.scene.nodes.base.TransformNode
import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.scene.nodes.views.CustomScrollView
import com.reactlibrary.utils.*

class UiScrollViewNode(initProps: ReadableMap, context: Context) :
        UiNode(initProps, context, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
    }

    private lateinit var child: Node

    private var looperHandler = Handler(Looper.getMainLooper())

    private var childBounds = Bounding()

    init {
        // set default properties values
        properties.putDefaultDouble(PROP_WIDTH, 1.0)
        properties.putDefaultDouble(PROP_HEIGHT, 1.0)
        layoutLoop()
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
                logMessage("new childBounds " + childBounds.toString())
                val childSize = childBounds.size()
                scrollView.contentSize = PointF(
                        Utils.metersToPx(childSize.x, context).toFloat(),
                        Utils.metersToPx(childSize.y, context).toFloat())
                update(scrollView.viewPosition())
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
        return view
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }
    }

    override fun setViewSize() {
        val widthInMeters = this.properties.getDouble(PROP_WIDTH).toFloat()
        val widthPx = metersToPx(widthInMeters)

        val heightInMeters = this.properties.getDouble(PROP_HEIGHT).toFloat()
        val heightPx = metersToPx(heightInMeters)

        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

    private fun update(viewPosition: PointF) {

        val childBounds = calculateAbsoluteBoundsOfNode(child)
        val viewSize = calculateAbsoluteBoundsOfNode(this).size()
        val childSize = childBounds.size()

        val zero = PointF(viewSize.x / -2F, viewSize.y / 2F)
        val pivot = PointF(childBounds.left, childBounds.top)

        // logMessage("localPosition " + child.localPosition.toString())
        // logMessage("childBounds " + childBounds.toString())
        // logMessage("pivot " + pivot.toString())

        val possibleTravel = (childSize - viewSize).coerceAtLeast(0F)
        val travel = possibleTravel * viewPosition
        val childPosition = zero - pivot - travel

        // logMessage("contentSize " + metersToPx(childSize.x).toString())

        child.localPosition = Vector3(childPosition.x, childPosition.y, 0F)
        if (child is TransformNode) {
            val clipArea = calculateClipArea(viewSize, travel)
            (child as TransformNode).setClipBounds(clipArea)
        }
    }

    private fun calculateClipArea(viewSize: PointF, travel: PointF): RectF {
        return RectF(
            travel.x, 
            viewSize.y + travel.y, 
            viewSize.x + travel.x, 
            travel.y)
    }

    private fun metersToPx(meters: Float): Int {
        return Utils.metersToPx(meters, context)
    }

    private fun calculateAbsoluteBoundsOfNode(node: Node): Bounding {
        val bounds = if (node is TransformNode) {
            node.getBounding()
        } else {
            Utils.calculateBoundsOfNode(node)
        }
        return Bounding(
                bounds.left - child.localPosition.x,
                bounds.bottom - child.localPosition.y,
                bounds.right - child.localPosition.x,
                bounds.top - child.localPosition.y)
    }
}
