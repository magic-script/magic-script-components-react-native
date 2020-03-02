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

import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.layouts.LayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.params.LayoutParams
import com.magicleap.magicscript.scene.nodes.layouts.params.LinearLayoutParams
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.ORIENTATION_VERTICAL

class LinearLayoutManager(
    private val verticalLinearLayoutManager: VerticalLinearLayoutManager<LayoutParams>,
    private val horizontalLinearLayoutManager: HorizontalLinearLayoutManager<LayoutParams>
) : LayoutManager<LinearLayoutParams> {

    override fun layoutChildren(
        layoutParams: LinearLayoutParams,
        children: List<TransformNode>,
        childrenBounds: Map<TransformNode, AABB>
    ) {

        val manager = getOrientationAwareManager(layoutParams)
        manager.layoutChildren(layoutParams, children, childrenBounds)
    }

    override fun getLayoutBounds(layoutParams: LinearLayoutParams): AABB {
        val manager = getOrientationAwareManager(layoutParams)
        return manager.getLayoutBounds(layoutParams)
    }

    private fun getOrientationAwareManager(layoutParams: LinearLayoutParams) =
        if (layoutParams.orientation == ORIENTATION_VERTICAL) {
            verticalLinearLayoutManager
        } else {
            horizontalLinearLayoutManager
        }

}
