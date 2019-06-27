package com.reactlibrary.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.BuildConfig

// By default, every 250dp for the view becomes 1 meter for the renderable
// https://developers.google.com/ar/develop/java/sceneform/create-renderables
const val DP_TO_METER_RATIO = 250

class Utils {
    companion object {

        fun getAndroidPath(filePath: String, context: Context): Uri {
            // e.g. resources\DemoPicture1.jpg
            return if (BuildConfig.DEBUG) {
                Uri.parse("http://localhost:8081/assets/$filePath")
            } else {
                val packageName = context.packageName
                val basePath = "android.resource://$packageName/drawable/"
                // resources\DemoPicture1.jpg is copied to
                // res/drawable in format: resources_demopicture1
                val fileName = filePath.toLowerCase().replace("/", "_")
                Uri.parse(basePath + fileName)
            }
        }

        // converts ARCore's meters to pixels
        fun metersToPx(meters: Double, context: Context): Int {
            val screenDensity = context.resources.displayMetrics.density
            return (meters * DP_TO_METER_RATIO * screenDensity).toInt()
        }
    }

}

/**
 * Extension methods
 */
fun Any.logMessage(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(this.javaClass.name, message) //this.javaClass.name
    }
}

fun ReadableMap.getArraySafely(key: String): ReadableArray? {
    return if (hasKey(key)) getArray(key) else null
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
    return if (hasKey(key)) getDouble(key) else null
}

fun ReadableMap.getStringSafely(key: String): String? {
    return if (hasKey(key)) getString(key)!! else null
}

fun ReadableMap.getBooleanSafely(key: String): Boolean? {
    return if (hasKey(key)) getBoolean(key) else null
}

