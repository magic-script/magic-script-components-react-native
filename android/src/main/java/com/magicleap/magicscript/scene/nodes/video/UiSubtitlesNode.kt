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

package com.magicleap.magicscript.scene.nodes.video

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.RenderPriority
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.scene.nodes.UiTextNode

class UiSubtitlesNode(
        props: ReadableMap,
        context: Context,
        viewRenderableLoader: ViewRenderableLoader,
        nodeClipper: Clipper,
        fontProvider: FontProvider
) : UiTextNode(props, context, viewRenderableLoader, nodeClipper, fontProvider) {

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.subtitles, null) as TextView
    }

    override fun onViewLoaded(viewRenderable: Renderable) {
        super.onViewLoaded(viewRenderable)
        viewRenderable.renderPriority = RenderPriority.ABOVE_DEFAULT
    }
}
