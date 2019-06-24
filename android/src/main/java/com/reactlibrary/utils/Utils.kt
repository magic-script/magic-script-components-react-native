package com.reactlibrary.utils

import android.content.Context
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.NodesFactory

// converts ARCore's meters to pixels
fun metersToPx(meters: Double, context: Context): Int {
    val screenDensity = context.resources.displayMetrics.density
    return (meters * NodesFactory.DP_TO_METER_RATIO * screenDensity).toInt()
}

fun ReadableMap.getArraySafely(key: String): ReadableArray? {
    return if (hasKey(key)) {
        getArray(key)
    } else {
        null
    }
}

fun ReadableArray.toVector3(): Vector3? {
    return if (size() == 3) {
        val x = getDouble(0).toFloat()
        val y = getDouble(1).toFloat()
        val z = getDouble(2).toFloat()
        Vector3(x, y, z)
    } else {
        null
    }
}

fun ReadableMap.getDoubleSafely(key: String): Double? {
    return if (hasKey(key)) {
        getDouble(key)
    } else {
        null
    }
}