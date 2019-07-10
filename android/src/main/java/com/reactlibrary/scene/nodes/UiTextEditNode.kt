package com.reactlibrary.scene.nodes

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.logMessage
import com.reactlibrary.utils.setTextAndMoveCursor
import kotlinx.android.synthetic.main.text_edit.view.*

class UiTextEditNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_CHARACTER_SPACING = "charSpacing"
    }

    private var cursorVisible = false
    private var text = ""

    init {
        // set default width
        if (!properties.containsKey(PROP_WIDTH)) {
            properties.putDouble(PROP_WIDTH, 0.4)
        }
    }

    override fun provideView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.text_edit, null)
        view.text_edit.setOnClickListener {
            logMessage("UiTextNode on click")
            val activity = ArViewManager.getActivityRef().get()
            if (activity != null) {
                showInputDialog(activity)
            }
        }
        startCursorAnimation(view.text_edit)
        return view
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setText(props)
        setTextSize(props)
        setCharacterSpacing(props)
    }

    private fun startCursorAnimation(textEdit: TextView) {
        val runnable = object : Runnable {
            override fun run() {
                if (cursorVisible) {
                    textEdit.text = text
                } else {
                    val textWithCursor = "$text|"
                    textEdit.text = textWithCursor
                }
                cursorVisible = !cursorVisible
                textEdit.postDelayed(this, 400)
            }
        }
        runnable.run()
    }

    private fun setText(properties: Bundle) {
        val text = properties.getString(PROP_TEXT)
        if (text != null) {
            view.findViewById<TextView>(R.id.text_edit).text = text
            this.text = text
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE)
            val size = Utils.metersToPx(sizeMeters, view.context).toFloat()
            val hintTv = view.findViewById<TextView>(R.id.text_edit_hint)
            val editTv = view.findViewById<TextView>(R.id.text_edit)
            editTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            hintTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 1.5f * size)
        }
    }

    private fun setCharacterSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTER_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTER_SPACING)
            view.text_edit.letterSpacing = spacing.toFloat()
        }
    }

    private fun showInputDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.input_dialog_title)
        val viewInflated = LayoutInflater.from(context).inflate(R.layout.edit_text_2d, null)
        val input = viewInflated.findViewById(R.id.edit_text_2d) as EditText
        input.setTextAndMoveCursor(text)
        builder.setView(viewInflated)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            text = input.text.toString()
            view.text_edit.text = text
        }
        builder.setNegativeButton(android.R.string.cancel, null)

        val dialog = builder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show()
    }

}