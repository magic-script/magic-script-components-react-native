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

package com.magicleap.magicscript.utils

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import com.google.ar.sceneform.math.Matrix
import com.google.ar.sceneform.math.Vector3
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontStyle
import com.magicleap.magicscript.font.FontWeight
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundDistance
import com.magicleap.magicscript.scene.nodes.audio.model.SpatialSoundPosition
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.AABB
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Padding

/**
 * File containing functions that parse common nodes' properties from Bundle
 */
private const val PROP_SCROLL_BOUNDS_MIN = "min"
private const val PROP_SCROLL_BOUNDS_MAX = "max"
private const val PROP_WEIGHT = "weight"
private const val PROP_STYLE = "style"
private const val PROP_ALL_CAPS = "allCaps"
private const val FILE_URI_PROPERTY = "uri"

inline fun <reified T : Any> Bundle.read(key: String): T? =
    if (containsKey(key))
        when (T::class) {
            Boolean::class -> getBoolean(key) as T?
            Double::class -> getDouble(key) as T?
            String::class -> getString(key) as T?
            AABB::class -> readAABB(this, key) as T?
            Vector2::class -> readVector2(this, key) as T?
            Vector3::class -> readVector3(this, key) as T?
            Matrix::class -> readMatrix(this, key) as T?
            Padding::class -> readPadding(this, key) as T?
            Alignment::class -> readAlignment(this, key) as T?
            FontParams::class -> readFontParams(this, key) as T?
            SpatialSoundPosition::class -> readSpatialSoundPosition(this, key) as T?
            SpatialSoundDistance::class -> readSpatialSoundDistance(this, key) as T?
            else -> {
                logMessage("Unknown class ${T::class.java.name}", true)
                null
            }
        } else null

fun Bundle.readImagePath(propertyName: String, context: Context): Uri? {
    return getFileUri(this, propertyName, context, "drawable")
}

fun Bundle.readFilePath(propertyName: String, context: Context): Uri? {
    return getFileUri(this, propertyName, context, "raw")
}

/**
 * Returns Android color number or null if [props] do not contain the property
 * data for a given [propertyName]
 */
fun Bundle.readColor(propertyName: String): Int? {
    val color = this.getSerializable(propertyName) as? ArrayList<Double> ?: return null
    if (color.size == 4) {
        val r = color[0] * 255
        val g = color[1] * 255
        val b = color[2] * 255
        val a = color[3] * 255
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }
    return null
}

fun Bundle.readVectorsList(propertyName: String): List<Vector3> {
    val vectorsList = mutableListOf<Vector3>()
    val pointsArray = this.getSerializable(propertyName)
    if (pointsArray is ArrayList<*>) {
        pointsArray.forEach {
            if (it is ArrayList<*> && it.size == 3) {
                val point = it as ArrayList<Double>
                val vector =
                    Vector3(point[0].toFloat(), point[1].toFloat(), point[2].toFloat())
                vectorsList.add(vector)
            }
        }
    }
    return vectorsList
}

fun readVector2(props: Bundle, propertyName: String): Vector2? {
    val vector = props.getSerializable(propertyName) as? ArrayList<Double> ?: return null
    if (vector.size == 2) {
        val x = vector[0].toFloat()
        val y = vector[1].toFloat()
        return Vector2(x, y)
    }
    return null
}

fun readVector3(props: Bundle, propertyName: String): Vector3? {
    val list = props.getSerializable(propertyName) as? ArrayList<Double> ?: return null
    return getVectorFromList(list)
}

fun TransformNode.getUserSpecifiedScale(): Vector3? {
    val scale = getProperty(TransformNode.PROP_LOCAL_SCALE)
            as? java.util.ArrayList<Double> ?: return null
    return getVectorFromList(scale)
}

fun readMatrix(props: Bundle, propertyName: String): Matrix? {
    val array = props.getSerializable(propertyName) as? ArrayList<Double> ?: return null
    if (array.size == 16) {
        val matrixData = FloatArray(16)
        array.forEachIndexed { index, element ->
            matrixData[index] = element.toFloat()
        }
        return Matrix(matrixData)
    }
    return null
}

