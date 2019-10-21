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

package com.reactlibrary.scene.nodes

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.icons.IconsProvider
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.Vector2
import kotlinx.android.synthetic.main.image.view.*

open class UiImageNode(initProps: ReadableMap,
                       context: Context,
                       viewRenderableLoader: ViewRenderableLoader,
                       private val iconsProvider: IconsProvider)
    : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_FILE_PATH = "filePath"
        const val PROP_ICON = "icon"
        const val PROP_COLOR = "color"
        const val PROP_FRAME = "useFrame"
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.image, null)
    }

    override fun getDesiredSize(): Vector2 {
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
        setColor(props)
        setUseFrame(props)
    }

    private fun setImagePath(props: Bundle) {
        val imageUri = PropertiesReader.readImagePath(props, PROP_FILE_PATH, context)
        if (imageUri != null) {
            Glide.with(context)
                    .load(imageUri)
                    .into(view.image_view)

            val color = PropertiesReader.readColor(props, PROP_COLOR)
            if (color != null) {
                (view.image_view as ImageView).setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    private fun setIcon(props: Bundle) {
        val iconName = props.getString(PROP_ICON)
        if (iconName != null) {
            val icon = iconsProvider.provideIcon(iconName)
            if (icon != null) {
                view.image_view.setImageDrawable(icon)
            }
        }
    }

    private fun setColor(props: Bundle) {
        val color = PropertiesReader.readColor(props, PROP_COLOR)
        if (color != null) {
            if (properties.containsKey(PROP_FILE_PATH)) {
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

}