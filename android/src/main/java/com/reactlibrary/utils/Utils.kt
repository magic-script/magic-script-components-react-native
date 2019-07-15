package com.reactlibrary.utils

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.EditText
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.BuildConfig
import java.io.Serializable

// By default, every 250dp for the view becomes 1 meter for the renderable
// https://developers.google.com/ar/develop/java/sceneform/create-renderables
private const val DP_TO_METER_RATIO = 250

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

        /**
         *  Converts ARCore's meters to pixels
         *  (Uses an average of horizontal and vertical density -
         *  usually they are almost the same)
         */
        fun metersToPx(meters: Double, context: Context): Int {
            val xdpi = context.resources.displayMetrics.xdpi
            val ydpi = context.resources.displayMetrics.ydpi
            val densityAvgFactor = (xdpi + ydpi) / 320
            logMessage("density avg=$densityAvgFactor")
            return (meters * DP_TO_METER_RATIO * densityAvgFactor).toInt()
        }
    }

}

/**
 * ==========Extension methods============
 */

fun Any.logMessage(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
    }
}

fun Serializable.toVector3(): Vector3? {
    this as ArrayList<Double>
    return if (size == 3) {
        val x = get(0).toFloat()
        val y = get(1).toFloat()
        val z = get(2).toFloat()
        Vector3(x, y, z)
    } else {
        null
    }
}

fun Serializable.toVector4(): List<Double>? {
    return if ((this as ArrayList<Double>).size == 4) {
        this
    } else {
        null
    }
}

fun List<Double>.toColor(): Int? {
    return if (this.size == 4) {
        val r = get(0) * 255
        val g = get(1) * 255
        val b = get(2) * 255
        val a = get(3) * 255
        Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    } else {
        null
    }
}

fun Serializable.toQuaternion(): Quaternion? {
    this as ArrayList<Double>
    return if (size == 4) {
        val x = get(0).toFloat()
        val y = get(1).toFloat()
        val z = get(2).toFloat()
        val w = get(3).toFloat()
        Quaternion(x, y, z, w) // Quaternion.axisAngle
    } else {
        null
    }
}

fun EditText.setTextAndMoveCursor(text: String) {
    this.setText("")
    this.append(text)
}