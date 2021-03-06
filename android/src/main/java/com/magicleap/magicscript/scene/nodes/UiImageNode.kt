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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.utils.*
import kotlinx.android.synthetic.main.image.view.*

open class UiImageNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper,
    private val iconsRepo: IconsRepository
) : UiNode(initProps, context, viewRenderableLoader, nodeClipper) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_FILE_PATH = "filePath"
        const val PROP_ICON = "icon"
        const val PROP_COLOR = "color"
        const val PROP_FRAME = "useFrame"
        const val PROP_USE_DEFAULT_ICON = "useDefaultIcon"
        const val PROP_OPAQUE = "opaque"
        const val PROP_CONTENT_MODE = "fit"

        const val CONTENT_MODE_FILL = "aspect-fill"
        const val CONTENT_MODE_FIT = "aspect-fit"
        const val CONTENT_MODE_STRETCH = "stretch"
    }

    init {
        properties.putDefault(PROP_CONTENT_MODE, CONTENT_MODE_STRETCH)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.image, null)
    }

    override fun provideDesiredSize(): Vector2 {
        val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        var width = properties.getDouble(PROP_WIDTH, WRAP_CONTENT_DIMENSION.toDouble())
        if (width.toFloat() == WRAP_CONTENT_DIMENSION) {
            width = height // for icons support
        }
        return Vector2(width.toFloat(), height.toFloat())
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_WIDTH) || props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        setImagePath(props)
        setIcon(props)
        setUseDefaultIcon(props)
        setColor(props)
        setUseFrame(props)
        setIsOpaque(props)
        setContentMode(props)
    }

    private fun setImagePath(props: Bundle) {
        val imageUri = props.readImagePath(PROP_FILE_PATH, context)
        if (imageUri != null) {
            Glide.with(context)
                .load(imageUri)
                // use original size in order to aspect-fill work
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(view.image_view)

            val color = props.readColor(PROP_COLOR)
            if (color != null) {
                (view.image_view as ImageView).setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    private fun setIcon(props: Bundle) {
        val forceDefault = properties.getBoolean(PROP_USE_DEFAULT_ICON, false)
        val iconName = props.getString(PROP_ICON)
        if (iconName != null) {
            val icon = iconsRepo.getIcon(iconName, forceDefault)
            if (icon != null) {
                view.image_view.setImageDrawable(icon)
            }
        }
    }

    private fun setUseDefaultIcon(props: Bundle) {
        if (updatingProperties && props.containsKey(PROP_USE_DEFAULT_ICON)) {
            setIcon(props) // reload icon
        }
    }

    private fun setColor(props: Bundle) {
        val color = props.readColor(PROP_COLOR)
        if (color != null) {
            if (properties.containsKey(PROP_FILE_PATH) || properties.containsKey(PROP_ICON)) {
                // blend color with image
                view.image_view.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            } else { // use color instead of image
                view.image_view.setBackgroundColor(color)
            }
        }
    }

    private fun setUseFrame(props: Bundle) {
        if (props.containsKey(PROP_FRAME)) {
            val useFrame = props.getBoolean(PROP_FRAME)
            if (useFrame) {
                view.setPadding(1, 1, 1, 1)
                view.setBackgroundResource(R.drawable.image_border)
            } else {
                view.setPadding(0, 0, 0, 0)
                view.setBackgroundResource(0)
            }
        }
    }

    private fun setIsOpaque(props: Bundle) {
        props.read<Boolean>(PROP_OPAQUE)?.let { isOpaque ->
            if (isOpaque) {
                val color = properties.readColor(PROP_COLOR) ?: Color.WHITE
                view.image_view.setBackgroundColor(color)
            } else {
                view.image_view.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun setContentMode(props: Bundle) {
        props.read<String>(PROP_CONTENT_MODE)?.let { mode ->
            view.image_view.scaleType = when (mode) {
                CONTENT_MODE_FILL -> ImageView.ScaleType.CENTER
                CONTENT_MODE_FIT -> ImageView.ScaleType.FIT_CENTER
                else -> ImageView.ScaleType.FIT_XY
            }
        }
    }

}