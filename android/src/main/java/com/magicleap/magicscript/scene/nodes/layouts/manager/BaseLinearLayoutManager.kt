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

import com.magicleap.magicscript.scene.nodes.base.LayoutParams
import com.magicleap.magicscript.scene.nodes.base.UiBaseLayout.Companion.WRAP_CONTENT_DIMENSION
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.Utils

abstract class BaseLinearLayoutManager<T : LayoutParams> : SizedLayoutManager<T>() {

    override fun getLayoutBounds(layoutParams: T): Bounding {
        val childrenBounds = Utils.calculateSumBounds(childrenList)
        val parentSize = layoutParams.size
        var sizeX = parentSize.x
        var sizeY = parentSize.y

        val itemPadding = layoutParams.itemPadding
        var leftOffset = -itemPadding.left
        var bottomOffset = -itemPadding.bottom
        var rightOffset = itemPadding.right
        var topOffset = itemPadding.top

        if (parentSize.x == WRAP_CONTENT_DIMENSION) {
            sizeX = childrenBounds.size().x
        } else {
            leftOffset = 0f
            rightOffset = 0f
        }

        if (parentSize.y == WRAP_CONTENT_DIMENSION) {
            sizeY = childrenBounds.size().y
        } else {
            topOffset = 0f
            bottomOffset = 0f
        }

        return Bounding(
            left = leftOffset,
            bottom = -sizeY + bottomOffset,
            right = sizeX + rightOffset,
            top = topOffset
        )
    }

}
