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