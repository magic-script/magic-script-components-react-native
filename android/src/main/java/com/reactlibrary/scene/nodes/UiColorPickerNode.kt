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
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.font.FontProvider
import com.reactlibrary.icons.IconsRepository
import com.reactlibrary.scene.nodes.views.ColorPickerDialog
import com.reactlibrary.scene.nodes.views.CustomButton
import com.reactlibrary.utils.PropertiesReader
import com.reactlibrary.utils.putDefaultString
import com.reactlibrary.utils.toJsColorArray


open class UiColorPickerNode @JvmOverloads constructor(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    private val fontProvider: FontProvider,
    private val iconsRepo: IconsRepository,
    private val colorPickerDialog: ColorPickerDialog = ColorPickerDialog(ArViewManager.getActivityRef().get() as Context)
) : UiButtonNode(initProps, context, viewRenderableLoader, fontProvider, iconsRepo) {

    companion object {
        const val PROP_COLOR = "color"
        const val PROP_STARTING_COLOR = "startingColor"
        const val PROP_HEIGHT = "height"
        const val PROP_WIDTH = "width"
    }

    init {
        properties.apply {
            putDefaultString(PROP_STARTING_COLOR, "[1.0, 1.0, 1.0, 1.0]")
        }

        colorPickerDialog.apply {
            onConfirm = { color ->
                selectedColor = color
                this@UiColorPickerNode.onColorConfirmed?.invoke(color.toJsColorArray())
            }
            onCanceled = {
                this@UiColorPickerNode.onColorCanceled?.invoke()
            }
            onChanged = { color ->
                this@UiColorPickerNode.onColorChanged?.invoke(color.toJsColorArray())
            }
        }
    }

    var onColorConfirmed: ((color: Array<Double>) -> Unit)? = null
    var onColorCanceled: (() -> Unit)? = null
    var onColorChanged: ((color: Array<Double>) -> Unit)? = null

    private var selectedColor = 0
        set(value) {
            field = value
            (view as CustomButton).apply {
                val hexColor = Integer.toHexString(value)
                setIconColor(value)
                text = "#$hexColor"
                setNeedsRebuild()
            }

        }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.color_picker, null)
    }

    override fun setupView() {
        super.setupView()

        (view as CustomButton).setIcon(ColorDrawable(Color.WHITE))
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        if (props.containsKey(PROP_HEIGHT)) {
            setNeedsRebuild()
        }

        setColor(props)
    }

    private fun setColor(props: Bundle) {
        val color = PropertiesReader.readColor(props, PROP_COLOR)
        val startingColor = PropertiesReader.readColor(props, PROP_STARTING_COLOR)
        if (color != null) {
            selectedColor = color
        } else if (startingColor != null) {
            selectedColor = startingColor
        }
    }

    override fun onViewClick() {
        super.onViewClick()
        colorPickerDialog.initialColor = selectedColor
        colorPickerDialog.show()
    }
}