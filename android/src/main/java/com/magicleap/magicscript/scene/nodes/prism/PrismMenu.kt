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

package com.magicleap.magicscript.scene.nodes.prism

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.ar.clip.TextureClipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoaderImpl
import com.magicleap.magicscript.font.providers.AndroidFontProvider
import com.magicleap.magicscript.font.providers.FontProviderImpl
import com.magicleap.magicscript.icons.DefaultIconsProvider
import com.magicleap.magicscript.icons.ExternalIconsProvider
import com.magicleap.magicscript.icons.IconsRepositoryImpl
import com.magicleap.magicscript.scene.nodes.GroupNode
import com.magicleap.magicscript.scene.nodes.UiTextNode
import com.magicleap.magicscript.scene.nodes.button.UiButtonNode

class PrismMenu(
    private val context: Context,
    private val arResourcesProvider: ArResourcesProvider
) : GroupNode(JavaOnlyMap()) {

    companion object {
        private const val ICON_SIZE = 0.1
        private const val TEXT_SIZE = 0.1
        private const val ICON_TYPE = "generic_three_dimensional"

        private const val SHOW_DELAY = 1000L
        private const val SHOW_ANIM_DURATION = 300L
    }

    var onButtonClickListener: (() -> Unit)? = null

    private val animator = ValueAnimator.ofFloat(0f, 1.2f, 1f)

    override fun build() {
        super.build()

        val labelProps = JavaOnlyMap.of(
            UiTextNode.PROP_TEXT, "Catalog app",
            UiTextNode.PROP_TEXT_SIZE, TEXT_SIZE,
            PROP_ALIGNMENT, "bottom-center"
        )

        val label = UiTextNode(
            initProps = labelProps,
            context = context,
            viewRenderableLoader = ViewRenderableLoaderImpl(context, arResourcesProvider),
            nodeClipper = TextureClipper(),
            fontProvider = FontProviderImpl(context, AndroidFontProvider())
        ).apply {
            build()
            localPosition = Vector3(0f, ICON_SIZE.toFloat(), 0f)
        }

        val buttonShowProps = JavaOnlyMap.of(
            UiButtonNode.PROP_WIDTH, ICON_SIZE,
            UiButtonNode.PROP_HEIGHT, ICON_SIZE,
            UiButtonNode.PROP_ICON_TYPE, ICON_TYPE
        )

        val buttonShow = UiButtonNode(
            initProps = buttonShowProps,
            context = context,
            viewRenderableLoader = ViewRenderableLoaderImpl(context, arResourcesProvider),
            nodeClipper = TextureClipper(),
            fontProvider = FontProviderImpl(context, AndroidFontProvider()),
            iconsRepo = IconsRepositoryImpl(
                DefaultIconsProvider(context),
                ExternalIconsProvider(context)
            )
        ).apply {
            build()
            localPosition = Vector3(0f, (ICON_SIZE / 2).toFloat(), 0f)
            onClickListener = {
                onButtonClickListener?.invoke()
            }
        }

        addContent(label)
        addContent(buttonShow)
    }

    override fun onVisibilityChanged(visibility: Boolean) {
        super.onVisibilityChanged(visibility)
        if (!visibility) {
            stopShowAnimation()
        }
    }

    fun showAnimated() {
        if (animator.isStarted) {
            return
        }

        animator.addListener(object : SimpleAnimatorListener() {
            override fun onAnimationStart(animation: Animator?) {
                isVisible = true
            }
        })

        animator.addUpdateListener {
            val scale = it.animatedValue as Float
            localScale = Vector3(scale, scale, scale)
        }

        animator.startDelay = SHOW_DELAY
        animator.duration = SHOW_ANIM_DURATION
        animator.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopShowAnimation()
    }

    private fun stopShowAnimation() {
        if (animator.isStarted) {
            animator.cancel()
        }
    }

    private abstract class SimpleAnimatorListener : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {

        }

        override fun onAnimationRepeat(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {

        }

        override fun onAnimationCancel(animation: Animator?) {

        }
    }

}