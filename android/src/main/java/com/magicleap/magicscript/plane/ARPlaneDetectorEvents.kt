package com.magicleap.magicscript.plane

import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

class ARPlaneDetectorEvents(private val reactContext: ReactContext): ARPlaneDetectorEventsManager {
    override fun onPlaneDetectedEventReceived(planes: WritableMap) {
        sendEvent("onPlaneDetected", planes)
    }

    override fun onPlaneRemovedEventReceived(planes: WritableMap) {
        sendEvent("onPlaneRemoved", planes)
    }

    override fun onPlaneUpdatedEventReceived(planes: WritableMap) {
        sendEvent("onPlaneUpdated", planes)
    }

    override fun onPlaneTappedListener(planes: WritableMap) {
        sendEvent("onPlaneTapped", planes)
    }

    private fun sendEvent(eventName: String, payload: WritableMap) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, payload)
    }


}