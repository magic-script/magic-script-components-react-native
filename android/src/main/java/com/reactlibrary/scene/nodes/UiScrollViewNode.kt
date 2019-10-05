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

import com.reactlibrary.scene.nodes.props.Bounding
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

            val newBounds = Utils.calculateBoundsOfNode(child)
            if (!Bounding.equalInexact(newBounds, childBounds)) {
                val scrollView = (view as CustomScrollView)
                childBounds = newBounds
                // logMessage("new childBounds " + childBounds.toString())
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
        val widthPx = Utils.metersToPx(widthInMeters, context)

        val heightInMeters = this.properties.getDouble(PROP_HEIGHT).toFloat()
        val heightPx = Utils.metersToPx(heightInMeters, context)

        view.layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
    }

    private fun update(viewPosition: PointF){

        val viewSize = Utils.calculateBoundsOfNode(this).size()
        val childSize = Utils.calculateBoundsOfNode(child).size()

        val padding = (childSize - viewSize) / 2F
        val travel = (childSize - viewSize).coerceAtLeast(0F)
        val childPosition = padding - travel * viewPosition

        // logMessage("pos " + childPosition.toString())
        // logMessage("viewSize " + viewSize.toString())
        // logMessage("childSize " + childSize.toString())
        // logMessage("padding " + padding.toString())
        // logMessage("travel " + travel.toString())
        // logMessage("childPosition " + childPosition.toString())
        // logMessage("viewPosition " + viewPosition.toString())

        child.localPosition = Vector3(childPosition.x, -childPosition.y, 0F)  
    }
}