fun readFontParams(bundle: Bundle, paramName: String): FontParams? {
    val paramsBundle = bundle.getBundle(paramName) ?: return null
    val weightName = paramsBundle.getString(PROP_WEIGHT, "")
    val styleName = paramsBundle.getString(PROP_STYLE, "")
    val allCaps = paramsBundle.getBoolean(PROP_ALL_CAPS, false)

    val weight = FontWeight.fromName(weightName) ?: FontWeight.DEFAULT
    val style = FontStyle.fromName(styleName) ?: FontStyle.DEFAULT

    return FontParams(weight, style, allCaps)
}

/**
 * Reads [Padding] from an array in the format [top, right, bottom, left]
 *
 * @return padding or null if [props] do not contain the property
 * data for a given [propertyName]
 */
fun readPadding(props: Bundle, propertyName: String): Padding? {
    val paddingData = props.getSerializable(propertyName) as? ArrayList<Double>
        ?: return null
    if (paddingData.size == 4) {
        val top: Float = paddingData[0].toFloat()
        val right: Float = paddingData[1].toFloat()
        val bottom: Float = paddingData[2].toFloat()
        val left: Float = paddingData[3].toFloat()
        return Padding(top, right, bottom, left)
    }
    return null
}

fun readAlignment(props: Bundle, propertyName: String): Alignment? {
    val alignmentData = props.getString(propertyName) ?: return null
    val alignmentArray = alignmentData.split("-")
    if (alignmentArray.size != 2) {
        return null
    }
    val verticalAlign = Alignment.VerticalAlignment.valueOf(alignmentArray[0].toUpperCase())
    val horizontalAlign =
        Alignment.HorizontalAlignment.valueOf(alignmentArray[1].toUpperCase())
    return Alignment(verticalAlign, horizontalAlign)
}

/**
 * Returns [AABB] or null if [props] do not contain the property
 * data for a given [propertyName]
 */
fun readAABB(props: Bundle, propertyName: String): AABB? {
    val data = props.getBundle(propertyName) ?: return null
    val min = readVector3(data, PROP_SCROLL_BOUNDS_MIN) ?: return null
    val max = readVector3(data, PROP_SCROLL_BOUNDS_MAX) ?: return null
    return AABB(min, max)
}

fun readSpatialSoundPosition(props: Bundle, propertyName: String): SpatialSoundPosition? {
    val spatialSoundPositionBundle = props.getBundle(propertyName) ?: return null

    return SpatialSoundPosition(
        channel = spatialSoundPositionBundle.getDouble(SpatialSoundPosition::channel.name),
        channelPosition = spatialSoundPositionBundle.read(SpatialSoundPosition::channelPosition.name)
    )
}

fun readSpatialSoundDistance(props: Bundle, propertyName: String): SpatialSoundDistance? {
    val spatialSoundDistance = props.getBundle(propertyName) ?: return null

    return SpatialSoundDistance(
        channel = spatialSoundDistance.getDouble(SpatialSoundDistance::channel.name),
        maxDistance = spatialSoundDistance.getDouble(SpatialSoundDistance::maxDistance.name).toFloat(),
        minDistance = spatialSoundDistance.getDouble(SpatialSoundDistance::minDistance.name).toFloat(),
        rolloffFactor = spatialSoundDistance.getDouble(SpatialSoundDistance::rolloffFactor.name).toInt()
    )
}

private fun getFileUri(
    props: Bundle,
    propertyName: String,
    context: Context,
    resType: String
): Uri? {
    // If the path is a string, we treat it as a remote URL
    val remoteUrl = props.getString(propertyName)
    if (remoteUrl != null) {
        return Uri.parse(remoteUrl)
    }

    // In case of using 'require' and local paths, we get bundle that include the uri.
    // For debug mode it's a localhost URL, for release it's an android resource name
    // like 'resources_demopicture1')
    val pathBundle = props.getBundle(propertyName)
    if (pathBundle != null) {
        val fileUri = pathBundle.getString(FILE_URI_PROPERTY)
        if (fileUri != null) {
            if (fileUri.startsWith("http")) { // fileUri is a localhost URL
                return Uri.parse(fileUri)
            } else {
                // here fileUri is in format: 'resources_demopicture1'
                val packageName = context.packageName
                val basePath = "android.resource://$packageName/$resType/"

                return Uri.parse(basePath + fileUri)
            }
        }
    }
    return null
}

private fun getVectorFromList(list: List<Double>): Vector3? {
    if (list.size == 3) {
        val x = list[0].toFloat()
        val y = list[1].toFloat()
        val z = list[2].toFloat()
        return Vector3(x, y, z)
    }
    return null
}

