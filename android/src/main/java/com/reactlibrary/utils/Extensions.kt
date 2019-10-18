/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactlibrary.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import java.io.Serializable

/**
 * ==========Extension methods============
 */

fun Any.logMessage(message: String, warn: Boolean = false) {
    if (warn) {
        Log.w("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
    } else {
        Log.d("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
    }
}

fun EditText.setTextAndMoveCursor(text: String) {
    this.setText("")
    this.append(text)
}

fun Bundle.putDefaultDouble(key: String, value: Double) {
    if (!containsKey(key)) {
        putDouble(key, value)
    }
}

fun Bundle.putDefaultString(key: String, value: String) {
    if (!containsKey(key)) {
        putString(key, value)
    }
}

fun Bundle.putDefaultBoolean(key: String, value: Boolean) {
    if (!containsKey(key)) {
        putBoolean(key, value)
    }
}

fun Bundle.putDefaultSerializable(key: String, value: Serializable) {
    if (!containsKey(key)) {
        putSerializable(key, value)
    }
}

fun View.getSizeInMeters(context: Context, maxWidthMeters: Float, maxHeightMeters: Float): Pair<Float, Float> {
    val widthMeasureSpec = if (maxWidthMeters > 0) {
        val maxWidth = Utils.metersToPx(maxWidthMeters, context)
        View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST)
    } else {
        View.MeasureSpec.UNSPECIFIED
    }

    val heightMeasureSpec = if (maxHeightMeters > 0) {
        val maxHeight = Utils.metersToPx(maxHeightMeters, context)
        View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST)
    } else {
        View.MeasureSpec.UNSPECIFIED
    }

    measure(widthMeasureSpec, heightMeasureSpec)
    logMessage("Measured width= $measuredWidth")
    val widthMeters = Utils.pxToMeters(measuredWidth.toFloat(), context)
    val heightMeters = Utils.pxToMeters(measuredHeight.toFloat(), context)
    return Pair(widthMeters, heightMeters)
}
