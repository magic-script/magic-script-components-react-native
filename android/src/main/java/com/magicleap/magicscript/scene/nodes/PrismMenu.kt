/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.ar.clip.TextureClipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoaderImpl
import com.magicleap.magicscript.font.providers.AndroidFontProvider
import com.magicleap.magicscript.font.providers.FontProviderImpl
import com.magicleap.magicscript.icons.DefaultIconsProvider
import com.magicleap.magicscript.icons.ExternalIconsProvider
import com.magicleap.magicscript.icons.IconsRepositoryImpl
import com.magicleap.magicscript.scene.nodes.button.UiButtonNode
import com.magicleap.magicscript.scene.nodes.layouts.UiLinearLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.HorizontalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.LinearLayoutManager
import com.magicleap.magicscript.scene.nodes.layouts.manager.VerticalLinearLayoutManager
import com.magicleap.magicscript.scene.nodes.props.ORIENTATION_VERTICAL

class PrismMenu(
    private val context: Context,
    private val arResourcesProvider: ArResourcesProvider
) :
    UiLinearLayout(createProps(), createLayoutManager()) {

    companion object {
        private const val MENU_ICON_SIZE = 0.15
        private const val MENU_TEXT_SIZE = 0.15
        private const val MENU_SHOW_PRISM_ICON = "generic_three_dimensional"

        private fun createProps(): JavaOnlyMap {
            return JavaOnlyMap.of(
                PROP_ORIENTATION, ORIENTATION_VERTICAL,
                PROP_ALIGNMENT, "bottom-center",
                PROP_DEFAULT_ITEM_ALIGNMENT, "center-center",
                PROP_DEFAULT_ITEM_PADDING, JavaOnlyArray.of(0.02, 0.02, 0.02, 0.02)
            )
        }

        private fun createLayoutManager(): LinearLayoutManager {
            return LinearLayoutManager(
                VerticalLinearLayoutManager(),
                HorizontalLinearLayoutManager()
            )
        }
    }

    var onButtonClickListener: (() -> Unit)? = null

    override fun build() {
        super.build()

        val labelProps = JavaOnlyMap.of(
            UiTextNode.PROP_TEXT, "Catalog app",
            UiTextNode.PROP_TEXT_SIZE, MENU_TEXT_SIZE,
            PROP_ALIGNMENT, "center-center"
        )

        val label = UiTextNode(
            initProps = labelProps,
            context = context,
            viewRenderableLoader = ViewRenderableLoaderImpl(context, arResourcesProvider),
            nodeClipper = TextureClipper(),
            fontProvider = FontProviderImpl(context, AndroidFontProvider())
        ).apply {
            build()
        }

        val buttonShowProps = JavaOnlyMap.of(
            UiButtonNode.PROP_WIDTH, MENU_ICON_SIZE,
            UiButtonNode.PROP_HEIGHT, MENU_ICON_SIZE,
            UiButtonNode.PROP_ICON_TYPE, MENU_SHOW_PRISM_ICON
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
            onClickListener = {
                onButtonClickListener?.invoke()
            }
        }

        addContent(label)
        addContent(buttonShow)
    }
}