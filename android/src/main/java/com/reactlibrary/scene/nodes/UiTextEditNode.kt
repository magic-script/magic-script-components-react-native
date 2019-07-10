package com.reactlibrary.scene.nodes

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.setTextAndMoveCursor
import kotlinx.android.synthetic.main.text_edit.view.*

class UiTextEditNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_CHARACTER_SPACING = "charSpacing"
        private const val PROP_PASSWORD = "password"
    }

    private var cursorVisible = false
    private var text = ""
    private val mainHandler = Handler(Looper.getMainLooper())

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
        // set default width
        if (!properties.containsKey(PROP_WIDTH)) {
            properties.putDouble(PROP_WIDTH, 0.4)
        }
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text_edit, null)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setText(props)
        setTextSize(props)
        setCharacterSpacing(props)
    }

    override fun onClick() {
        super.onClick()
        val activity = ArViewManager.getActivityRef().get()
        if (activity != null) {
            startCursorAnimation()
            showInputDialog(activity)
        }
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            view.text_edit.text = generateVisibleText(text)
            this.text = text
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE)
            val size = Utils.metersToPx(sizeMeters, view.context).toFloat()
            view.text_edit.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            view.text_edit_hint.setTextSize(TypedValue.COMPLEX_UNIT_PX, 1.5f * size)
        }
    }

    private fun setCharacterSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTER_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTER_SPACING)
            view.text_edit.letterSpacing = spacing.toFloat()
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
        val viewInflated = LayoutInflater.from(context).inflate(R.layout.edit_text_2d, null)
        val input = viewInflated.findViewById(R.id.edit_text_2d) as EditText
        val visibleText = generateVisibleText(text)
        if (isPassword()) {
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        input.setTextAndMoveCursor(visibleText)
        builder.setView(viewInflated)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            text = input.text.toString()
            view.text_edit.text = generateVisibleText(text)
        }
        builder.setNegativeButton(android.R.string.cancel, null)

        val dialog = builder.create()
        // show keyboard
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        dialog.setOnDismissListener {
            stopCursorAnimation()
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