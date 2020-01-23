/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.magicleap.magicscript

import android.os.Bundle
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.math.Vector3

/**
 * Properties building functions to use in tests
 */

fun reactMapOf(vararg keyAndValues: Any) = JavaOnlyMap.of(*keyAndValues)

fun reactArrayOf(vararg values: Any) = JavaOnlyArray.of(*values)

fun createProperty(vararg keysAndValues: Any): Bundle =
    Arguments.toBundle(reactMapOf(*keysAndValues)) ?: Bundle()

fun JavaOnlyMap.localPosition(x: Double, y: Double, z: Double): JavaOnlyMap {
    putArray("localPosition", JavaOnlyArray.of(x, y, z))
    return this
}

fun JavaOnlyMap.localPosition(position: Vector3?): JavaOnlyMap {
    putArray(
        "localPosition", JavaOnlyArray.of(
            position?.x?.toDouble() ?: 0.0,
            position?.y?.toDouble() ?: 0.0,
            position?.z?.toDouble() ?: 0.0
        )
    )
    return this
}

fun JavaOnlyMap.localScale(x: Double, y: Double, z: Double): JavaOnlyMap {
    putArray("localScale", JavaOnlyArray.of(x, y, z))
    return this
}

fun JavaOnlyMap.width(width: Double): JavaOnlyMap {
    putDouble("width", width)
    return this
}

fun JavaOnlyMap.height(height: Double): JavaOnlyMap {
    putDouble("height", height)
    return this
}

fun JavaOnlyMap.roundness(roundness: Double): JavaOnlyMap {
    putDouble("roundness", roundness)
    return this
}

fun JavaOnlyMap.textSize(textSize: Double): JavaOnlyMap {
    putDouble("textSize", textSize)
    return this
}

fun JavaOnlyMap.text(text: String): JavaOnlyMap {
    putString("text", text)
    return this
}

fun JavaOnlyMap.alignment(alignment: String): JavaOnlyMap {
    putString("alignment", alignment)
    return this
}

fun JavaOnlyMap.textAlignment(textAlignment: String): JavaOnlyMap {
    putString("textAlignment", textAlignment)
    return this
}

fun JavaOnlyMap.boundsSize(x: Double, y: Double, wrap: Boolean): JavaOnlyMap {
    putMap(
        "boundsSize", JavaOnlyMap.of(
            "boundsSize", JavaOnlyArray.of(x, y),
            "wrap", wrap
        )
    )
    return this
}

fun JavaOnlyMap.on(on: Boolean): JavaOnlyMap {
    putBoolean("on", on)
    return this
}

fun JavaOnlyMap.points(
    x: Double,
    y: Double,
    z: Double,
    x2: Double,
    y2: Double,
    z2: Double
): JavaOnlyMap {
    putArray(
        "points", JavaOnlyArray.of(
            JavaOnlyArray.of(x, y, z),
            JavaOnlyArray.of(x2, y2, z2)
        )
    )
    return this
}

fun JavaOnlyMap.color(r: Double, g: Double, b: Double, a: Double): JavaOnlyMap {
    putArray("color", JavaOnlyArray.of(r, g, b, a))
    return this
}

fun JavaOnlyMap.value(value: Double): JavaOnlyMap {
    putDouble("value", value)
    return this
}

fun JavaOnlyMap.min(min: Double): JavaOnlyMap {
    putDouble("min", min)
    return this
}

fun JavaOnlyMap.max(max: Double): JavaOnlyMap {
    putDouble("max", max)
    return this
}

fun JavaOnlyMap.progressColor(
    beginColor: Array<Double>,
    endColor: Array<Double>? = null
): JavaOnlyMap {
    if (endColor != null) {
        putMap(
            "progressColor", JavaOnlyMap.of(
                "beginColor",
                JavaOnlyArray.of(beginColor[0], beginColor[1], beginColor[2], beginColor[3]),
                "endColor",
                JavaOnlyArray.of(endColor[0], endColor[1], endColor[2], endColor[3])
            )
        )
    } else {
        putMap(
            "progressColor", JavaOnlyMap.of(
                "beginColor",
                JavaOnlyArray.of(beginColor[0], beginColor[1], beginColor[2], beginColor[3])
            )
        )
    }

    return this
}

