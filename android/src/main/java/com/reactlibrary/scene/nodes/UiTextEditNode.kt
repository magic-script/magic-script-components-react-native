package com.reactlibrary.scene.nodes

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.rendering.ViewRenderable
import com.reactlibrary.ArViewManager
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.*
import kotlinx.android.synthetic.main.text_edit.view.*

class UiTextEditNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_TEXT = "text"
        private const val PROP_HINT = "hint"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_TEXT_ALIGNMENT = "textAlignment"
        private const val PROP_TEXT_COLOR = "textColor"
        private const val PROP_CHARACTER_SPACING = "charSpacing"
        private const val PROP_PASSWORD = "password"
        private const val PROP_MULTILINE = "multiline"
        private const val PROP_TEXT_PADDING = "padding"

        private const val DEFAULT_TEXT_SIZE = 0.025 // in meters
        private const val DEFAULT_WIDTH = 0.4 // in meters
        private const val MULTILINE_BOX_HEIGHT = 0.12F // in meters
    }

    var textChangedListener: ((text: String) -> Unit)? = null

    private var cursorVisible = false
    private var text = ""
    private var hint = ""
    private val mainHandler = Handler(Looper.getMainLooper())
    private var textColor = context.getColor(R.color.text_color_default)

    private val cursorAnimationRunnable = object : Runnable {
        override fun run() {
            if (cursorVisible) {
                view.text_edit.text = generateVisibleText(text)
            } else {
                val textWithCursor = generateVisibleText(text) + "|"
                view.text_edit.text = textWithCursor
            }
            cursorVisible = !cursorVisible
            mainHandler.postDelayed(this, 400)
        }
    }

    init {
        // set default values of properties
        if (!properties.containsKey(PROP_TEXT_SIZE)) {
            properties.putDouble(PROP_TEXT_SIZE, DEFAULT_TEXT_SIZE)
        }

        if (!properties.containsKey(PROP_WIDTH)) {
            properties.putDouble(PROP_WIDTH, DEFAULT_WIDTH)
        }

        if (!properties.containsKey(PROP_MULTILINE)) {
            properties.putBoolean(PROP_MULTILINE, false)
        }

    }

    override fun provideView(context: Context): View {
        val v = LayoutInflater.from(context).inflate(R.layout.text_edit, null)

        val multiline = properties.getBoolean(PROP_MULTILINE, false)
        if (multiline) {
            val textBoxHeight = Utils.metersToPx(MULTILINE_BOX_HEIGHT, context)
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, textBoxHeight)
            v.sv_text_edit.layoutParams = params
        }
        v.text_edit.setOnClickListener {
            logMessage("on text edit click")
            val activity = ArViewManager.getActivityRef().get()
            if (activity != null) {
                startCursorAnimation()
                view.text_edit_underline.visibility = View.INVISIBLE
                view.background = context.getDrawable(R.drawable.text_edit_outline)
                val paddingLeft = if (multiline) 15 else 10
                view.setPadding(paddingLeft, 5, 10, 5)
                view.text_edit.setTextColor(textColor)
                showInputDialog(activity)
            }
        }
        return v
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setText(props)
        setHint(props)
        setTextSize(props)
        setTextAlignment(props)
        setTextColor(props)
        setCharacterSpacing(props)
        setMultiline(props)
        setTextPadding(props)
    }

    override fun getHorizontalAlignment(): ViewRenderable.HorizontalAlignment {
        return ViewRenderable.HorizontalAlignment.LEFT
    }

    override fun getVerticalAlignment(): ViewRenderable.VerticalAlignment {
        return ViewRenderable.VerticalAlignment.TOP
    }

    private fun setText(props: Bundle) {
        val text = props.getString(PROP_TEXT)
        if (text != null) {
            setText(text)
        }
    }

    private fun setText(txt: String) {
        view.text_edit.text = generateVisibleText(txt)
        view.text_edit.setTextColor(textColor) // clear hint color
        this.text = txt
    }

    private fun setHint(props: Bundle) {
        val hint = props.getString(PROP_HINT)
        if (hint != null) {
            setHint(hint)
        }
    }

    private fun setHint(hint: String) {
        this.hint = hint
        view.text_edit.text = hint
        view.text_edit.setTextColor(context.getColor(R.color.text_color_hint))
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE).toFloat()
            val size = Utils.metersToPx(sizeMeters, view.context).toFloat()
            view.text_edit.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun setTextAlignment(props: Bundle) {
        when (props.getString(PROP_TEXT_ALIGNMENT)) {
            "left" -> {
                view.text_edit.gravity = Gravity.LEFT
            }
            "center" -> {
                view.text_edit.gravity = Gravity.CENTER_HORIZONTAL
            }
            "right" -> {
                view.text_edit.gravity = Gravity.RIGHT
            }
        }
    }

    private fun setTextColor(props: Bundle) {
        if (props.containsKey(PROP_TEXT_COLOR)) {
            val color = props.getSerializable(PROP_TEXT_COLOR)?.toVector4()?.toColor()
            if (color != null) {
                this.textColor = color
                if (view.text_edit.text.toString() != hint) {
                    view.text_edit.setTextColor(color)
                }
            }
        }
    }

    private fun setCharacterSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTER_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTER_SPACING)
            view.text_edit.letterSpacing = spacing.toFloat()
        }
    }

    private fun setMultiline(props: Bundle) {
        if (props.containsKey(PROP_MULTILINE)) {
            val isMultiline = props.getBoolean(PROP_MULTILINE)
            view.text_edit.setSingleLine(!isMultiline)
            //  view.text_edit.setLines(5)
        }
    }

    private fun setTextPadding(props: Bundle) {
        props.getSerializable(PROP_TEXT_PADDING)?.toVector4()?.let {
            val paddingMeters = Padding(it)
            val top = Utils.metersToPx(paddingMeters.top, view.context)
            val right = Utils.metersToPx(paddingMeters.right, view.context)
            val bottom = Utils.metersToPx(paddingMeters.bottom, view.context)
            val left = Utils.metersToPx(paddingMeters.left, view.context)
            view.text_edit.setPadding(left, top, right, bottom)
        }
    }

    private fun startCursorAnimation() {
        cursorAnimationRunnable.run()
    }

    private fun stopCursorAnimation() {
        mainHandler.removeCallbacks(cursorAnimationRunnable)
        view.text_edit.text = generateVisibleText(text) // remove cursor if present
    }

    private fun showInputDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.input_dialog_title)
        val multiline = properties.getBoolean(PROP_MULTILINE)
        val resId = if (multiline) R.layout.edit_text_2d_multiline else R.layout.edit_text_2d
        val nativeEditText = LayoutInflater.from(context).inflate(resId, null)

        val input = nativeEditText.findViewById(R.id.edit_text_2d) as EditText
        val visibleText = generateVisibleText(text)
        if (isPassword()) {
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        if (multiline) {
            input.inputType = input.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
        input.setTextAndMoveCursor(visibleText)
        builder.setView(nativeEditText)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val txt = input.text.toString()
            if (txt != text) {
                setText(txt)
                textChangedListener?.invoke(txt)
            }
        }
        builder.setNegativeButton(android.R.string.cancel, null)

        val dialog = builder.create()
        // show keyboard
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        dialog.setOnDismissListener {
            stopCursorAnimation()
            view.background = null
            view.setPadding(0, 0, 0, 0)
            view.text_edit_underline.visibility = View.VISIBLE
        }

        dialog.show()
    }


    private fun generateVisibleText(input: String): String {
        return if (isPassword()) {
            "*".repeat(input.length)
        } else {
            input
        }
    }

    private fun isPassword(): Boolean {
        return properties.getBoolean(PROP_PASSWORD, false)
    }

}