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

package com.magicleap.magicscript.ar

import com.google.ar.core.Anchor
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.TransformationSystem

abstract class ArResourcesProvider {
    private val arSceneListeners = mutableListOf<ArSceneChangedListener>()
    private val arLoadedListeners = mutableListOf<ArLoadedListener>()
    private val cameraUpdatedListeners = mutableListOf<CameraUpdatedListener>()
    private val transformationSystemListeners = mutableListOf<TransformationSystemListener>()
    private val planeTapListeners = mutableListOf<PlaneTapListener>()

    fun addArSceneChangedListener(listener: ArSceneChangedListener) {
        arSceneListeners.add(listener)
    }

    fun removeArSceneListener(listener: ArSceneChangedListener) {
        arSceneListeners.remove(listener)
    }

    fun addArLoadedListener(listener: ArLoadedListener) {
        arLoadedListeners.add(listener)
    }

    fun removeArLoadedListener(listener: ArLoadedListener) {
        arLoadedListeners.remove(listener)
    }

    fun addCameraUpdatedListener(listener: CameraUpdatedListener) {
        cameraUpdatedListeners.add(listener)
    }

    fun removeCameraUpdatedListener(listener: CameraUpdatedListener) {
        cameraUpdatedListeners.remove(listener)
    }

    fun addTransformationSystemListener(listener: TransformationSystemListener) {
        transformationSystemListeners.add(listener)
    }

    fun removeTransformationSystemListener(listener: TransformationSystemListener) {
        transformationSystemListeners.remove(listener)
    }

    fun addPlaneTapListener(listener: PlaneTapListener) {
        planeTapListeners.add(listener)
    }

    fun removePlaneTapListener(listener: PlaneTapListener) {
        planeTapListeners.remove(listener)
    }

    abstract fun getArScene(): Scene?

    abstract fun isArLoaded(): Boolean

    abstract fun getSession(): Session?

    abstract fun getTransformationSystem(): TransformationSystem?

    abstract fun getCameraState(): TrackingState?

    abstract fun isPlaneDetectionEnabled(): Boolean

    protected fun notifySceneChanged(arScene: Scene) {
        arSceneListeners.forEach { it.onSceneChanged(arScene) }
    }

    protected fun notifyCameraUpdated(position: Vector3, state: TrackingState) {
        cameraUpdatedListeners.forEach { it.onCameraUpdated(position, state) }
    }

    protected fun notifyTransformationSystemChanged(system: TransformationSystem) {
        transformationSystemListeners.forEach { it.onTransformationSystemChanged(system) }
    }

    protected fun notifyPlaneTapped(anchor: Anchor) {
        planeTapListeners.forEach { it.onPlaneTap(anchor) }
    }

    protected fun notifyArLoaded() {
        arLoadedListeners.forEach { it.onArLoaded() }
    }

    interface CameraUpdatedListener {
        fun onCameraUpdated(position: Vector3, state: TrackingState)
    }

    interface ArSceneChangedListener {
        fun onSceneChanged(arScene: Scene)
    }

    interface TransformationSystemListener {
        fun onTransformationSystemChanged(transformationSystem: TransformationSystem)
    }

    interface PlaneTapListener {
        fun onPlaneTap(anchor: Anchor)
    }

    interface ArLoadedListener {
        fun onArLoaded()
    }
}