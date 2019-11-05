package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.facebook.react.bridge.ReadableMap
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
    private val colorPickerDialog: ColorPickerDialog = ColorPickerDialog(context)
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

    override fun setupView() {
        super.setupView()

        setupListeners()
    }

    override fun provideDesiredSize(): Vector2 {
        val width = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        val height = properties.getDouble(PROP_HEIGHT, WRAP_CONTENT_DIMENSION.toDouble())
        return Vector2(width.toFloat() * 3, height.toFloat())
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

    private fun setupListeners() {
        view.setOnClickListener {
            colorPickerDialog.initialColor = selectedColor
            colorPickerDialog.show()
        }
    }
}