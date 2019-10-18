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

import android.os.Bundle
import android.util.Log
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

/**
 * Returns a string limited to [maxCharacters].
 * If length > [maxCharacters] it adds 3 dots at the end
 */
fun String.limited(maxCharacters: Int): String {
    return if (this.length > maxCharacters) {
        substring(0, maxCharacters) + "\u2026"
    } else this
}
