package com.reactlibrary.utils

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3

class PropertiesReader {
    object Common {

        fun getPosition(props: ReadableMap): Vector3 {
            val posArray = props.getArray("localPosition")
            val x = posArray?.getDouble(0)?.toFloat() ?: 0F
            val y = posArray?.getDouble(1)?.toFloat() ?: 0F
            val z = posArray?.getDouble(2)?.toFloat() ?: 0F

            return Vector3(x, y, z)
        }

        fun getSize(props: ReadableMap): Size {
            val width = props.getDouble("width")
            val height = props.getDouble("height")
            return Size(width, height)
        }


        fun getTextSize(props: ReadableMap): Double? {
            return props.getDouble("textSize")
        }
    }

}