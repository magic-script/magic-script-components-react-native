package com.magicleap.magicscript.ar

import com.google.ar.sceneform.animation.ModelAnimator
import com.google.ar.sceneform.rendering.ModelRenderable

class LoopedAnimator : RenderableAnimator {

    override fun play(modelRenderable: ModelRenderable) {
        val animationsCount = modelRenderable.animationDataCount
        if (animationsCount > 0) {
            val animation = modelRenderable.getAnimationData(0)
            val animator = ModelAnimator(animation, modelRenderable)
            animator.repeatCount = ModelAnimator.INFINITE
            animator.start()
        }
    }

}