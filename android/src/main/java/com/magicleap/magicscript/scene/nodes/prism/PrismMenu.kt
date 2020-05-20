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
import com.magicleap.magicscript.utils.SimpleAnimatorListener

class PrismMenu(
    context: Context,
    arResourcesProvider: ArResourcesProvider,
    private var title: String
) : GroupNode(JavaOnlyMap()) {

    companion object {
        private const val ICON_SIZE = 0.2
        private const val TEXT_SIZE = 0.1
        private const val ICON_TYPE = "generic_three_dimensional"

        private const val SHOW_DELAY = 1000L
        private const val SHOW_ANIM_DURATION = 300L
    }

    var onEditClickListener: (() -> Unit)? = null

    var hardInvisible = false

    private val label: UiTextNode
    private val buttonEdit: UiButtonNode
    private val animator = ValueAnimator.ofFloat(0f, 1.2f, 1f)

    init {

        label = UiTextNode(
            initProps = getLabelProps(),
            context = context,
            viewRenderableLoader = ViewRenderableLoaderImpl(context, arResourcesProvider),
            nodeClipper = TextureClipper(),
            fontProvider = FontProviderImpl(context, AndroidFontProvider())
        )

        buttonEdit = UiButtonNode(
            initProps = getButtonProps(),
            context = context,
            viewRenderableLoader = ViewRenderableLoaderImpl(context, arResourcesProvider),
            nodeClipper = TextureClipper(),
            fontProvider = FontProviderImpl(context, AndroidFontProvider()),
            iconsRepo = IconsRepositoryImpl(
                DefaultIconsProvider(context),
                ExternalIconsProvider(context)
            )
        )
    }

    fun showAnimated() {
        if (animator.isStarted) {
            return
        }

        animator.addListener(object : SimpleAnimatorListener() {
            override fun onAnimationStart(animation: Animator?) {
                if (isActive && !hardInvisible) {
                    isVisible = true
                }
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

    override fun build() {
        super.build()

        label.apply {
            build()
            val positionY = ICON_SIZE.toFloat()
            localPosition = Vector3(0f, positionY, 0f)
        }

        buttonEdit.apply {
            build()
            // sale down for better quality
            localScale = Vector3(0.5f, 0.5f, 0.5f)
            val positionY = (ICON_SIZE / 2).toFloat()
            localPosition = Vector3(0f, positionY, 0f)
            onClickListener = {
                onEditClickListener?.invoke()
            }
        }

        addContent(label)
        addContent(buttonEdit)
    }

    override fun onVisibilityChanged(visibility: Boolean) {
        super.onVisibilityChanged(visibility)
        if (!visibility) {
            stopShowAnimation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopShowAnimation()
    }

    fun updateTitle(title: String) {
        this.title = title
        label.update(getLabelProps())
    }

    private fun getLabelProps(): JavaOnlyMap {
        return JavaOnlyMap.of(
            UiTextNode.PROP_TEXT, title,
            UiTextNode.PROP_TEXT_SIZE, TEXT_SIZE,
            PROP_ALIGNMENT, "bottom-center"
        )
    }

    private fun getButtonProps(): JavaOnlyMap {
        return JavaOnlyMap.of(
            UiButtonNode.PROP_WIDTH, ICON_SIZE,
            UiButtonNode.PROP_HEIGHT, ICON_SIZE,
            UiButtonNode.PROP_ICON_TYPE, ICON_TYPE
        )
    }

    private fun stopShowAnimation() {
        if (animator.isStarted) {
            animator.cancel()
        }
    }
}