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
import com.google.ar.sceneform.rendering.ViewRenderable
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.utils.DataResult

class ViewRenderableLoaderImpl(
    private val context: Context,
    private val arResourcesProvider: ArResourcesProvider
) : ViewRenderableLoader,
    ArResourcesProvider.ArLoadedListener {

    init {
        arResourcesProvider.addArLoadedListener(this)
    }

    private val pendingRequests = mutableListOf<ViewRenderableLoader.LoadRequest>()

    override fun loadRenderable(request: ViewRenderableLoader.LoadRequest) {
        if (arResourcesProvider.isArLoaded()) {
            load(request)
        } else {
            pendingRequests.add(request)
        }
    }

    override fun onArLoaded(firstTime: Boolean) {
        val requestIterator = pendingRequests.iterator()
        while (requestIterator.hasNext()) {
            load(requestIterator.next())
            requestIterator.remove()
        }
    }

    private fun load(request: ViewRenderableLoader.LoadRequest) {
        val builder = ViewRenderable
            .builder()
            .setSource(context, R.raw.android_view) // using custom material to disable back side
            .setView(context, request.view)

        val horizontalAlignment =
            ViewRenderable.HorizontalAlignment.valueOf(request.horizontalAlignment.name)
        val verticalAlignment =
            ViewRenderable.VerticalAlignment.valueOf(request.verticalAlignment.name)
        builder.setHorizontalAlignment(horizontalAlignment)
        builder.setVerticalAlignment(verticalAlignment)

        builder.build()
            .thenAccept { renderable ->
                if (!request.isCancelled) {
                    renderable.isShadowReceiver = false
                    renderable.isShadowCaster = false
                    request.listener.invoke(DataResult.Success(renderable))
                }
            }
            .exceptionally { throwable ->
                request.listener.invoke(DataResult.Error(throwable))
                null
            }
    }

    override fun cancel(request: ViewRenderableLoader.LoadRequest) {
        request.cancel()
        pendingRequests.remove(request)
    }

}