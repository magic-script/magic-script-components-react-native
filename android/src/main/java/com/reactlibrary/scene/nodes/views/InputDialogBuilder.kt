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

package com.reactlibrary.scene.nodes.views

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.reactlibrary.R
import com.reactlibrary.utils.setTextAndMoveCursor

class InputDialogBuilder(context: Context, multiline: Boolean, passwordMode: Boolean)
    : AlertDialog.Builder(context) {

    private var editText: EditText
    private var onSubmitListener: ((text: String) -> Unit)? = null
    private var onCloseListener: (() -> Unit)? = null

    init {
        val resId = if (multiline) R.layout.edit_text_2d_multiline else R.layout.edit_text_2d
        val nativeEditText = LayoutInflater.from(context).inflate(resId, null)
        editText = nativeEditText.findViewById(R.id.edit_text_2d) as EditText
        if (passwordMode) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        if (multiline) {
            editText.inputType = editText.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }

        editText.imeOptions = EditorInfo.IME_ACTION_DONE

        setView(nativeEditText)
        setPositiveButton(android.R.string.ok) { _, _ ->
            onSubmitListener?.invoke(editText.text.toString())
        }
        setNegativeButton(android.R.string.cancel, null)
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

        return dialog
    }

    fun setInputText(text: String): AlertDialog.Builder {
        editText.setTextAndMoveCursor(text)
        return this
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
}