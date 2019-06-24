package com.reactlibrary.utils

import android.content.Context
import android.net.Uri
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.BuildConfig

class PropertiesReader {
    object Common {

        fun getPosition(props: ReadableMap): Vector3 {
            val key = "localPosition"
            if (props.hasKey(key)) {
                val posArray = props.getArray(key)
                val x = posArray?.getDouble(0)?.toFloat() ?: 0F
                val y = posArray?.getDouble(1)?.toFloat() ?: 0F
                val z = posArray?.getDouble(2)?.toFloat() ?: 0F
                return Vector3(x, y, z)
            } else {
                return Vector3(0F, 0F, 0F)
            }
        }

        fun getSize(props: ReadableMap): Size {
            val widthKey = "width"
            val heightKey = "height"
            if (props.hasKey(widthKey) && props.hasKey(heightKey)) {
                val width = props.getDouble(widthKey)
                val height = props.getDouble(heightKey)
                return Size(width, height)
            } else {
                return Size(0.0, 0.0)
            }
        }

        fun getTextSize(props: ReadableMap): Double {
            val key = "textSize"
            if (props.hasKey(key)) {
                return props.getDouble(key)
            } else {
                val size = getSize(props)
                return size.height / 3
            }
        }

    }

    object Image {

        fun getFilePath(props: ReadableMap, context: Context): Uri {
            // e.g. resources\DemoPicture1.jpg
            val filePath = props.getString("filePath") ?: ""

            return if (BuildConfig.DEBUG) {
                Uri.parse("http://localhost:8081/assets/$filePath")
            } else {
                val packageName = context.packageName
                val resourcesPath = "android.resource://$packageName/drawable/"
                // convert string to format: resources_demopicture1
                val normalizedPath = filePath.toLowerCase().replace("/", "_")
                Uri.parse(normalizedPath)
            }
        }

    }

}