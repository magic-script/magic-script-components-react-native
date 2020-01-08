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

package com.magicleap.magicscript.scene.nodes.views

import android.content.Context
import android.view.MotionEvent
import android.widget.LinearLayout
import com.google.ar.sceneform.Node
import com.magicleap.magicscript.ArViewManager
import com.magicleap.magicscript.R
import com.magicleap.magicscript.scene.nodes.UiScrollViewNode
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.Vector2

class ViewWrapper(context: Context, private val parent: UiNode) : LinearLayout(context) {

    companion object {
        const val TOUCH_RADIUS_DP = 16F
    }

    private val touchRadiusPx = context.resources.displayMetrics.density * TOUCH_RADIUS_DP
    private var isBeingDragged = false
    private var previousTouch = Vector2()

    init {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (ArViewManager.showLayoutBounds) {
            background = context.getDrawable(R.drawable.debug_bg)
        }
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

    override fun shouldDelayChildPressedState(): Boolean {
        return findScrollAncestor() != null
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (parent.disallowInterceptTouchEvent()) {
            return false
        }

        val scrollNode = findScrollAncestor() ?: return false
        val action = event.actionMasked
        val pos = Vector2(event.x, event.y)

        // Check if the user has moved far enough from his original
        // down touch to consider this event a scroll.
        if (action == MotionEvent.ACTION_MOVE && !isBeingDragged) {
            val dist = pos - previousTouch
            val radiusSqr = dist.x * dist.x + dist.y * dist.y
            if (radiusSqr >= touchRadiusPx * touchRadiusPx) {
                isBeingDragged = true
            }
        }

        if (action != MotionEvent.ACTION_MOVE) {
            scrollNode.stopNestedScroll()
            isBeingDragged = false
        } else {
            previousTouch = pos
        }

        return action == MotionEvent.ACTION_MOVE && isBeingDragged
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val translation = calculateScrollTranslation()
        val positionMeters = Vector2(
            Utils.pxToMeters(event.x.toInt(), context),
            -Utils.pxToMeters(event.y.toInt(), context)
        )
        event.setLocation(
            positionMeters.x + translation.x,
            positionMeters.y + translation.y
        )
        return findScrollAncestor()?.onTouchEvent(event) ?: false
    }

    private fun findScrollAncestor(): UiScrollViewNode? {
        // If parent is ScrollView we return null,
        // as there's no sense in intercepting its events.
        if (parent is UiScrollViewNode) {
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

    private fun calculateScrollTranslation(): Vector2 {
        var translation = Vector2()
        var p: Node? = parent
        while (p != null) {
            if (p is TransformNode) {
                translation += p.getContentPosition()
            }
            if (p is UiScrollViewNode) {
                break
            }
            p = p.parent
        }
        return translation
    }

}
