/*
 * Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes.prism

import com.google.ar.sceneform.ux.BaseTransformableNode
import com.google.ar.sceneform.ux.SelectionVisualizer

/**
 * This selection visualizer can be used to hide the default hint model displayed
 * when the node is selected.
 */
class EmptySelectionVisualizer : SelectionVisualizer {
    override fun applySelectionVisual(node: BaseTransformableNode?) {
        // no-op
    }

    override fun removeSelectionVisual(node: BaseTransformableNode?) {
        // no-op
    }
}