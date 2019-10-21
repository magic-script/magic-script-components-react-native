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

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import com.google.ar.sceneform.math.Vector3
import java.io.Serializable
import kotlin.math.abs

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

/**
 * android.widget.EditText
 */
fun EditText.setTextAndMoveCursor(text: String) {
    this.setText("")
    this.append(text)
}

/**
 * com.google.ar.sceneform.math.Vector3
 */
operator fun Vector3.plus(other: Vector3): Vector3 {
    return Vector3.add(this, other)
}

operator fun Vector3.minus(other: Vector3): Vector3 {
    return Vector3.subtract(this, other)
}

operator fun Vector3.div(other: Float): Vector3 {
    return this.scaled(1F / other)
}

/**
 * android.os.Bundle
 */
fun Bundle.putDefaultDouble(name: String, value: Double) {
    if (!containsKey(name)) {
        putDouble(name, value)
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

/**
 * android.view.View
 */
inline fun View.onLayoutListener(crossinline f: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            f()
        }
    })
}

inline fun View.onDrawListener(crossinline f: () -> Unit) {
    viewTreeObserver.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
        override fun onDraw() {
            f()
        }
    })
}

/*
 * Returns a string limited to [maxCharacters].
 * If length > [maxCharacters] it adds 3 dots at the end
 */
fun String.limited(maxCharacters: Int): String {
    return if (this.length > maxCharacters) {
        substring(0, maxCharacters) + "\u2026"
    } else this
}
