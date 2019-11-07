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

import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.props.Bounding
import com.reactlibrary.utils.Utils

/**
 * Container for other Nodes (<view>)
 *
 * It does not use alignment, because it does not have a shape defined.
 * However if we wanted to use alignment, we would probably have to reimplement
 * the bounding calculation method [Utils.calculateSumBounds],
 * so that it would take into account the local position of GroupNode itself.
 */
class GroupNode(initProps: ReadableMap) :
        TransformNode(initProps, hasRenderable = false, useContentNodeAlignment = false) {

    override fun setClipBounds(clipBounds: Bounding) {
        val localBounds = clipBounds.translate(-getContentPosition())
        contentNode.children
                .filterIsInstance<TransformNode>()
                .forEach { it.setClipBounds(localBounds) }
    }

    override fun getContentBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(contentNode.children)
        return Bounding(
                childBounds.left + contentNode.localPosition.x,
                childBounds.bottom + contentNode.localPosition.y,
                childBounds.right + contentNode.localPosition.x,
                childBounds.top + contentNode.localPosition.y
        )
    }
}

