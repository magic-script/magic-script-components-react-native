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
package com.magicleap.magicscript.scene.nodes.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.magicleap.magicscript.R
import com.magicleap.magicscript.scene.nodes.dialog.DialogType
import kotlinx.android.synthetic.main.dialog.view.*

class CustomAlertDialogBuilder(context: Context) : AlertDialog.Builder(context) {


    companion object {
        const val BUTTON_TYPE_ICON = "icon"
        const val BUTTON_TYPE_ICON_WITH_LABEL = "icon-with-label"
        const val BUTTON_TYPE_TEXT = "text"
        const val BUTTON_TYPE_TEXT_WITH_ICON = "text-with-icon"
    }

    private var cancelIconView: ImageView?
    private var cancelTextView: TextView?
    private var cancelLayout: ConstraintLayout?
    private var confirmIconView: ImageView?
    private var confirmTextView: TextView?
    private var confirmLayout: ConstraintLayout?
    private var descriptionView: TextView?
    private var titleView: TextView?
    private var confirmLabelView: TextView?
    private var cancelLabelView: TextView?

    private var onDialogConfirmListener: (() -> Unit)? = null
    private var onDialogCancelListener: (() -> Unit)? = null

    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog, null).apply {
            titleView = title
            descriptionView = description
            confirmLayout = confirm_layout
            confirmTextView = confirm_text
            confirmIconView = confirm_icon
            cancelLayout = cancel_layout
            cancelTextView = cancel_text
            cancelIconView = cancel_icon
            confirmLabelView = confirm_label
            cancelLabelView = cancel_label
        }
        setView(contentView)
        setCancelable(false)
    }

    fun setTitle(title: String?): AlertDialog.Builder {
        titleView?.text = title
        return this
    }

    fun setDescription(description: String?): AlertDialog.Builder {
        descriptionView?.text = description
        return this
    }

    fun setConfirmationText(text: String?): AlertDialog.Builder {
        if (confirmLayout?.visibility == View.GONE) {
            confirmLayout?.visibility = View.VISIBLE
        }
        confirmTextView?.text = text
        confirmLabelView?.text = text
        return this
    }

    fun setConfirmationIcon(icon: Drawable?): AlertDialog.Builder {
        if (confirmLayout?.visibility == View.GONE) {
            confirmLayout?.visibility = View.VISIBLE
        }
        confirmIconView?.let {
            Glide.with(context).load(icon).into(it)
        }
        return this
    }

    fun setCancelText(text: String?): AlertDialog.Builder {
        if (cancelLayout?.visibility == View.GONE) {
            cancelLayout?.visibility = View.VISIBLE
        }
        cancelTextView?.text = text
        cancelLabelView?.text = text
        return this
    }

    fun setCancelIcon(icon: Drawable?): AlertDialog.Builder {
        if (cancelLayout?.visibility == View.GONE) {
            cancelLayout?.visibility = View.VISIBLE
        }
        cancelIconView?.let {
            Glide.with(context).load(icon).into(it)
        }
        return this
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setButtonType(buttonType: String) {
        when (buttonType) {
            BUTTON_TYPE_TEXT_WITH_ICON -> {
                showIcon()
                showText()
            }
            BUTTON_TYPE_ICON -> {
                showIcon()
                hideText()
            }

            BUTTON_TYPE_ICON_WITH_LABEL -> {
                showIcon()
                hideText()
                confirmLayout?.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            confirmLabelView?.visibility = View.VISIBLE
                            false
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            confirmLabelView?.visibility = View.GONE
                            false
                        }
                        MotionEvent.ACTION_UP -> {
                            confirmLabelView?.visibility = View.GONE
                            false
                        }
                        else -> false
                    }
                }
                cancelLayout?.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            cancelLabelView?.visibility = View.VISIBLE
                            false
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            cancelLabelView?.visibility = View.GONE
                            false
                        }
                        MotionEvent.ACTION_UP -> {
                            cancelLabelView?.visibility = View.GONE
                            false
                        }
                        else -> false
                    }
                }
            }

            BUTTON_TYPE_TEXT -> {
                hideIcon()
                showText()
            }
        }
    }


    fun setDialogType(dialogType: String) {
        when (dialogType) {
            DialogType.DUAL_ACTION -> {
                confirmLayout?.visibility = View.VISIBLE
                cancelLayout?.visibility = View.VISIBLE
            }
            DialogType.SINGLE_ACTION -> {
                confirmLayout?.visibility = View.VISIBLE
                cancelLayout?.visibility = View.GONE
            }
            DialogType.NO_ACTION -> {
                confirmLayout?.visibility = View.GONE
                cancelLayout?.visibility = View.GONE
            }
        }
    }

    private fun showText() {
        confirmTextView?.visibility = View.VISIBLE
        cancelTextView?.visibility = View.VISIBLE
    }

    private fun showIcon() {
        confirmIconView?.visibility = View.VISIBLE
        cancelIconView?.visibility = View.VISIBLE
    }

    private fun hideText() {
        confirmTextView?.visibility = View.GONE
        cancelTextView?.visibility = View.GONE
    }

    private fun hideIcon() {
        confirmIconView?.visibility = View.GONE
        cancelIconView?.visibility = View.GONE
    }


    fun setOnDialogConfirmClick(onDialogConfirmListener: (() -> Unit)? = null): AlertDialog.Builder {
        this.onDialogConfirmListener = onDialogConfirmListener
        return this
    }

    fun setOnDialogCancelClick(onDialogCancelListener: (() -> Unit)? = null): AlertDialog.Builder {
        this.onDialogCancelListener = onDialogCancelListener
        return this
    }

    override fun create(): AlertDialog {
        val dialog = super.create()
        confirmLayout?.setOnClickListener {
            onDialogConfirmListener?.invoke()
            dialog.cancel()
        }
        cancelLayout?.setOnClickListener {
            onDialogCancelListener?.invoke()
            dialog.cancel()
        }
        return dialog
    }
}