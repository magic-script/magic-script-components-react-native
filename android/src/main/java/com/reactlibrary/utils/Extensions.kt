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
 * android.graphics.PointF
 */
operator fun PointF.unaryMinus(): PointF {
    return PointF(-x, -y)
}

operator fun PointF.plus(other: PointF): PointF {
    return PointF(x + other.x, y + other.y)
}

operator fun PointF.minus(other: PointF): PointF {
    return PointF(x - other.x, y - other.y)
}

operator fun PointF.minus(other: Float): PointF {
    return PointF(x - other, y - other)
}

operator fun PointF.times(other: PointF): PointF {
    return PointF(x * other.x, y * other.y)
}

operator fun PointF.div(other: PointF): PointF {
    return PointF(if (other.x != 0F) {
        x / other.x
    } else {
        0F
    }, if (other.y != 0F) {
        y / other.y
    } else {
        0F
    })
}

operator fun PointF.div(other: Float): PointF {
    return div(PointF(other, other))
}

fun PointF.coerceIn(min: Float, max: Float): PointF {
    return PointF(x.coerceIn(min, max), y.coerceIn(min, max))
}

fun PointF.coerceAtLeast(min: Float): PointF {
    return PointF(x.coerceAtLeast(min), y.coerceAtLeast(min))
}

fun PointF.equalInexact(other: PointF): Boolean {
    val eps = 1e-5 // epsilon
    return abs(x - other.x) < eps && abs(y - other.y) < eps
}

/**
 * com.google.ar.sceneform.math.Vector3
 */
operator fun Vector3.plus(other: Vector3): Vector3 {
    return Vector3(
            x + other.x,
            y + other.y,
            z + other.z
    )
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

inline fun View.onPreDrawListener(crossinline f: () -> Boolean) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            return f()
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