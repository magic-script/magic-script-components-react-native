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

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Message
import android.view.View
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ArViewManager
import com.reactlibrary.icons.IconsRepository
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.scene.nodes.views.CustomAlertDialogBuilder
import com.reactlibrary.utils.ifContainsDouble
import com.reactlibrary.utils.ifContainsString
import com.reactlibrary.utils.logMessage
import java.lang.ref.WeakReference

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
        const val PROP_EXPIRATION_TIME = "expireTime"
        const val TIMER_HANDLER_EVENT = 1

    }

    class TimerHandler(private val dialogNode: WeakReference<DialogNode>) : Handler() {

        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                TIMER_HANDLER_EVENT -> {
                    val dialog = dialogNode.get()?.dialog
                    if (dialog != null && dialog.isShowing) {
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    var onDialogConfirmListener: (() -> Unit)? = null
    var onDialogCancelListener: (() -> Unit)? = null

    private var dialog: AlertDialog? = null

    private val timerHandler = TimerHandler(WeakReference(this))

//    private val onConfirm = DialogInterface.OnClickListener { _, _ ->
//        onDialogConfirmListener?.invoke()
//    }
//
//    private val onCancel = DialogInterface.OnClickListener { _, _ ->
//        onDialogCancelListener?.invoke()
//    }

    override fun build() {
        super.build()
        showDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

    private fun showDialog() {
        logMessage("Show dialog")
        logMessage("props: $properties")
        val activityContext = ArViewManager.getActivityRef().get() as Context
        val dialog = CustomAlertDialogBuilder(activityContext)
        dialog.apply {
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
        this.dialog = dialog.create()
        this.dialog?.show()
        this.dialog?.window?.setBackgroundDrawable(null)
        this.dialog?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        properties.ifContainsDouble(PROP_EXPIRATION_TIME) { time: Double ->
            val msg = timerHandler.obtainMessage(TIMER_HANDLER_EVENT)
            val timeInMillis = (time * 1000).toLong()
            timerHandler.sendMessageDelayed(msg, timeInMillis)
        }
    }
}