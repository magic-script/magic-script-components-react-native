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

import android.graphics.PointF
import com.google.ar.sceneform.math.Vector3
import android.os.Handler
import android.os.Looper
import com.google.ar.sceneform.Node
import com.reactlibrary.R
import android.view.LayoutInflater
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.CustomScrollBar
import com.reactlibrary.scene.nodes.views.CustomScrollView
import com.reactlibrary.utils.*
import com.reactlibrary.utils.putDefaultDouble
import com.reactlibrary.utils.putDefaultString
import kotlinx.android.synthetic.main.scroll_view.view.*
import com.reactlibrary.utils.logMessage
import com.reactlibrary.scene.nodes.base.TransformNode

class UiScrollViewNode(initProps: ReadableMap, context: Context) :
        UiNode(initProps, context, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
    }

    private lateinit var child: Node

    init {
        // set default properties values
        properties.putDefaultDouble(PROP_WIDTH, 1.0)
        properties.putDefaultDouble(PROP_HEIGHT, 1.0)
    }
    
    override fun addContent(child: Node) {
        super.addContent(child)
        this.child = child
        logMessage("addContent")
        layoutLoop()
    }

    private var handler = Handler(Looper.getMainLooper())
    private fun layoutLoop() {
        handler.postDelayed({
            positionChild(PointF())
            // layoutLoop()
        }, 1000L)
    }

    private fun onScrollChangeListener(position: PointF) {
        positionChild(position)
    }

    override fun provideView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.scroll_view, null)
        val scrollView = view as CustomScrollView
        scrollView.onScrollChangeListener = ::onScrollChangeListener
        scrollView.contentSize = PointF(2000F, 1000F)

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
        val widthPx = Utils.metersToPx(widthInMeters, context)

        val heightInMeters = this.properties.getDouble(PROP_HEIGHT).toFloat()
        val heightPx = Utils.metersToPx(heightInMeters, context)

        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
        (view as CustomScrollView).viewWidth = widthPx.toFloat()
        (view as CustomScrollView).viewHeight = heightPx.toFloat()
    }

    private fun positionChild(viewPosition: PointF){

        val viewBounds = getBounding()
        val childBounds = if (child is TransformNode) {
            (child as TransformNode).getBounding()
        } else {
            Utils.calculateBoundsOfNode(child)
        }

        val viewSize = viewBounds.getSize()
        val childSize = childBounds.getSize()
        val padding = (childSize - viewSize) / 2F
        val travel = viewSize - childSize
        val childPosition = padding + travel * viewPosition

        logMessage("pos " + childPosition.toString())

        child.localPosition = Vector3(childPosition.x, -childPosition.y, 0F)  
    }
}
