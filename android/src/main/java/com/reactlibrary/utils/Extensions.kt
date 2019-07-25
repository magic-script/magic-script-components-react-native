package com.reactlibrary.utils

import android.util.Log
import android.widget.EditText
import com.reactlibrary.BuildConfig

/**
 * ==========Extension methods============
 */

fun Any.logMessage(message: String, warn: Boolean = false) {
    if (BuildConfig.DEBUG) {
        if (warn) {
            Log.w("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
        } else {
            Log.d("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
        }
    }
}

fun EditText.setTextAndMoveCursor(text: String) {
    this.setText("")
    this.append(text)
}