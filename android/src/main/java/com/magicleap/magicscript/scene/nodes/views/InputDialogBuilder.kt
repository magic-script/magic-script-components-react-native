/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript.scene.nodes.views

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.magicleap.magicscript.R
import com.magicleap.magicscript.utils.setTextAndMoveCursor
import kotlinx.android.synthetic.main.edit_text_2d.view.*

class InputDialogBuilder(context: Context, multiline: Boolean, passwordMode: Boolean) :
    AlertDialog.Builder(context, R.style.InputDialogTheme) {

    companion object {
        private const val MAX_HEIGHT = 100
    }

    private var editText: EditText
    private var tvConfirm: TextView
    private var onSubmitListener: ((text: String) -> Unit)? = null
    private var onCloseListener: (() -> Unit)? = null

    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.edit_text_2d, null)
        editText = contentView.edit_text_2d

        if (passwordMode) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        if (multiline) {
            editText.inputType = editText.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            editText.maxHeight = (MAX_HEIGHT * context.resources.displayMetrics.density).toInt()
        }
        editText.imeOptions = EditorInfo.IME_ACTION_DONE
        tvConfirm = contentView.tv_confirm
        setView(contentView)
    }

    override fun create(): AlertDialog {
        val dialog = super.create()
        dialog.setOnDismissListener {
            onCloseListener?.invoke()
        }

        // show keyboard
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        editText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                onSubmitListener?.invoke(editText.text.toString())
                dialog.dismiss()
                true
            } else false
        }

        tvConfirm.setOnClickListener {
            onSubmitListener?.invoke(editText.text.toString())
            dialog.dismiss()
        }

        return dialog
    }

    fun setInputText(text: String): AlertDialog.Builder {
        editText.setTextAndMoveCursor(text)
        return this
    }

    fun setEntryMode(mode: EntryMode) {
        val inputType = when (mode) {
            EntryMode.EMAIL -> {
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            EntryMode.NUMERIC -> {
                InputType.TYPE_CLASS_NUMBER
            }
            EntryMode.NORMAL -> {
                InputType.TYPE_CLASS_TEXT
            }
        }
        editText.inputType = editText.inputType or inputType
    }

    fun setMaxCharacters(maxChars: Int): AlertDialog.Builder {
        val lengthFilter = InputFilter.LengthFilter(maxChars)
        editText.filters = arrayOf(lengthFilter)
        return this
    }

    fun setOnSubmitListener(callback: (text: String) -> Unit): AlertDialog.Builder {
        this.onSubmitListener = callback
        return this
    }

    fun setOnCloseListener(callback: () -> Unit): AlertDialog.Builder {
        this.onCloseListener = callback
        return this
    }

    enum class EntryMode { NORMAL, EMAIL, NUMERIC }
}