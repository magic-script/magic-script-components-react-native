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

import com.google.ar.core.HitResult
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.TransformationSystem

object ArResourcesManager : ArResourcesProvider() {

    var planeDetection = false

    private var scene: Scene? = null
    private var session: Session? = null
    private var transformationSystem: TransformationSystem? = null
    private var cameraState: TrackingState? = null
    private var arLoaded = false

    /**
     * Scene is changed when fragment is recreated
     */
    fun setupScene(scene: Scene) {
        this.scene = scene
        notifySceneChanged(scene)
    }

    fun setupSession(session: Session) {
        this.session = session
    }

    fun setupTransformationSystem(transformationSystem: TransformationSystem) {
        this.transformationSystem = transformationSystem
        notifyTransformationSystemChanged(transformationSystem)
    }

    fun onArCoreLoaded() {
        arLoaded = true
        notifyArLoaded()
    }

    fun updateCameraPosition(position: Vector3, trackingState: TrackingState) {
        this.cameraState = trackingState
        notifyCameraUpdated(position, trackingState)
    }

    fun onPlaneTapped(hitResult: HitResult) {
        notifyPlaneTapped(hitResult)
    }

    override fun getSession(): Session? {
        return session
    }

    override fun getArScene(): Scene? {
        return scene
    }

    override fun isArLoaded(): Boolean {
        return arLoaded
    }

    override fun getTransformationSystem(): TransformationSystem? {
        return transformationSystem
    }

    override fun getCameraState(): TrackingState? {
        return cameraState
    }

    override fun isPlaneDetectionEnabled(): Boolean {
        return planeDetection
    }
}