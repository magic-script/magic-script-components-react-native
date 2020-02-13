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

package com.magicleap.magicscript.plane

import com.facebook.react.bridge.*
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.magicleap.magicscript.utils.NativeReactObjectsProvider
import com.magicleap.magicscript.utils.ReactObjectsProvider
import kotlin.math.abs

class ARPlaneDetectorBridge private constructor() {
    private var onUpdateListener: OnPlanesUpdated? = null
    private var onAddedListener: OnPlanesAdded? = null
    private var onRemovedListener: OnPlanesRemoved? = null
    private var onTappedListener: OnPlaneTapped? = null
    private var lastPlanes: List<Plane>? = null
    private var isDetecting: Boolean = false
    private var detectionConfiguration: List<Plane.Type>? = null
    var objectsProvider: ReactObjectsProvider = NativeReactObjectsProvider.INSTANCE

    companion object {
        private val ID = "id"
        private val CENTER = "center"
        private val VERTICES = "vertices"
        private val TYPE = "type"
        private val ERROR = "error"
        private val POINT = "point"
        val PLANE_TYPE = "planeType"
        val PLANE_TYPE_VERTICAL = "vertical"
        val PLANE_TYPE_HORIZONTAL = "horizontal"
        val INSTANCE = ARPlaneDetectorBridge()
    }

    fun mapPlanesToWritableMap(plane: Plane): WritableMap {
        val planeMap = objectsProvider.createMap()
        val vertices = objectsProvider.createArray()
        val polygonList = plane.polygon.array().toList()
        val centerPose = plane.centerPose
        polygonList.chunked(2).forEach {
            val point = centerPose.transformPoint(floatArrayOf(it[0], 0f, it[1]))
            vertices.pushArray(objectsProvider.createArray().apply {
                pushDouble(point[0].toDouble())
                pushDouble(point[1].toDouble())
                pushDouble(point[2].toDouble())
            })
        }
        planeMap.putString(TYPE, plane.type.name)
        planeMap.putArray(VERTICES, vertices)
        planeMap.putArray(CENTER, objectsProvider.createArray().apply {
            pushDouble(plane.centerPose.tx().toDouble())
            pushDouble(plane.centerPose.ty().toDouble())
            pushDouble(plane.centerPose.tz().toDouble())
        })
        planeMap.putString(ID, abs(plane.hashCode()).toString())
        return planeMap
    }

    fun mapToError(errorMessage: String): WritableMap {
        return objectsProvider.createMap().apply {
            putString(ERROR, errorMessage)
        }
    }

    fun onPlaneUpdate(planes: List<Plane>) {
        if (planes.isNotEmpty()) {
            val filteredPlanes = planes.filter { detectionConfiguration?.contains(it.type) ?: true }
            if (lastPlanes == null) {
                lastPlanes = filteredPlanes
                filteredPlanes.forEach {
                    onAddedListener?.invoke(mapPlanesToWritableMap(it))
                }
            } else if (lastPlanes != null && lastPlanes != filteredPlanes) {
                filterAddedPlanes(filteredPlanes)
                filterRemovedPlanes(filteredPlanes)
                filterUpdatedPlanes(filteredPlanes)
                lastPlanes = filteredPlanes
            }
        }
    }

    fun onPlaneTapped(plane: Plane, hitTest: HitResult) {
        val payload = mapPlanesToWritableMap(plane)
        payload.putArray(POINT, objectsProvider.createArray().apply {
            pushDouble(hitTest.hitPose.tx().toDouble())
            pushDouble(hitTest.hitPose.ty().toDouble())
            pushDouble(hitTest.hitPose.tz().toDouble())
        })
        onTappedListener?.invoke(payload)
    }

    fun setOnPlanesAddedListener(listener: OnPlanesAdded) {
        this.onAddedListener = listener
    }

    fun setOnPlanesRemovedListener(listener: OnPlanesRemoved) {
        this.onRemovedListener = listener
    }

    fun setOnPlanesUpdatedListener(listener: OnPlanesUpdated) {
        this.onUpdateListener = listener
    }

    fun setOnPlaneTappedListener(listener: OnPlaneTapped) {
        this.onTappedListener = listener
    }

    fun isDetecting() = isDetecting

    fun getAllPlanes(configuration: ReadableMap?, callback: Callback) {
        val type = getPlaneTypes(configuration)
        if(!isDetecting) {
            callback.invoke(mapToError("You have to enable planes detection with startDetecting method!"), null)
        } else if(lastPlanes.isNullOrEmpty()) {
            callback.invoke(mapToError("The are no planes detected yet"), null)
        } else {
            val planes = objectsProvider.createArray()
            lastPlanes!!
                    .filter { type.contains(it.type) }
                    .forEach { planes.pushMap(mapPlanesToWritableMap(it)) }
                    .also { callback.invoke(null, planes) }
        }
    }

    private fun getPlaneTypes(configuration: ReadableMap?): List<Plane.Type> {
        return if(configuration == null || configuration.isNull(PLANE_TYPE)) {
            listOf(Plane.Type.VERTICAL, Plane.Type.HORIZONTAL_UPWARD_FACING, Plane.Type.HORIZONTAL_DOWNWARD_FACING)
        } else {
            val planeTypes = arrayListOf<Plane.Type>()
            val planeArray = configuration.getArray(PLANE_TYPE)
            if(planeArray != null) {
                for (i in 0 until planeArray.size()) {
                    when (planeArray.getString(i)) {
                        PLANE_TYPE_HORIZONTAL -> {
                            planeTypes.add(Plane.Type.HORIZONTAL_DOWNWARD_FACING)
                            planeTypes.add(Plane.Type.HORIZONTAL_UPWARD_FACING)
                        }
                        PLANE_TYPE_VERTICAL -> {
                            planeTypes.add(Plane.Type.VERTICAL)
                        }
                    }
                }
            }
            planeTypes
        }
    }

    fun destroy() {
        this.onUpdateListener = null
        this.onAddedListener = null
        this.onRemovedListener = null
        this.lastPlanes = null
    }

    private fun filterAddedPlanes(planes: List<Plane>) {
        lastPlanes?.let {
            planes.minus(it).forEach {
                onAddedListener?.invoke(mapPlanesToWritableMap(it))
            }
        }
    }

    private fun filterRemovedPlanes(planes: List<Plane>) {
        lastPlanes?.let {
            it.minus(planes).forEach {
                onRemovedListener?.invoke(mapPlanesToWritableMap(it))
            }
        }
    }

    private fun filterUpdatedPlanes(planes: List<Plane>) {
        lastPlanes?.filter { planes.contains(it) }?.forEach {
            onUpdateListener?.invoke(mapPlanesToWritableMap(it))
        }
    }

    fun startDetecting(configuration: ReadableMap) {
        detectionConfiguration = getPlaneTypes(configuration)
        isDetecting = true
    }

    fun stopDetecting() {
        isDetecting = false
    }
}