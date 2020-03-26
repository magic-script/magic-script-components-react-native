/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.ar.renderable

import android.view.View
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.scene.nodes.props.Alignment

interface ViewRenderableLoader {

    fun loadRenderable(request: LoadRequest)

    fun cancel(request: LoadRequest)

    class LoadRequest(
        val view: View,
        val horizontalAlignment: Alignment.Horizontal,
        val verticalAlignment: Alignment.Vertical,
        listener: ((result: RenderableResult<Renderable>) -> Unit)
    ) : RenderableLoadRequest(listener)

}