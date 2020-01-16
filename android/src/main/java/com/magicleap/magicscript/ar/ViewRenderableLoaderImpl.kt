/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.ar

import android.content.Context
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.magicleap.magicscript.R

class ViewRenderableLoaderImpl(private val context: Context) : ViewRenderableLoader {

    override fun loadRenderable(
        config: ViewRenderableLoader.Config,
        resultCallback: ((result: RenderableResult<Renderable>) -> Unit)
    ) {

        val builder = ViewRenderable
            .builder()
            .setSource(context, R.raw.android_view) // using custom material to disable back side
            .setView(context, config.view)

        val horizontalAlignment =
            ViewRenderable.HorizontalAlignment.valueOf(config.horizontalAlignment.name)
        val verticalAlignment =
            ViewRenderable.VerticalAlignment.valueOf(config.verticalAlignment.name)
        builder.setHorizontalAlignment(horizontalAlignment)
        builder.setVerticalAlignment(verticalAlignment)

        builder.build()
            .thenAccept { renderable ->
                renderable.isShadowReceiver = false
                renderable.isShadowCaster = false
                resultCallback(RenderableResult.Success(renderable))
            }
            .exceptionally { throwable ->
                resultCallback(RenderableResult.Error(throwable))
                null
            }
    }

}