package com.magicleap.magicscript.ar

import com.google.ar.sceneform.rendering.ModelRenderable

interface RenderableAnimator {
    fun play(modelRenderable: ModelRenderable)
}