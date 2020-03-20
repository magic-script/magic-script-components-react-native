package com.magicleap.magicscript.scene.nodes.video

import android.content.Context
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.ar.RenderPriority
import com.magicleap.magicscript.ar.ViewRenderableLoader
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.scene.nodes.UiTextNode

class UiSubtitlesNode(
        props: ReadableMap = JavaOnlyMap.of(PROP_ALIGNMENT, "bottom-center"),
        context: Context,
        viewRenderableLoader: ViewRenderableLoader,
        nodeClipper: Clipper,
        fontProvider: FontProvider
): UiTextNode(props, context, viewRenderableLoader, nodeClipper, fontProvider) {

    override fun onViewLoaded(viewRenderable: Renderable) {
        super.onViewLoaded(viewRenderable)

        viewRenderable.renderPriority = RenderPriority.ABOVE_DEFAULT
    }
}