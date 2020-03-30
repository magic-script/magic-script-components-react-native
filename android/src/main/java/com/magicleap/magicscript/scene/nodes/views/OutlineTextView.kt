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

package com.magicleap.magicscript.scene.nodes.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import java.lang.reflect.Field


class OutlineTextView(context: Context, attributeSet: AttributeSet): TextView(context, attributeSet) {

    private var colorField: Field? = null
    private var textColor = 0
    private val outlineColor = Color.BLACK

    init {
        try {
            colorField = TextView::class.java.getDeclaredField("mCurTextColor")
            colorField?.isAccessible = true
            textColor = textColors.defaultColor
            paint.strokeWidth = 4f

        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
            colorField = null
        }
    }

    override fun setTextColor(color: Int) {
        textColor = color
        super.setTextColor(color)
    }

    override fun onDraw(canvas: Canvas?) {
        if (colorField != null) {
            setColorField(outlineColor)
            paint.style = Paint.Style.STROKE
            super.onDraw(canvas)
            setColorField(textColor)
            paint.style = Paint.Style.FILL
        }
        super.onDraw(canvas)
    }

    private fun setColorField(color: Int) {
        try {
            colorField?.setInt(this, color)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}