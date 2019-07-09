package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Utils
import com.reactlibrary.utils.afterTextChanged
import com.reactlibrary.utils.logMessage
import kotlinx.android.synthetic.main.text_edit.view.*


class UiTextEditNode(props: ReadableMap, context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_TEXT = "text"
        private const val PROP_TEXT_SIZE = "textSize"
        private const val PROP_CHARACTER_SPACING = "charSpacing"
    }

    private var addedNativeView = false
    private var cursorVisible = false
    private var text = ""

    override fun provideView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.text_edit, null)
        view.text_edit.setOnClickListener {
            logMessage("click")
            if (!addedNativeView) { //append view above the keyboard
                val editTextContainer = LayoutInflater.from(context).inflate(R.layout.edit_text_native, null)
                val editText = editTextContainer.findViewById<EditText>(R.id.et_native)

                ArViewManager.addViewToContainer(editTextContainer)

                view.post {
                    // show the keyboard
                    editText.requestFocus()
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm!!.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }

                editText.afterTextChanged { text ->
                    this.text = text
                    view.text_edit.text = text
                }

                startCursorAnimation()

                addedNativeView = true
                logMessage("added native view")
            }
        }
        return view
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setText(props)
        setTextSize(props)
        setCharacterSpacing(props)
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
            hintTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun setCharacterSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTER_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTER_SPACING)
            view.text_edit.letterSpacing = spacing.toFloat()
        }
    }


    private fun startCursorAnimation() {
        val runnable = object : Runnable {
            override fun run() {
                if (cursorVisible) {
                    view.text_edit.text = text
                } else {
                    val textWithCursor = "$text|"
                    view.text_edit.text = textWithCursor
                }
                cursorVisible = !cursorVisible
                view.text_edit.postDelayed(this, 400)
            }

        }
        runnable.run()
    }

}