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
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.base.UiNode
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

fun Vector3.rotatedBy(quaternion: Quaternion): Vector3 {
    return Utils.rotateVector(this, quaternion)
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

/*
 * Returns a string limited to [maxCharacters].
 * If length > [maxCharacters] it adds 3 dots at the end
 */
fun String.limited(maxCharacters: Int): String {
    return if (this.length > maxCharacters) {
        substring(0, maxCharacters) + "\u2026"
    } else this

}

/**
 * Calculates the view size in meters
 *
 * @param desiredWidth desired width in meters or [UiNode.WRAP_CONTENT_DIMENSION]
 * @param desiredHeight desired height in meters or [UiNode.WRAP_CONTENT_DIMENSION]
 */
fun View.getSizeInMeters(context: Context, desiredWidth: Float, desiredHeight: Float): Vector2 {
    val widthMeasureSpec = if (desiredWidth > UiNode.WRAP_CONTENT_DIMENSION) {
        val maxWidth = Utils.metersToPx(desiredWidth, context)
        View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.EXACTLY)
    } else {
        View.MeasureSpec.UNSPECIFIED
    }

    val heightMeasureSpec = if (desiredHeight > UiNode.WRAP_CONTENT_DIMENSION) {
        val maxHeight = Utils.metersToPx(desiredHeight, context)
        View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.EXACTLY)
    } else {
        View.MeasureSpec.UNSPECIFIED
    }

    measure(widthMeasureSpec, heightMeasureSpec)
    val width = Utils.pxToMeters(measuredWidth.toFloat(), context)
    val height = Utils.pxToMeters(measuredHeight.toFloat(), context)
    return Vector2(width, height)
}

fun Int.toJsColorArray(): Array<Double> {
    val red = Color.red(this).toDouble() / 255
    val green = Color.green(this).toDouble() / 255
    val blue = Color.blue(this).toDouble() / 255
    val alpha = Color.alpha(this).toDouble() / 255

    return arrayOf(red, green, blue, alpha)
}