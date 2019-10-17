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

package com.reactlibrary.scene

import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent
import android.widget.LinearLayout
import com.google.ar.sceneform.Node
import com.reactlibrary.scene.nodes.UiScrollViewNode
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.plus

class ViewWrapper(
        context: Context,
        private val parent: Node) : LinearLayout(context) {

    init {
        layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        )
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return findScrollAncestor() != null
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {

        val scrollNode = findScrollAncestor()
        if (event.actionMasked != MotionEvent.ACTION_MOVE && scrollNode != null) {
            scrollNode.stopNestedScroll()
        }

        val action = event.actionMasked
        return action == MotionEvent.ACTION_MOVE && scrollNode != null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val translation = calculateScrollTranslation()
        val positionMeters = PointF(
                Utils.pxToMeters(event.getX(), context),
                -Utils.pxToMeters(event.getY(), context))
        event.setLocation(
                positionMeters.x + translation.x,
                positionMeters.y + translation.y)
        return findScrollAncestor()!!.onTouchEvent(event)
    }

    private fun findScrollAncestor(): UiScrollViewNode? {

        // If parent is ScrollView we return null,
        // as ther's no sense in intercepting its events.
        if (parent == null || parent is UiScrollViewNode) {
            return null
        }

        var p: Node? = parent.parent
        while (p != null) {
            if (p is UiScrollViewNode) {
                return p
            }
            p = p.parent
        }
        return null
    }

    private fun calculateScrollTranslation(): PointF {

        var translation = PointF()
        var p: Node? = parent
        while (p != null) {
            if (p is TransformNode) {
                translation += p.scrollTranslation()
            }
            if (p is UiScrollViewNode) {
                break
            }
            p = p.parent
        }
        return translation
    }

    // Workaround for https://github.com/magic-script/magic-script-components-react-native/issues/7
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val width = if (widthMode == MeasureSpec.AT_MOST) {
            MeasureSpec.UNSPECIFIED
        } else {
            widthMeasureSpec
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val height = if (heightMode == MeasureSpec.AT_MOST) {
            MeasureSpec.UNSPECIFIED
        } else {
            heightMeasureSpec
        }

        super.onMeasure(width, height)
    }
}
