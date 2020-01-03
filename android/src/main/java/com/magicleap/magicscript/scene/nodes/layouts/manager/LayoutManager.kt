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
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Bounding

/**
 * Layout manager is responsible for placing the children nodes in
 * the correct positions inside a layout
 */
interface LayoutManager<T : LayoutParams> {

    /**
     * Layouts the children based on calculated [childrenBounds]
     */
    fun layoutChildren(
        layoutParams: T,
        children: List<TransformNode>,
        childrenBounds: Map<Int, Bounding>
    )

    /**
     * Return the layout's local bounding (usually the sum of children bounds + padding)
     */
    fun getLayoutBounds(layoutParams: T): Bounding

}