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
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.R
import com.magicleap.magicscript.utils.logMessage

class VideoRenderableLoaderImpl(private val context: Context) : VideoRenderableLoader {
    override fun loadRenderable(resultCallback: (result: RenderableResult<Renderable>) -> Unit) {
        ModelRenderable.builder()
            .setSource(context, R.raw.chroma_key_video)
            .build()
            .thenAccept { renderable ->
                renderable.material.setBoolean("disableChromaKey", true)
                // renderable.material.setFloat4("keyColor", CHROMA_KEY_COLOR)
                renderable.isShadowCaster = false
                renderable.isShadowReceiver = false
                resultCallback(RenderableResult.Success(renderable))
            }
            .exceptionally { throwable ->
                logMessage("error loading video renderable: $throwable")
                resultCallback(RenderableResult.Error(throwable))
                null
            }
    }
}