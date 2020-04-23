/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

import android.os.Bundle
import android.view.View
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ux.ArFragment
import com.magicleap.magicscript.plane.ARPlaneDetectorBridge

class CustomArFragment : ArFragment() {

    private var onReadyCalled = false
    private var lastTimestamp: Long = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arResourcesManager = ArResourcesManager.INSTANCE
            ?: throw RuntimeException("ArResourcesManager not initialized")

        arResourcesManager.setupScene(arSceneView.scene)

        arSceneView.scene.camera.farClipPlane = FAR_CLIP_PLANE

        arSceneView.scene.addOnUpdateListener {
            arSceneView.arFrame?.camera?.let { camera ->
                if (!onReadyCalled && camera.trackingState == TrackingState.TRACKING) {
                    // We can add AR objects after session is ready and camera is in tracking mode
                    arSceneView.session?.let {
                        arResourcesManager.setupSession(it)
                    }
                    arResourcesManager.setupTransformationSystem(transformationSystem)
                    arResourcesManager.onArCoreLoaded()
                    onReadyCalled = true
                }

                arResourcesManager.onCameraUpdated(camera.pose, camera.trackingState)
            }
            if (onReadyCalled && ARPlaneDetectorBridge.INSTANCE.isDetecting()) {
                val newFrame = arSceneView.session?.update()
                if (newFrame != null && newFrame.timestamp != lastTimestamp) {
                    lastTimestamp = newFrame.timestamp
                    ARPlaneDetectorBridge.INSTANCE.onPlaneUpdate(newFrame.getUpdatedTrackables(Plane::class.java).toList())
                }
            }
        }

        setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            ARPlaneDetectorBridge.INSTANCE.onPlaneTapped(plane, hitResult)
            arResourcesManager.onPlaneTapped(hitResult)
        }

        // Hide the instructions
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
    }

    /**
     * Overridden to do nothing, because the default implementation of BaseArFragment
     * changes the app mode to full screen causing artifacts e.g. when displaying
     * overlay view on top of ArView in React
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        // no-up
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ArResourcesManager.INSTANCE?.clearArReferences()
    }

    companion object {
        const val FAR_CLIP_PLANE = 15f // in meters
    }
}