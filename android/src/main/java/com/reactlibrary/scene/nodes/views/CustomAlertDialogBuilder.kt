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
        confirmTextView?.text = text
        return this
    }

    fun setConfirmationIcon(icon: Drawable?): AlertDialog.Builder {
        confirmIconView?.let {
            Glide.with(context).load(icon).into(it)
        }
        return this
    }

    fun setCancelText(text: String?): AlertDialog.Builder {
        cancelTextView?.text = text
        return this
    }

    fun setCancelIcon(icon: Drawable?): AlertDialog.Builder {
        cancelIconView?.let {
            Glide.with(context).load(icon).into(it)
        }
        return this
    }

    fun setOnDialogConfirmClick(onDialogConfirmListener: (() -> Unit)? = null): AlertDialog.Builder {
        Log.d("DialogNode", "onDialogConfirmListener: $onDialogConfirmListener")
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