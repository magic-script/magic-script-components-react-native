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