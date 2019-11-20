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
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.reactlibrary.R
import kotlinx.android.synthetic.main.dialog.view.*

class CustomAlertDialogBuilder(context: Context) : AlertDialog.Builder(context) {

    private var cancelIconView: ImageView?
    private var cancelTextView: TextView?
    private var cancelLayout: LinearLayout?
    private var confirmIconView: ImageView?
    private var confirmTextView: TextView?
    private var confirmLayout: LinearLayout?
    private var descriptionView: TextView?
    private var titleView: TextView?

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
        if(confirmLayout?.visibility == View.GONE) {
            confirmLayout?.visibility = View.VISIBLE
        }
        confirmTextView?.text = text
        return this
    }

    fun setConfirmationIcon(icon: Drawable?): AlertDialog.Builder {
        if(confirmLayout?.visibility == View.GONE) {
            confirmLayout?.visibility = View.VISIBLE
        }
        confirmIconView?.let {
            Glide.with(context).load(icon).into(it)
        }
        return this
    }

    fun setCancelText(text: String?): AlertDialog.Builder {
        if(cancelLayout?.visibility == View.GONE) {
            cancelLayout?.visibility = View.VISIBLE
        }
        cancelTextView?.text = text
        return this
    }

    fun setCancelIcon(icon: Drawable?): AlertDialog.Builder {
        if(cancelLayout?.visibility == View.GONE) {
            cancelLayout?.visibility = View.VISIBLE
        }
        cancelIconView?.let {
            Glide.with(context).load(icon).into(it)
        }
        return this
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