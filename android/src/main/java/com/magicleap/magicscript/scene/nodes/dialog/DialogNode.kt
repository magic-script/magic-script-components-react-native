/*
 *  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.R
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.views.CustomAlertDialogBuilder
import com.magicleap.magicscript.scene.nodes.views.DialogProvider
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read
import java.lang.ref.WeakReference


class DialogNode(
    initProps: ReadableMap,
    context: Context,
    private val iconsRepository: IconsRepository,
    private val dialogProvider: DialogProvider
) : TransformNode(initProps, false) {

    companion object {
        const val PROP_TITLE = "title"
        const val PROP_TEXT = "message"
        const val PROP_CONFIRM_TEXT = "confirmText"
        const val PROP_CONFIRM_ICON = "confirmIcon"
        const val PROP_CANCEL_TEXT = "cancelText"
        const val PROP_CANCEL_ICON = "cancelIcon"
        const val PROP_EXPIRATION_TIME = "expireTime"
        const val TIMER_HANDLER_EVENT = 1
        const val PROP_BUTTON_TYPE = "buttonType"
        const val PROP_DIALOG_TYPE = "type"
    }

    var onDialogConfirmListener: (() -> Unit)? = null
        set(value) {
            dialog?.findViewById<ConstraintLayout>(R.id.confirm_layout)?.setOnClickListener {
                value?.invoke()
                dialog?.dismiss()
            }
            field = value
        }

    var onDialogCancelListener: (() -> Unit)? = null
        set(value) {
            dialog?.findViewById<ConstraintLayout>(R.id.cancel_layout)?.setOnClickListener {
                value?.invoke()
                dialog?.dismiss()
            }
            field = value
        }

    var onDialogExpiredListener: (() -> Unit)? = null

    private var dialog: AlertDialog? = null

    private val timerHandler =
        TimerHandler(WeakReference(this))

    init {
        properties.apply {
            putDefault(PROP_DIALOG_TYPE, DialogType.DUAL_ACTION)
            putDefault(PROP_CONFIRM_TEXT, context.getString(R.string.confirm))
            putDefault(PROP_CANCEL_TEXT, context.getString(R.string.cancel))
            putDefault(PROP_BUTTON_TYPE, CustomAlertDialogBuilder.BUTTON_TYPE_TEXT)
        }
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
        val dialogBuilder = dialogProvider.provideCustomAlertDialogBuilder() ?: return

        dialogBuilder.apply {
            properties.read<String>(PROP_TITLE)?.let { title ->
                setTitle(title)
            }
            properties.read<String>(PROP_TEXT)?.let { text ->
                setDescription(text)
            }
            properties.read<String>(PROP_CONFIRM_TEXT)?.let { text ->
                setConfirmationText(text)
                setOnDialogConfirmClick(onDialogConfirmListener)
            }
            properties.read<String>(PROP_CONFIRM_ICON)?.let { iconName ->
                val icon = iconsRepository.getIcon(iconName, false)
                setConfirmationIcon(icon)
            }
            properties.read<String>(PROP_CANCEL_TEXT)?.let { text ->
                setCancelText(text)
                setOnDialogCancelClick(onDialogCancelListener)
            }
            properties.read<String>(PROP_CANCEL_ICON)?.let { iconName ->
                val icon = iconsRepository.getIcon(iconName, false)
                setCancelIcon(icon)
            }
            properties.read<String>(PROP_BUTTON_TYPE)?.let { buttonType ->
                setButtonType(buttonType)
            }
            properties.read<String>(PROP_DIALOG_TYPE)?.let { dialogType ->
                setDialogType(dialogType)
            }
        }
        val alertDialog: AlertDialog? = dialogBuilder.create()
        alertDialog?.let {
            it.show()
            it.window?.setBackgroundDrawable(null)
            it.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        this.dialog = alertDialog

        properties.read<Double>(PROP_EXPIRATION_TIME)?.let { time ->
            if (properties.read<String>(PROP_DIALOG_TYPE) == DialogType.TIMED) {
                val msg = timerHandler.obtainMessage(TIMER_HANDLER_EVENT)
                val timeInMillis = (time * 1000).toLong()
                timerHandler.sendMessageDelayed(msg, timeInMillis)
            }
        }
    }

    class TimerHandler(private val dialogNodeRef: WeakReference<DialogNode>) : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                TIMER_HANDLER_EVENT -> {
                    val dialogNode = dialogNodeRef.get()
                    val dialog = dialogNode?.dialog
                    if (dialog != null && dialog.isShowing) {
                        dialog.dismiss()
                        dialogNode.onDialogExpiredListener?.invoke()
                    }
                }
            }
        }
    }
}