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

package com.reactlibrary.scene

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.icons.IconsRepository
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.ifContainsString
import kotlinx.android.synthetic.main.color_picker_dialog.*

class DialogNode(
        initProps: ReadableMap,
        private val iconsRepository: IconsRepository
) : TransformNode(initProps, false, false) {

    companion object {
        const val PROP_TITLE = "title"
        const val PROP_TEXT = "text"
        const val PROP_CONFIRM_TEXT = "confirmText"
        const val PROP_CONFIRM_ICON = "confirmIcon"
        const val PROP_CANCEL_TEXT = "cancelText"
        const val PROP_CANCEL_ICON = "cancelIcon"
    }

    var onDialogConfirmListener: (() -> Unit)? = null
    var onDialogCancelListener: (() -> Unit)? = null

    private var dialog: AlertDialog? = null

    private val onConfirm = DialogInterface.OnClickListener { _, _ ->
        onDialogConfirmListener?.invoke()
    }

    private val onCancel = DialogInterface.OnClickListener { _, _ ->
        onDialogCancelListener?.invoke()
    }

    override fun build() {
        super.build()

        showDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

    private fun showDialog() {
        val activityContext = ArViewManager.getActivityRef().get() as Context
        val dialogBuilder = AlertDialog.Builder(activityContext)

        dialogBuilder.apply {
            setCancelable(false)

            properties.ifContainsString(PROP_TITLE) { title ->
                setTitle(title)
            }
            properties.ifContainsString(PROP_TEXT) { text ->
                setMessage(text)
            }

            if (properties.containsKey(PROP_CONFIRM_TEXT)) {
                val confirmText = properties.getString(PROP_CONFIRM_TEXT)
                setPositiveButton(confirmText, onConfirm)
            } else {
                setPositiveButton(android.R.string.ok, onConfirm)
            }

            if (properties.containsKey(PROP_CANCEL_TEXT)) {
                val confirmText = properties.getString(PROP_CANCEL_TEXT)
                setNegativeButton(confirmText, onCancel)
            } else {
                setNegativeButton(android.R.string.cancel, onCancel)
            }
        }


        this.dialog = dialogBuilder.create()
        this.dialog?.apply {
            this.setOnShowListener {
                properties.ifContainsString(PROP_CONFIRM_ICON) { iconRes ->
                    if(iconRes != null) {
                        val icon = iconsRepository.getIcon(iconRes, false)
                        getButton(AlertDialog.BUTTON_POSITIVE).setCompoundDrawables(icon, null, null, null)
                    }
                }
                properties.ifContainsString(PROP_CANCEL_ICON) { iconRes ->
                    if(iconRes != null) {
                        val icon = iconsRepository.getIcon(iconRes, false)
                        getButton(AlertDialog.BUTTON_NEGATIVE).setCompoundDrawables(icon, null, null, null)
                    }
                }
            }
        }
        this.dialog?.show()
    }
}