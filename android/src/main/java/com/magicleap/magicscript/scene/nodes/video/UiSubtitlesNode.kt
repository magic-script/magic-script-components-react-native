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
