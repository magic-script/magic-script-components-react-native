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

private const val DEBUG_ASSETS_PATH = "http://localhost:8081/assets/"

class Utils {
    companion object {

        /**
         * Converts React project's image path to path
         * that can be accessed from android code.
         */
        fun getImagePath(imagePath: String, context: Context): Uri {
            // e.g. resources\DemoPicture1.jpg
            return if (BuildConfig.DEBUG) {
                Uri.parse(DEBUG_ASSETS_PATH + imagePath)
            } else {
                val packageName = context.packageName
                val basePath = "android.resource://$packageName/drawable/"
                // resources\DemoPicture1.jpg is copied to
                // res/drawable with file name = "resources_demopicture1"
                val fileName = imagePath.toLowerCase().replace("/", "_")
                Uri.parse(basePath + fileName)
            }
        }

        /**
         *
         * Converts React project's file path (other than image) to path
         * that can be accessed from android code.
         *
         * TODO (currently working only in debug when device is connected to PC)
         */
        fun getFilePath(filePath: String, context: Context): Uri {
            // e.g. resources\model.glb
            return if (BuildConfig.DEBUG) {
                Uri.parse(DEBUG_ASSETS_PATH + filePath)
            } else {
                val packageName = context.packageName
                val basePath = "android.resource://$packageName/raw/"
                // TODO check if resources\model.glb is copied to
                // TODO res/raw with file name = "resources_model"
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
        Log.d("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
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