fun JavaOnlyMap.columns(columns: Int): JavaOnlyMap {
    putDouble("columns", columns.toDouble())
    return this
}

fun JavaOnlyMap.scrollBounds(min: Array<Double>, max: Array<Double>): JavaOnlyMap {
    putMap(
        "scrollBounds", reactMapOf(
            "min", reactArrayOf(*min),
            "max", reactArrayOf(*max)
        )
    )
    return this
}

fun JavaOnlyMap.defaultItemAlignment(defaultItemAlignment: String): JavaOnlyMap {
    putString("defaultItemAlignment", defaultItemAlignment)
    return this
}

fun JavaOnlyMap.defaultItemPadding(
    top: Double,
    right: Double,
    bottom: Double,
    left: Double
): JavaOnlyMap {
    putArray("defaultItemPadding", JavaOnlyArray.of(top, right, bottom, left))
    return this
}

fun JavaOnlyMap.orientation(orientation: String): JavaOnlyMap {
    putString("orientation", orientation)
    return this
}

fun JavaOnlyMap.id(id: String): JavaOnlyMap {
    putString("id", id)
    return this
}

fun JavaOnlyMap.label(label: String): JavaOnlyMap {
    putString("label", label)
    return this
}

fun JavaOnlyMap.selected(selected: Boolean): JavaOnlyMap {
    putBoolean("selected", selected)
    return this
}

fun JavaOnlyMap.timeFormat(timeFormat: String): JavaOnlyMap {
    putString("timeFormat", timeFormat)
    return this
}

fun JavaOnlyMap.allowMultipleOn(allowMultipleOn: Boolean): JavaOnlyMap {
    putBoolean("allowMultipleOn", allowMultipleOn)
    return this
}

fun JavaOnlyMap.allowAllOff(allowAllOff: Boolean): JavaOnlyMap {
    putBoolean("allowAllOff", allowAllOff)
    return this
}

fun JavaOnlyMap.allTogglesOff(allTogglesOff: Boolean): JavaOnlyMap {
    putBoolean("allTogglesOff", allTogglesOff)
    return this
}

fun JavaOnlyMap.type(type: String): JavaOnlyMap {
    putString("type", type)
    return this
}

fun JavaOnlyMap.icon(icon: String): JavaOnlyMap {
    putString("icon", icon)
    return this
}

fun JavaOnlyMap.fileName(fileName: String): JavaOnlyMap {
    putMap("fileName", reactMapOf("uri", fileName))
    return this
}

fun JavaOnlyMap.spatialSoundEnable(spatialSoundEnable: Boolean): JavaOnlyMap {
    putBoolean("spatialSoundEnable", spatialSoundEnable)
    return this
}

fun JavaOnlyMap.soundMute(soundMute: Boolean): JavaOnlyMap {
    putBoolean("soundMute", soundMute)
    return this
}

fun JavaOnlyMap.action(action: String): JavaOnlyMap {
    putString("action", action)
    return this
}

fun JavaOnlyMap.soundLooping(soundLooping: Boolean): JavaOnlyMap {
    putBoolean("soundLooping", soundLooping)
    return this
}

fun JavaOnlyMap.spatialSoundPosition(channel: Int, channelPosition: Array<Double>): JavaOnlyMap {
    putMap(
        "spatialSoundPosition", reactMapOf(
            "channel", channel,
            "channelPosition", reactArrayOf(*channelPosition)
        )
    )

    return this
}

fun JavaOnlyMap.spatialSoundDistance(
    channel: Int,
    minDistance: Double,
    maxDistance: Double,
    rolloffFactor: Int
): JavaOnlyMap {
    putMap(
        "spatialSoundDistance", reactMapOf(
            "channel", channel,
            "minDistance", minDistance,
            "maxDistance", maxDistance,
            "rolloffFactor", rolloffFactor
        )
    )

    return this
}
