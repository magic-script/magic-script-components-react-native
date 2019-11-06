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
package com.reactlibrary.scene.nodes.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import com.larswerkman.holocolorpicker.ColorPicker
import com.reactlibrary.R
import kotlinx.android.synthetic.main.color_picker_dialog.*

open class ColorPickerDialog(context: Context) : Dialog(context) {

    companion object {
        const val ALPHA_FORMAT = "%.2f"
    }

    var initialColor: Int = 0
    var onResult: ((Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.color_picker_dialog)

        val picker = findViewById<ColorPicker>(R.id.picker)
        picker.addSVBar(svbar)
        picker.addOpacityBar(opacitybar)

        picker.oldCenterColor = initialColor
        picker.color = initialColor

        updateColorText(initialColor)
        setupListeners(picker)
    }

    private fun setupListeners(picker: ColorPicker) {
        picker.onColorChangedListener =
            ColorPicker.OnColorChangedListener { color ->
                updateColorText(color)
            }

        confirm.setOnClickListener {
            onResult?.invoke(picker.color)
            dismiss()
        }

        cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun updateColorText(color: Int) {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alpha = Color.alpha(color).toDouble() / 255

        rvalue.text = red.toString()
        gvalue.text = green.toString()
        bvalue.text = blue.toString()
        avalue.text = ALPHA_FORMAT.format(alpha)
    }
}