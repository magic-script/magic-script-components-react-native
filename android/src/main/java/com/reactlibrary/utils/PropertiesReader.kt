package com.reactlibrary.utils

import android.graphics.Color
import android.os.Bundle
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.scene.nodes.props.Padding

/**
 * Class containing functions that parse common nodes' properties
 */
class PropertiesReader {

    companion object {

        fun readVector3(props: Bundle, propertyName: String): Vector3? {
            val vector = props.getSerializable(propertyName) ?: return null
            if (vector is ArrayList<*> && (vector as ArrayList<Double>).size == 3) {
                val x = vector[0].toFloat()
                val y = vector[1].toFloat()
                val z = vector[2].toFloat()
                return Vector3(x, y, z)
            }
            return null
        }

        /**
         * Returns Android color number or null if [props] do not contain the property
         * data for a given [propertyName]
         */
        fun readColor(props: Bundle, propertyName: String): Int? {
            val color = props.getSerializable(propertyName) ?: return null
            if (color is ArrayList<*> && (color as ArrayList<Double>).size == 4) {
                val r = color[0] * 255
                val g = color[1] * 255
                val b = color[2] * 255
                val a = color[3] * 255
                return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
            }
            return null
        }

        /**
         * Reads [Padding] from an array in the format [top, right, bottom, left]
         *
         * @return padding or null if [props] do not contain the property
         * data for a given [propertyName]
         */
        fun readPadding(props: Bundle, propertyName: String): Padding? {
            val paddingData = props.getSerializable(propertyName) ?: return null
            if (paddingData is ArrayList<*> && (paddingData as ArrayList<Double>).size == 4) {
                val top: Float = paddingData[0].toFloat()
                val right: Float = paddingData[1].toFloat()
                val bottom: Float = paddingData[2].toFloat()
                val left: Float = paddingData[3].toFloat()
                return Padding(top, right, bottom, left)
            }
            return null
        }
    }
}