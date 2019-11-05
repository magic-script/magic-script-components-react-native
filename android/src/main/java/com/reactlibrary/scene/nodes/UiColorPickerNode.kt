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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.R
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.views.ColorPickerDialog
import com.reactlibrary.utils.*
import kotlinx.android.synthetic.main.color_picker.view.*

open class UiColorPickerNode @JvmOverloads constructor(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    private val colorPickerDialog: ColorPickerDialog = ColorPickerDialog(ArViewManager.getActivityRef().get() as Context)
) : UiNode(initProps, context, viewRenderableLoader) {

    companion object {
        const val PROP_COLOR = "color"
        const val PROP_HEIGHT = "height"
    }

    init {
        properties.apply {
            putDefaultString(PROP_COLOR, "[1.0, 1.0, 1.0, 1.0]")
            putDefaultDouble(PROP_HEIGHT, 1.0)
        }

        colorPickerDialog.onResult = { result ->
            selectedColor = result
            onColorSelected?.invoke(result.toJsColorArray())
        }
    }

    var onColorSelected: ((color: Array<Double>) -> Unit)? = null

    private var selectedColor = 0
        set(value) {
            field = value
            view.selectedColor.setBackgroundColor(value)
        }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.color_picker, null)
    }

    override fun provideDesiredSize(): Vector2 {
        val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble()).toFloat()

        return Vector2(height * 3, height)
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
        if (color != null) {
            selectedColor = color
        }
    }

    override fun onViewClick() {
        super.onViewClick()
        colorPickerDialog.initialColor = selectedColor
        colorPickerDialog.show()
    }
}