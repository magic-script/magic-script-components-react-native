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

package com.magicleap.magicscript.scene.nodes.layouts.manager

import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.layouts.params.LinearLayoutParams
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.ORIENTATION_VERTICAL

class LinearLayoutManager(
    private val verticalLinearLayoutManager: VerticalLinearLayoutManager<LayoutParams>,
    private val horizontalLinearLayoutManager: HorizontalLinearLayoutManager<LayoutParams>
) : SizedLayoutManager<LinearLayoutParams>() {

    override fun <T : LayoutParams> layoutNode(
        nodeInfo: NodeInfo,
        layoutInfo: LayoutInfo<T>
    ) {
        val layoutParams = layoutInfo.params as LinearLayoutParams
        val manager = getOrientationAwareManager(layoutParams)
        manager.layoutNode(nodeInfo, layoutInfo)
    }

    override fun getContentWidth(
        childrenBounds: Map<Int, Bounding>,
        layoutParams: LinearLayoutParams
    ): Float {
        val manager = getOrientationAwareManager(layoutParams)
        return manager.getContentWidth(childrenBounds, layoutParams)
    }

    override fun getContentHeight(
        childrenBounds: Map<Int, Bounding>,
        layoutParams: LinearLayoutParams
    ): Float {
        val manager = getOrientationAwareManager(layoutParams)
        return manager.getContentHeight(childrenBounds, layoutParams)
    }

    override fun calculateMaxChildWidth(
        childIdx: Int,
        childrenBounds: Map<Int, Bounding>,
        layoutParams: LinearLayoutParams
    ): Float {
        val manager = getOrientationAwareManager(layoutParams)
        return manager.calculateMaxChildWidth(childIdx, childrenBounds, layoutParams)
    }

    override fun calculateMaxChildHeight(
        childIdx: Int,
        childrenBounds: Map<Int, Bounding>,
        layoutParams: LinearLayoutParams
    ): Float {
        val manager = getOrientationAwareManager(layoutParams)
        return manager.calculateMaxChildHeight(childIdx, childrenBounds, layoutParams)
    }

    private fun getOrientationAwareManager(layoutParams: LinearLayoutParams) =
        if (layoutParams.orientation == ORIENTATION_VERTICAL) {
            verticalLinearLayoutManager
        } else {
            horizontalLinearLayoutManager
        }

}
