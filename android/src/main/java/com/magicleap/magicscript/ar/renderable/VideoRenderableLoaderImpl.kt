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

import android.content.Context
import com.google.ar.sceneform.rendering.ModelRenderable
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.utils.logMessage

class VideoRenderableLoaderImpl(
    private val context: Context,
    private val arResourcesProvider: ArResourcesProvider
) : VideoRenderableLoader,
    ArResourcesProvider.ArLoadedListener {

    init {
        arResourcesProvider.addArLoadedListener(this)
    }

    private val pendingRequests = mutableListOf<RenderableLoadRequest>()

    override fun loadRenderable(request: RenderableLoadRequest) {
        if (arResourcesProvider.isArLoaded()) {
            load(request)
        } else {
            pendingRequests.add(request)
        }
    }

    override fun onArLoaded() {
        val requestIterator = pendingRequests.iterator()
        while (requestIterator.hasNext()) {
            load(requestIterator.next())
            requestIterator.remove()
        }
    }

    override fun cancel(request: RenderableLoadRequest) {
        request.cancel()
        pendingRequests.remove(request)
    }

    private fun load(request: RenderableLoadRequest) {
        ModelRenderable.builder()
            .setSource(context, R.raw.chroma_key_video)
            .build()
            .thenAccept { renderable ->
                if (!request.isCancelled) {
                    renderable.material.setBoolean("disableChromaKey", true)
                    renderable.isShadowCaster = false
                    renderable.isShadowReceiver = false
                    request.listener.invoke(RenderableResult.Success(renderable))
                }
            }
            .exceptionally { throwable ->
                logMessage("error loading video renderable: $throwable")
                request.listener.invoke(RenderableResult.Error(throwable))
                null
            }
    }

}