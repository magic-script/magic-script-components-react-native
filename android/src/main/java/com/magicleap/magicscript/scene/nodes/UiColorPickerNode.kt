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
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.button.UiButtonNode
import com.magicleap.magicscript.scene.nodes.views.CustomButton
import com.magicleap.magicscript.scene.nodes.views.DialogProvider
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.readColor
import com.magicleap.magicscript.utils.toJsColorArray


open class UiColorPickerNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper,
    fontProvider: FontProvider,
    iconsRepo: IconsRepository,
    private val dialogProvider: DialogProvider
) : UiButtonNode(
    initProps,
    context,
    viewRenderableLoader,
    nodeClipper,
    fontProvider,
    iconsRepo
) {

    companion object {
        const val PROP_COLOR = "color"
        const val PROP_STARTING_COLOR = "startingColor"
        const val PROP_HEIGHT = "height"
        const val PROP_WIDTH = "width"
    }

    init {
        properties.apply {
            putDefault(PROP_STARTING_COLOR, "[1.0, 1.0, 1.0, 1.0]")
        }
    }

    var onColorConfirmed: ((color: Array<Double>) -> Unit)? = null
    var onColorCanceled: (() -> Unit)? = null
    var onColorChanged: ((color: Array<Double>) -> Unit)? = null

    private var selectedColor = readColor(properties)

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.color_picker, null)
    }

    override fun setupView() {
        super.setupView()

        (view as CustomButton).setIcon(ColorDrawable(Color.WHITE))

        val hexColor = Integer.toHexString(selectedColor)
        (view as CustomButton).apply {
            setIconColor(selectedColor)
            text = "#$hexColor"
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        setColor(props)
    }

    private fun setColor(props: Bundle) {
        val color = readColor(props)
        if (color != selectedColor) {
            selectedColor = color
            setNeedsRebuild()
        }
    }

    override fun onViewClick() {
        super.onViewClick()
        showDialog()
    }

    private fun readColor(props: Bundle): Int {
        return props.readColor(PROP_COLOR) ?: (props.readColor(PROP_STARTING_COLOR) ?: Color.WHITE)
    }

    private fun showDialog() {
        val colorPickerDialog = dialogProvider.provideColorPickerDialog() ?: return

        colorPickerDialog.apply {
            onConfirm = { color ->
                selectedColor = color
                setNeedsRebuild(force = true)
                this@UiColorPickerNode.onColorConfirmed?.invoke(color.toJsColorArray())
            }
            onCanceled = {
                this@UiColorPickerNode.onColorCanceled?.invoke()
            }
            onChanged = { color ->
                this@UiColorPickerNode.onColorChanged?.invoke(color.toJsColorArray())
            }
        }
        colorPickerDialog.initialColor = selectedColor
        colorPickerDialog.show()
    }

}