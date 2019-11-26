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

package com.magicleap.magicscript.scene.nodes

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.LinearLayout
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.R
import com.magicleap.magicscript.icons.IconsRepository
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.utils.DialogProvider
import com.magicleap.magicscript.utils.ifContainsDouble
import com.magicleap.magicscript.utils.ifContainsString
import com.magicleap.magicscript.utils.putDefault
import java.lang.ref.WeakReference

class DialogNode(
        initProps: ReadableMap,
        private val context: Context,
        private val iconsRepository: IconsRepository,
        private val dialogProvider: DialogProvider
) : TransformNode(initProps, false, false) {

    companion object {
        const val PROP_TITLE = "title"
        const val PROP_TEXT = "message"
        const val PROP_CONFIRM_TEXT = "confirmText"
        const val PROP_CONFIRM_ICON = "confirmIcon"
        const val PROP_CANCEL_TEXT = "cancelText"
        const val PROP_CANCEL_ICON = "cancelIcon"
        const val PROP_EXPIRATION_TIME = "expireTime"
        const val TIMER_HANDLER_EVENT = 1
    }

    var onDialogConfirmListener: (() -> Unit)? = null
        set(value) {
            dialog?.findViewById<LinearLayout>(R.id.confirm_layout)?.setOnClickListener {
                value?.invoke()
                dialog?.dismiss()
            }
            field = value
        }

    var onDialogCancelListener: (() -> Unit)? = null
        set(value) {
            dialog?.findViewById<LinearLayout>(R.id.cancel_layout)?.setOnClickListener {
                value?.invoke()
                dialog?.dismiss()
            }
            field = value
        }

    var onDialogExpiredListener: (() -> Unit)? = null

    private var dialog: AlertDialog? = null

    private val timerHandler = TimerHandler(WeakReference(this))

    init {
        properties.apply {
            putDefault(PROP_CONFIRM_TEXT, context.getString(R.string.confirm))
            putDefault(PROP_CANCEL_TEXT, context.getString(R.string.cancel))
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
        val dialogBuilder = dialogProvider.provideCustomAlertDialogBuilder(context)
        dialogBuilder.apply {
            properties.ifContainsString(PROP_TITLE) { title ->
                setTitle(title)
            }
            properties.ifContainsString(PROP_TEXT) { text ->
                setDescription(text)
            }

            properties.ifContainsString(PROP_CONFIRM_TEXT) { text ->
                setConfirmationText(text)
                setOnDialogConfirmClick(onDialogConfirmListener)
            }
            properties.ifContainsString(PROP_CONFIRM_ICON) { iconRes ->
                if (iconRes != null) {
                    val icon = iconsRepository.getIcon(iconRes, false)
                    setConfirmationIcon(icon)
                }
            }
            properties.ifContainsString(PROP_CANCEL_TEXT) { text ->
                setCancelText(text)
                setOnDialogCancelClick(onDialogCancelListener)
            }
            properties.ifContainsString(PROP_CANCEL_ICON) { iconRes ->
                if (iconRes != null) {
                    val icon = iconsRepository.getIcon(iconRes, false)
                    setCancelIcon(icon)
                }
            }
        }
        this.dialog = dialogBuilder.create()
        this.dialog?.show()
        this.dialog?.window?.setBackgroundDrawable(null)
        this.dialog?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        properties.ifContainsDouble(PROP_EXPIRATION_TIME) { time: Double ->
            val msg = timerHandler.obtainMessage(TIMER_HANDLER_EVENT)
            val timeInMillis = (time * 1000).toLong()
            timerHandler.sendMessageDelayed(msg, timeInMillis)
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