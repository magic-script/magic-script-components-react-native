/*
 * Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.ux.TransformationSystem

abstract class ArResourcesProvider {
    private val arSceneListeners = mutableListOf<ArSceneChangedListener>()
    private val arLoadedListeners = mutableListOf<ArLoadedListener>()
    private val cameraUpdatedListeners = mutableListOf<CameraUpdatedListener>()
    private val transformationSystemListeners = mutableListOf<TransformationSystemListener>()
    private val planeTapListeners = mutableListOf<PlaneTapListener>()

    /**
     * Registers a listener that will be called when ARCore's scene is loaded first time or changed.
     * (Scene is changed e.g. when user exits the app with back button and opens it again)
     */
    fun addArSceneChangedListener(listener: ArSceneChangedListener) {
        arSceneListeners.add(listener)
    }

    fun removeArSceneChangedListener(listener: ArSceneChangedListener) {
        arSceneListeners.remove(listener)
    }

    /**
     * Registers a listener that will be called when ARCore's native library has been loaded.
     * (This is called each time the AR Fragment is recreated)
     * @see isArLoaded
     */
    fun addArLoadedListener(listener: ArLoadedListener) {
        arLoadedListeners.add(listener)
    }

    fun removeArLoadedListener(listener: ArLoadedListener) {
        arLoadedListeners.remove(listener)
    }

    /**
     * Registers a listener that will be called when camera position or state has changed.
     */
    fun addCameraUpdatedListener(listener: CameraUpdatedListener) {
        cameraUpdatedListeners.add(listener)
    }

    fun removeCameraUpdatedListener(listener: CameraUpdatedListener) {
        cameraUpdatedListeners.remove(listener)
    }

    /**
     * Registers a listener that will be called each time the new TransformationSystem is created.
     * (this happens when AR Fragment is recreated)
     */
    fun addTransformationSystemListener(listener: TransformationSystemListener) {
        transformationSystemListeners.add(listener)
    }

    fun removeTransformationSystemListener(listener: TransformationSystemListener) {
        transformationSystemListeners.remove(listener)
    }

    /**
     * Registers a plane tap event listener which is called after user taps a detected plane
     */
    fun addPlaneTapListener(listener: PlaneTapListener) {
        planeTapListeners.add(listener)
    }

    fun removePlaneTapListener(listener: PlaneTapListener) {
        planeTapListeners.remove(listener)
    }

    /**
     * Returns current ARCore's scene if already loaded or null otherwise
     */
    abstract fun getArScene(): Scene?

    /**
     * Returns true if ARCore native libraries are loaded (this is true after AR fragment
     * is created). Before ARCore is not loaded we cannot load a Renderable.
     */
    abstract fun isArLoaded(): Boolean

    /**
     * Return current ARCore session or null if not initialized yet
     */
    abstract fun getSession(): Session?

    /**
     * Returns current TransformationSystem
     */
    abstract fun getTransformationSystem(): TransformationSystem?

    /**
     * Returns current camera state or null if state has not been updated yet
     */
    abstract fun getCameraState(): TrackingState?

    /**
     * Returns true if plane detection mode is active.
     */
    abstract fun isPlaneDetectionEnabled(): Boolean

    protected fun notifySceneChanged(arScene: Scene) {
        arSceneListeners.forEach { it.onSceneChanged(arScene) }
    }

    protected fun notifyCameraUpdated(cameraPose: Pose, state: TrackingState) {
        cameraUpdatedListeners.forEach { it.onCameraUpdated(cameraPose, state) }
    }

    protected fun notifyTransformationSystemChanged(system: TransformationSystem) {
        transformationSystemListeners.forEach { it.onTransformationSystemChanged(system) }
    }

    protected fun notifyPlaneTapped(hitResult: HitResult) {
        planeTapListeners.forEach { it.onPlaneTap(hitResult) }
    }

    protected fun notifyArLoaded(firstTime: Boolean) {
        arLoadedListeners.forEach { it.onArLoaded(firstTime) }
    }

    interface CameraUpdatedListener {
        fun onCameraUpdated(cameraPose: Pose, state: TrackingState)
    }

    interface ArSceneChangedListener {
        fun onSceneChanged(arScene: Scene)
    }

    interface TransformationSystemListener {
        fun onTransformationSystemChanged(transformationSystem: TransformationSystem)
    }

    interface PlaneTapListener {
        fun onPlaneTap(hitResult: HitResult)
    }

    interface ArLoadedListener {
        fun onArLoaded(firstTime: Boolean)
    }
}