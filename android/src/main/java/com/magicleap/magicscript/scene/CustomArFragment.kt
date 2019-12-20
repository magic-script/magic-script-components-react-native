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

package com.magicleap.magicscript.scene

import android.os.Bundle
import android.view.View
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ux.ArFragment

class CustomArFragment : ArFragment() {

    private var onReadyCalled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arSceneView.scene.addOnUpdateListener {
            if (!onReadyCalled && arSceneView.arFrame?.camera?.trackingState == TrackingState.TRACKING) {
                // We can add AR objects after session is ready and camera is in tracking mode
                UiNodesManager.INSTANCE.onArFragmentReady()
                onReadyCalled = true
            }
        }
        setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor = hitResult.createAnchor()
            UiNodesManager.INSTANCE.onTapArPlane(anchor)
        }

        // Hide the instructions
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
    }
}