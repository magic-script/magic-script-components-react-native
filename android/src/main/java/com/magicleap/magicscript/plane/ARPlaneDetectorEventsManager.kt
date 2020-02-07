package com.magicleap.magicscript.plane

import com.facebook.react.bridge.WritableMap

interface ARPlaneDetectorEventsManager {
    fun onPlaneUpdatedEventReceived(planes: WritableMap)
    fun onPlaneDetectedEventReceived(planes: WritableMap)
    fun onPlaneRemovedEventReceived(planes: WritableMap)
    fun onPlaneTappedListener(planes: WritableMap)
}