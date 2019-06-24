package com.reactlibrary.scene

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
                UiNodesManager.onArFragmentReady()
                onReadyCalled = true
            }
        }
    }

}